package com.package_of_documents.byrogozin

import java.math.BigInteger
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme
import org.apache.poi.xwpf.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Acts(
    navController: NavController,
    customerType: String,
    customer: String = "Заказчик",
    performer: String = "Исполнитель",
    contractNumber: String = "",
    contractDate: String = "",
    serviceName: String = AgreementData.serviceName,
    quantity: String = "",
    cost: String = "",
    applications: List<ApplicationForm> = emptyList()
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    val acts = listOf(
        "Разработка исполнителя",
        "по форме 1С",
        "УПД (если НДС статус 1, если без НДС статус 2)"
    )

    // Функция для загрузки шаблона из assets
    fun loadTemplateFromAssets(fileName: String): XWPFDocument? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                XWPFDocument(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка загрузки шаблона: $fileName", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun formatFIO(fullName: String): String {
        val parts = fullName.split(" ").filter { it.isNotBlank() }
        return when (parts.size) {
            3 -> "${parts[0]} ${parts[1].first()}.${parts[2].first()}."
            2 -> "${parts[0]} ${parts[1].first()}."
            else -> fullName
        }
    }

    // Добавленные состояния для выбора статуса НДС
    var showVatStatusDialog by remember { mutableStateOf(false) }
    var selectedVatStatus by remember { mutableStateOf(1) }

    var editableQuantity by remember { mutableStateOf(quantity) }
    var editableCost by remember { mutableStateOf(cost) }

    var showQuantityTooltip by remember { mutableStateOf(false) }
    var editableContractNumber by remember { mutableStateOf(contractNumber) }
    var editableContractDate by remember { mutableStateOf(contractDate) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    var customerSigner by remember {
        mutableStateOf(
            when (customerType) {
                "juridical" -> formatFIO(AccountData1.fullName)
                "physical" -> formatFIO(AccountData2.fioText)
                else -> ""
            }
        )
    }

    var performerSigner by remember {
        mutableStateOf(formatFIO(AccountData.fullName))
    }
    val customerDetails = remember(customerType) {
        when (customerType) {
            "juridical" -> {
                """
                Организация: ${AccountData1.organizationName}
                Юридический адрес: ${AccountData1.legalAddress}
                ИНН: ${AccountData1.inn} КПП: ${AccountData1.kpp}
                Расчетный счет: ${AccountData1.accountNumber}
                К/счет: ${AccountData1.correspondentAccount}
                БИК: ${AccountData1.bic}
                Наименование банка: ${AccountData1.bankName}
                """.trimIndent()
            }
            "physical" -> {
                if (AccountData2.selectedOption == "ФИО + Паспорт") {
                    """
                    ФИО: ${AccountData2.fioText}
                    Паспорт: ${AccountData2.passportSeries} ${AccountData2.passportNumber}
                    Выдан: ${AccountData2.issuedBy}
                    Дата: ${AccountData2.issueDate}
                    Код подразделения: ${AccountData2.departmentCode}
                    """.trimIndent()
                } else {
                    "ФИО: ${AccountData2.fioText}"
                }
            }
            else -> "Данные заказчика не указаны"
        }
    }

    val executorDetails = remember {
        """   
        Организация: ${AccountData.organizationName}
        Юридический адрес: ${AccountData.legalAddress}
        ИНН: ${AccountData.inn} КПП: ${AccountData.kpp}
        Расчетный счет: ${AccountData.accountNumber}
        К/счет: ${AccountData.correspondentAccount}
        БИК: ${AccountData.bic}
        Наименование банка: ${AccountData.bankName}
        """.trimIndent()
    }

    fun saveAgreementToWord(): XWPFDocument {
        val document = XWPFDocument()

        // Заголовок договора
        val title = document.createParagraph()
        title.alignment = ParagraphAlignment.CENTER
        val titleRun = title.createRun()
        titleRun.setText("ДОГОВОР №${contractNumber}")
        titleRun.setBold(true)
        titleRun.fontSize = 16

        document.createParagraph().createRun().addBreak()

        // Основные данные договора
        val basicInfo = document.createParagraph()
        basicInfo.alignment = ParagraphAlignment.LEFT
        basicInfo.createRun().setText("Дата: $contractDate")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("Наименование услуги: $serviceName")

        // Стороны договора
        val partiesTitle = document.createParagraph()
        partiesTitle.alignment = ParagraphAlignment.LEFT
        partiesTitle.createRun().setText("ЗАКАЗЧИК:")
        partiesTitle.createRun().addBreak()

        val customerDetailsPara = document.createParagraph()
        customerDetailsPara.alignment = ParagraphAlignment.LEFT
        customerDetailsPara.createRun().setText(customerDetails)

        document.createParagraph().createRun().addBreak()

        val executorTitle = document.createParagraph()
        executorTitle.alignment = ParagraphAlignment.LEFT
        executorTitle.createRun().setText("ИСПОЛНИТЕЛЬ:")
        executorTitle.createRun().addBreak()

        val executorDetailsPara = document.createParagraph()
        executorDetailsPara.alignment = ParagraphAlignment.LEFT
        executorDetailsPara.createRun().setText(executorDetails)

        // Все пункты договора
        val sections = listOf(
            "1. Предмет договора",
            "2. Обязанности сторон",
            "3. Порядок оказания услуг",
            "4. Стоимость и порядок расчетов",
            "5. Сроки выполнения",
            "6. Ответственность сторон",
            "7. Порядок разрешения споров",
            "8. Форс-мажор",
            "9. Изменение и расторжение договора",
            "10. Другие условия"
        )

        sections.forEach { section ->
            val sectionPara = document.createParagraph()
            sectionPara.alignment = ParagraphAlignment.LEFT
            sectionPara.createRun().setText(section)
            sectionPara.createRun().addBreak()

            // Добавляем пустое место для текста раздела
            val contentPara = document.createParagraph()
            contentPara.alignment = ParagraphAlignment.LEFT
            contentPara.createRun().setText("[Текст раздела]")
            contentPara.createRun().addBreak()
        }

        // Особый формат для раздела о стоимости
        val paymentSection = document.createParagraph()
        paymentSection.alignment = ParagraphAlignment.LEFT
        paymentSection.createRun().setText("4. Стоимость и порядок расчетов")
        paymentSection.createRun().addBreak()
        paymentSection.createRun().setText("Порядок расчетов: ${AgreementData.selectedOption}")

        if (AgreementData.selectedOption == "аванс (100%/частично)") {
            paymentSection.createRun().addBreak()
            paymentSection.createRun().setText("Размер аванса: ${AgreementData.selectedAdvanceOption}")
        }

        // Реквизиты и подписи
        val detailsTitle = document.createParagraph()
        detailsTitle.alignment = ParagraphAlignment.LEFT
        detailsTitle.createRun().setText("11. Реквизиты сторон и подписи")
        detailsTitle.createRun().addBreak()

        // Реквизиты в таблице
        val table = document.createTable(1, 2)
        table.setWidth("100%")

        // Ячейка заказчика
        val customerCell = table.getRow(0).getCell(0)
        customerCell.text = "Заказчик:\n$customerDetails"

        // Ячейка исполнителя
        val executorCell = table.getRow(0).getCell(1)
        executorCell.text = "Исполнитель:\n$executorDetails"

        // Подписи сторон
        val signatures = document.createParagraph()
        signatures.alignment = ParagraphAlignment.LEFT
        signatures.createRun().setText("\nПОДПИСИ СТОРОН:\n")

        val customerSign = document.createParagraph()
        customerSign.alignment = ParagraphAlignment.LEFT
        customerSign.createRun().setText("Заказчик: ___________________ (${AgreementData.customerFIO})")

        val performerSign = document.createParagraph()
        performerSign.alignment = ParagraphAlignment.LEFT
        performerSign.createRun().setText("Исполнитель: ___________________ (${AgreementData.executorFIO})")

        return document
    }

    fun saveAdditionalAgreementToWord(): XWPFDocument {
        val document = XWPFDocument()

        // Заголовок дополнительного соглашения
        val title = document.createParagraph()
        title.alignment = ParagraphAlignment.CENTER
        val titleRun = title.createRun()
        titleRun.setText("ДОПОЛНИТЕЛЬНОЕ СОГЛАШЕНИЕ №${AddAgreementData.agreementNumber}")
        titleRun.setBold(true)
        titleRun.fontSize = 16

        document.createParagraph().createRun().addBreak()

        // Основные данные соглашения
        val basicInfo = document.createParagraph()
        basicInfo.alignment = ParagraphAlignment.LEFT
        basicInfo.createRun().setText("Дата: ${AddAgreementData.agreementDate}")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("к Договору №${AddAgreementData.contractNumber} от ${AddAgreementData.contractDate}")

        // Стороны соглашения
        val partiesTitle = document.createParagraph()
        partiesTitle.alignment = ParagraphAlignment.LEFT
        partiesTitle.createRun().setText("ЗАКАЗЧИК:")
        partiesTitle.createRun().addBreak()

        val customerDetailsPara = document.createParagraph()
        customerDetailsPara.alignment = ParagraphAlignment.LEFT
        customerDetailsPara.createRun().setText(customerDetails)

        document.createParagraph().createRun().addBreak()

        val executorTitle = document.createParagraph()
        executorTitle.alignment = ParagraphAlignment.LEFT
        executorTitle.createRun().setText("ИСПОЛНИТЕЛЬ:")
        executorTitle.createRun().addBreak()

        val executorDetailsPara = document.createParagraph()
        executorDetailsPara.alignment = ParagraphAlignment.LEFT
        executorDetailsPara.createRun().setText(executorDetails)

        // Предмет соглашения
        val subjectTitle = document.createParagraph()
        subjectTitle.alignment = ParagraphAlignment.LEFT
        subjectTitle.createRun().setText("Предмет соглашения:")
        subjectTitle.createRun().addBreak()

        // Пункты соглашения
        AddAgreementData.agreementItems.forEachIndexed { index, item ->
            if (item.isNotEmpty()) {
                val itemPara = document.createParagraph()
                itemPara.alignment = ParagraphAlignment.LEFT
                itemPara.createRun().setText("${index + 1}) $item")
                itemPara.createRun().addBreak()
            }
        }

        // Реквизиты и подписи
        val detailsTitle = document.createParagraph()
        detailsTitle.alignment = ParagraphAlignment.LEFT
        detailsTitle.createRun().setText("Реквизиты сторон и подписи")
        detailsTitle.createRun().addBreak()

        // Подписи сторон
        val signatures = document.createParagraph()
        signatures.alignment = ParagraphAlignment.LEFT
        signatures.createRun().setText("\nПОДПИСИ СТОРОН:\n")

        val customerSign = document.createParagraph()
        customerSign.alignment = ParagraphAlignment.LEFT
        customerSign.createRun().setText("Заказчик: ___________________ (${AddAgreementData.customerName})")

        val performerSign = document.createParagraph()
        performerSign.alignment = ParagraphAlignment.LEFT
        performerSign.createRun().setText("Исполнитель: ___________________ (${AddAgreementData.executorName})")

        return document
    }
    fun calculateVAT(cost: String): String {
        return try {
            val amount = cost.toDouble()
            String.format("%.2f", amount * 0.2)
        } catch (e: Exception) {
            "0.00"
        }
    }

    fun amountInWords(amount: Double): String {
        val rubles = amount.toInt()
        val kopecks = ((amount - rubles) * 100).toInt()

        val rublesStr = when (rubles % 100) {
            in 11..19 -> "$rubles рублей"
            else -> when (rubles % 10) {
                1 -> "$rubles рубль"
                in 2..4 -> "$rubles рубля"
                else -> "$rubles рублей"
            }
        }

        val kopecksStr = when (kopecks % 100) {
            in 11..19 -> "$kopecks копеек"
            else -> when (kopecks % 10) {
                1 -> "$kopecks копейка"
                in 2..4 -> "$kopecks копейки"
                else -> "$kopecks копеек"
            }
        }

        return "$rublesStr $kopecksStr"
    }


    fun saveUPDActToWord(vatStatus: Int): XWPFDocument {
        val document = XWPFDocument()
        val currentDate = SimpleDateFormat("dd.MM.yyyy").format(Date())
        val currentYear = SimpleDateFormat("yyyy").format(Date())
        val currentDay = SimpleDateFormat("dd").format(Date())
        val currentMonth = SimpleDateFormat("MMMM").format(Date())

        val fontFamily = "Times New Roman"

        // 1. Заголовок документа
        val title = document.createParagraph()
        title.alignment = ParagraphAlignment.CENTER
        val titleRun = title.createRun()
        titleRun.setText("Универсальный передаточный документ")
        titleRun.setBold(true)
        titleRun.fontSize = 14
        titleRun.fontFamily = fontFamily

        // 2. Приложение к постановлению
        val appendix = document.createParagraph()
        appendix.alignment = ParagraphAlignment.CENTER
        val appendixRun = appendix.createRun()
        appendixRun.setText("Приложение № 1")
        appendixRun.fontFamily = fontFamily
        appendixRun.fontSize = 10

        val regulation = document.createParagraph()
        regulation.alignment = ParagraphAlignment.CENTER
        val regulationRun = regulation.createRun()
        regulationRun.setText("к постановлению Правительства Российской Федерации")
        regulationRun.fontFamily = fontFamily
        regulationRun.fontSize = 10

        val regulationDate = document.createParagraph()
        regulationDate.alignment = ParagraphAlignment.CENTER
        val regulationDateRun = regulationDate.createRun()
        regulationDateRun.setText("от 26 декабря 2011 г. № 1137")
        regulationDateRun.fontFamily = fontFamily
        regulationDateRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 3. Блок статуса и счета-фактуры
        val statusTable = document.createTable(1, 2)
        statusTable.setWidth("100%")

        // Настройка ширины колонок
        statusTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(2000) // 20% ширины
            addNewGridCol().w = BigInteger.valueOf(8000) // 80% ширины
        }

        val statusCell = statusTable.getRow(0).getCell(0)
        statusCell.text = "Статус: $vatStatus"
        statusCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

        val invoiceCell = statusTable.getRow(0).getCell(1)
        if (vatStatus == 1) {
            invoiceCell.text = "Счет-фактура № __________ от __________ 20__ г. (1)"
        } else {
            invoiceCell.text = "Документ об отгрузке"
        }
        invoiceCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 4. Информация о продавце
        val sellerTitle = document.createParagraph()
        sellerTitle.alignment = ParagraphAlignment.LEFT
        val sellerTitleRun = sellerTitle.createRun()
        sellerTitleRun.setText("Продавец:")
        sellerTitleRun.setFontFamily("Times New Roman")
        sellerTitleRun.fontSize = 10

        val sellerInfo = document.createParagraph()
        sellerInfo.alignment = ParagraphAlignment.LEFT
        val sellerInfoRun = sellerInfo.createRun()
        sellerInfoRun.setText("${AccountData.organizationName} (2)")
        sellerInfoRun.setFontFamily("Times New Roman")
        sellerInfoRun.fontSize = 10

        val sellerAddressTitle = document.createParagraph()
        sellerAddressTitle.alignment = ParagraphAlignment.LEFT
        val sellerAddressTitleRun = sellerAddressTitle.createRun()
        sellerAddressTitleRun.setText("Адрес:")
        sellerAddressTitleRun.setFontFamily("Times New Roman")
        sellerAddressTitleRun.fontSize = 10

        val sellerAddress = document.createParagraph()
        sellerAddress.alignment = ParagraphAlignment.LEFT
        val sellerAddressRun = sellerAddress.createRun()
        sellerAddressRun.setText("${AccountData.legalAddress} (2а)")
        sellerAddressRun.setFontFamily("Times New Roman")
        sellerAddressRun.fontSize = 10

        val sellerInnTitle = document.createParagraph()
        sellerInnTitle.alignment = ParagraphAlignment.LEFT
        val sellerInnTitleRun = sellerInnTitle.createRun()
        sellerInnTitleRun.setText("ИНН/КПП продавца:")
        sellerInnTitleRun.setFontFamily("Times New Roman")
        sellerInnTitleRun.fontSize = 10

        val sellerInn = document.createParagraph()
        sellerInn.alignment = ParagraphAlignment.LEFT
        val sellerInnRun = sellerInn.createRun()
        sellerInnRun.setText("${AccountData.inn}/${AccountData.kpp} (2б)")
        sellerInnRun.setFontFamily("Times New Roman")
        sellerInnRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 5. Информация о грузоотправителе
        val shipperTitle = document.createParagraph()
        shipperTitle.alignment = ParagraphAlignment.LEFT
        val shipperTitleRun = shipperTitle.createRun()
        shipperTitleRun.setText("Грузоотправитель и его адрес:")
        shipperTitleRun.setFontFamily("Times New Roman")
        shipperTitleRun.fontSize = 10

        val shipperInfo = document.createParagraph()
        shipperInfo.alignment = ParagraphAlignment.LEFT
        val shipperInfoRun = shipperInfo.createRun()
        shipperInfoRun.setText("${AccountData.organizationName} (3)")
        shipperInfoRun.setFontFamily("Times New Roman")
        shipperInfoRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 6. Информация о грузополучателе
        val customerName = if (customerType == "juridical") AccountData1.organizationName else AccountData2.fioText
        val customerAddress = if (customerType == "juridical") AccountData1.legalAddress else ""

        val consigneeTitle = document.createParagraph()
        consigneeTitle.alignment = ParagraphAlignment.LEFT
        val consigneeTitleRun = consigneeTitle.createRun()
        consigneeTitleRun.setText("Грузополучатель и его адрес:")
        consigneeTitleRun.setFontFamily("Times New Roman")
        consigneeTitleRun.fontSize = 10

        val consigneeInfo = document.createParagraph()
        consigneeInfo.alignment = ParagraphAlignment.LEFT
        val consigneeInfoRun = consigneeInfo.createRun()
        consigneeInfoRun.setText("$customerName, $customerAddress (4)")
        consigneeInfoRun.setFontFamily("Times New Roman")
        consigneeInfoRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 7. Платежный документ (только для статуса 1)
        if (vatStatus == 1) {
            val paymentDoc = document.createParagraph()
            paymentDoc.alignment = ParagraphAlignment.LEFT
            val paymentDocRun = paymentDoc.createRun()
            paymentDocRun.setText("К платежно-расчетному документу № ______ от ______ 20__ г. (5)")
            paymentDocRun.setFontFamily("Times New Roman")
            paymentDocRun.fontSize = 10

            // Пустая строка
            document.createParagraph().createRun().addBreak()
        }

        // 8. Информация о покупателе
        val buyerTitle = document.createParagraph()
        buyerTitle.alignment = ParagraphAlignment.LEFT
        val buyerTitleRun = buyerTitle.createRun()
        buyerTitleRun.setText("Покупатель:")
        buyerTitleRun.setFontFamily("Times New Roman")
        buyerTitleRun.fontSize = 10

        val buyerInfo = document.createParagraph()
        buyerInfo.alignment = ParagraphAlignment.LEFT
        val buyerInfoRun = buyerInfo.createRun()
        buyerInfoRun.setText("$customerName (6)")
        buyerInfoRun.setFontFamily("Times New Roman")
        buyerInfoRun.fontSize = 10

        val buyerAddressTitle = document.createParagraph()
        buyerAddressTitle.alignment = ParagraphAlignment.LEFT
        val buyerAddressTitleRun = buyerAddressTitle.createRun()
        buyerAddressTitleRun.setText("Адрес:")
        buyerAddressTitleRun.setFontFamily("Times New Roman")
        buyerAddressTitleRun.fontSize = 10

        val buyerAddress = document.createParagraph()
        buyerAddress.alignment = ParagraphAlignment.LEFT
        val buyerAddressRun = buyerAddress.createRun()
        buyerAddressRun.setText("$customerAddress (6а)")
        buyerAddressRun.setFontFamily("Times New Roman")
        buyerAddressRun.fontSize = 10

        val buyerInnTitle = document.createParagraph()
        buyerInnTitle.alignment = ParagraphAlignment.LEFT
        val buyerInnTitleRun = buyerInnTitle.createRun()
        buyerInnTitleRun.setText("ИНН/КПП покупателя:")
        buyerInnTitleRun.setFontFamily("Times New Roman")
        buyerInnTitleRun.fontSize = 10

        val buyerInn = document.createParagraph()
        buyerInn.alignment = ParagraphAlignment.LEFT
        val buyerInnRun = buyerInn.createRun()
        buyerInnRun.setText(if (customerType == "juridical") "${AccountData1.inn}/${AccountData1.kpp} (6б)" else "- (6б)")
        buyerInnRun.setFontFamily("Times New Roman")
        buyerInnRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 9. Валюта
        val currencyTitle = document.createParagraph()
        currencyTitle.alignment = ParagraphAlignment.LEFT
        val currencyTitleRun = currencyTitle.createRun()
        currencyTitleRun.setText("Валюта: наименование, код")
        currencyTitleRun.setFontFamily("Times New Roman")
        currencyTitleRun.fontSize = 10

        val currencyInfo = document.createParagraph()
        currencyInfo.alignment = ParagraphAlignment.LEFT
        val currencyInfoRun = currencyInfo.createRun()
        currencyInfoRun.setText("Российский рубль, 643 (7)")
        currencyInfoRun.setFontFamily("Times New Roman")
        currencyInfoRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 10. Таблица товаров/услуг
        val goodsTable = document.createTable(3, 11)
        goodsTable.setWidth("100%")

        // Настройка ширины колонок
        goodsTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(500)  // № п/п
            addNewGridCol().w = BigInteger.valueOf(1000) // Код товара
            addNewGridCol().w = BigInteger.valueOf(3000) // Наименование
            addNewGridCol().w = BigInteger.valueOf(800)  // Ед. изм.
            addNewGridCol().w = BigInteger.valueOf(800)  // Код по ОКЕИ
            addNewGridCol().w = BigInteger.valueOf(800)  // Кол-во
            addNewGridCol().w = BigInteger.valueOf(1000) // Цена
            addNewGridCol().w = BigInteger.valueOf(1500) // Стоимость без налога
            addNewGridCol().w = BigInteger.valueOf(800)  // Сумма акциза
            addNewGridCol().w = BigInteger.valueOf(800)  // Налоговая ставка
            addNewGridCol().w = BigInteger.valueOf(1500) // Сумма налога
        }

        // Заголовки таблицы
        val headerRow = goodsTable.getRow(0)
        headerRow.getCell(0).text = "№ п/п"
        headerRow.getCell(1).text = "Код товара"
        headerRow.getCell(2).text = "Наименование товара (описание выполненных работ, оказанных услуг)"
        headerRow.getCell(3).text = "Ед. изм."
        headerRow.getCell(4).text = "Код по ОКЕИ"
        headerRow.getCell(5).text = "Кол-во"
        headerRow.getCell(6).text = "Цена"
        headerRow.getCell(7).text = "Стоимость без налога"
        headerRow.getCell(8).text = "Сумма акциза"
        headerRow.getCell(9).text = "Налоговая ставка"
        headerRow.getCell(10).text = "Сумма налога"

        // Вторая строка заголовков (буквенные обозначения)
        val subHeaderRow = goodsTable.getRow(1)
        subHeaderRow.getCell(0).text = "1"
        subHeaderRow.getCell(1).text = "2"
        subHeaderRow.getCell(2).text = "3"
        subHeaderRow.getCell(3).text = "4"
        subHeaderRow.getCell(4).text = "5"
        subHeaderRow.getCell(5).text = "6"
        subHeaderRow.getCell(6).text = "7"
        subHeaderRow.getCell(7).text = "8"
        subHeaderRow.getCell(8).text = "9"
        subHeaderRow.getCell(9).text = "10"
        subHeaderRow.getCell(10).text = "11"

        // Данные товара/услуги
        val dataRow = goodsTable.getRow(2)
        dataRow.getCell(0).text = "1"
        dataRow.getCell(1).text = "-"
        dataRow.getCell(2).text = serviceName
        dataRow.getCell(3).text = "-"
        dataRow.getCell(4).text = "-"
        dataRow.getCell(5).text = editableQuantity.ifEmpty { "1" }
        dataRow.getCell(6).text = editableCost
        dataRow.getCell(7).text = editableCost
        dataRow.getCell(8).text = "0"
        dataRow.getCell(9).text = if (vatStatus == 1) "20%" else "Без НДС"
        dataRow.getCell(10).text = if (vatStatus == 1) calculateVAT(editableCost) else "0"

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 11. Итоговая строка
        val totalTable = document.createTable(1, 11)
        totalTable.setWidth("100%")
        totalTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(500)
            addNewGridCol().w = BigInteger.valueOf(1000)
            addNewGridCol().w = BigInteger.valueOf(3000)
            addNewGridCol().w = BigInteger.valueOf(800)
            addNewGridCol().w = BigInteger.valueOf(800)
            addNewGridCol().w = BigInteger.valueOf(800)
            addNewGridCol().w = BigInteger.valueOf(1000)
            addNewGridCol().w = BigInteger.valueOf(1500)
            addNewGridCol().w = BigInteger.valueOf(800)
            addNewGridCol().w = BigInteger.valueOf(800)
            addNewGridCol().w = BigInteger.valueOf(1500)
        }

        val totalRow = totalTable.getRow(0)
        totalRow.getCell(2).text = "Всего к оплате"
        totalRow.getCell(7).text = editableCost
        totalRow.getCell(8).text = "0"
        totalRow.getCell(9).text = "X"
        totalRow.getCell(10).text = if (vatStatus == 1) calculateVAT(editableCost) else "0"

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 12. Информация о количестве листов
        val sheetsInfo = document.createParagraph()
        sheetsInfo.alignment = ParagraphAlignment.LEFT
        val sheetsInfoRun = sheetsInfo.createRun()
        sheetsInfoRun.setText("Документ составлен на 1 листе")
        sheetsInfoRun.setFontFamily("Times New Roman")
        sheetsInfoRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 13. Подписи
        val signTable = document.createTable(3, 3)
        signTable.setWidth("100%")
        signTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(4000)
            addNewGridCol().w = BigInteger.valueOf(2000)
            addNewGridCol().w = BigInteger.valueOf(4000)
        }

        // Руководитель
        val signRow1 = signTable.getRow(0)
        signRow1.getCell(0).text = "Руководитель организации или иное уполномоченное лицо"
        signRow1.getCell(1).text = "(подпись)"
        signRow1.getCell(2).text = "($performerSigner)"

        // Главный бухгалтер
        val signRow2 = signTable.getRow(1)
        signRow2.getCell(0).text = "Главный бухгалтер или иное уполномоченное лицо"
        signRow2.getCell(1).text = "(подпись)"
        signRow2.getCell(2).text = "($performerSigner)"

        // Индивидуальный предприниматель
        val signRow3 = signTable.getRow(2)
        signRow3.getCell(0).text = "Индивидуальный предприниматель или иное уполномоченное лицо"
        signRow3.getCell(1).text = "(подпись)"
        signRow3.getCell(2).text = "($performerSigner)"

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 14. Основание передачи
        val transferBasis = document.createParagraph()
        transferBasis.alignment = ParagraphAlignment.LEFT
        val transferBasisRun = transferBasis.createRun()
        transferBasisRun.setText("Основание передачи (сдачи) / получения (приемки) [8]")
        transferBasisRun.setFontFamily("Times New Roman")
        transferBasisRun.fontSize = 10

        val transferDetails = document.createParagraph()
        transferDetails.alignment = ParagraphAlignment.LEFT
        val transferDetailsRun = transferDetails.createRun()
        transferDetailsRun.setText("(договор; доверенность и др.)")
        transferDetailsRun.setFontFamily("Times New Roman")
        transferDetailsRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 15. Данные о транспортировке
        val transportInfo = document.createParagraph()
        transportInfo.alignment = ParagraphAlignment.LEFT
        val transportInfoRun = transportInfo.createRun()
        transportInfoRun.setText("Данные о транспортировке и грузе [9]")
        transportInfoRun.setFontFamily("Times New Roman")
        transportInfoRun.fontSize = 10

        val transportDetails = document.createParagraph()
        transportDetails.alignment = ParagraphAlignment.LEFT
        val transportDetailsRun = transportDetails.createRun()
        transportDetailsRun.setText("(транспортная накладная, поручение экспедитору, экспедиторская / складская расписка и др. / масса нетто/ брутто груза, если не приведены ссылки на транспортные документы, содержащие эти сведения)")
        transportDetailsRun.setFontFamily("Times New Roman")
        transportDetailsRun.fontSize = 10

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 16. Подписи передачи/приемки
        val transferSignTable = document.createTable(1, 2)
        transferSignTable.setWidth("100%")
        transferSignTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val transferSignRow = transferSignTable.getRow(0)
        transferSignRow.getCell(0).text = "Товар (груз) передал / услуги, результаты работ, права сдал"
        transferSignRow.getCell(1).text = "Товар (груз) получил / услуги, результаты работ, права принял"

        val transferSignDetailsTable = document.createTable(1, 2)
        transferSignDetailsTable.setWidth("100%")
        transferSignDetailsTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val transferSignDetailsRow = transferSignDetailsTable.getRow(0)
        transferSignDetailsRow.getCell(0).text = "(должность) (подпись) ($performerSigner)"
        transferSignDetailsRow.getCell(1).text = "(должность) (подпись) ($customerSigner)"

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 17. Даты
        val datesTable = document.createTable(1, 2)
        datesTable.setWidth("100%")
        datesTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val datesRow = datesTable.getRow(0)
        datesRow.getCell(0).text = "Дата отгрузки, передачи (сдачи)"
        datesRow.getCell(1).text = "Дата получения (приемки)"

        val datesValuesTable = document.createTable(1, 2)
        datesValuesTable.setWidth("100%")
        datesValuesTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val datesValuesRow = datesValuesTable.getRow(0)
        datesValuesRow.getCell(0).text = "«$currentDay» $currentMonth 20$currentYear г."
        datesValuesRow.getCell(1).text = "«$currentDay» $currentMonth 20$currentYear г."

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 18. Ответственные лица
        val responsibleTable = document.createTable(1, 2)
        responsibleTable.setWidth("100%")
        responsibleTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val responsibleRow = responsibleTable.getRow(0)
        responsibleRow.getCell(0).text = "Ответственный за правильность оформления факта хозяйственной жизни [14]"
        responsibleRow.getCell(1).text = "Ответственный за правильность оформления факта хозяйственной жизни"

        val responsibleDetailsTable = document.createTable(1, 2)
        responsibleDetailsTable.setWidth("100%")
        responsibleDetailsTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val responsibleDetailsRow = responsibleDetailsTable.getRow(0)
        responsibleDetailsRow.getCell(0).text = "(должность) (подпись) ($performerSigner)"
        responsibleDetailsRow.getCell(1).text = "(должность) (подпись) ($customerSigner)"

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // 19. Место для печати
        val printTable = document.createTable(1, 2)
        printTable.setWidth("100%")
        printTable.ctTbl.addNewTblGrid().apply {
            addNewGridCol().w = BigInteger.valueOf(5000)
            addNewGridCol().w = BigInteger.valueOf(5000)
        }

        val printRow = printTable.getRow(0)
        printRow.getCell(0).text = "М.П."
        printRow.getCell(1).text = "М.П."

        return document
    }

    fun save1CActToWord(): XWPFDocument {
        val document = XWPFDocument()

        // Создаем основной параграф для заголовка
        val title = document.createParagraph()
        title.alignment = ParagraphAlignment.CENTER
        val titleRun = title.createRun()
        titleRun.setText("Акт № от ${SimpleDateFormat("dd.MM.yyyy").format(Date())}")
        titleRun.setBold(true)
        titleRun.fontSize = 14

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // Информация об исполнителе
        val executorPara = document.createParagraph()
        executorPara.alignment = ParagraphAlignment.LEFT
        executorPara.createRun().setText("Исполнитель: ${AccountData.organizationName}")
        executorPara.createRun().addBreak()
        executorPara.createRun().setText("Адрес: ${AccountData.legalAddress}")
        executorPara.createRun().addBreak()
        executorPara.createRun().setText("ИНН: ${AccountData.inn} КПП: ${AccountData.kpp}")

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // Информация о заказчике
        val customerPara = document.createParagraph()
        customerPara.alignment = ParagraphAlignment.LEFT
        customerPara.createRun().setText("Заказчик: ${if (customerType == "juridical") AccountData1.organizationName else AccountData2.fioText}")
        customerPara.createRun().addBreak()
        customerPara.createRun().setText("Адрес: ${if (customerType == "juridical") AccountData1.legalAddress else ""}")
        if (customerType == "juridical") {
            customerPara.createRun().addBreak()
            customerPara.createRun().setText("ИНН: ${AccountData1.inn} КПП: ${AccountData1.kpp}")
        }

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // Таблица с услугами
        val table = document.createTable(3, 5)
        table.setWidth("100%")

        // Заголовки таблицы
        val headerRow = table.getRow(0)
        headerRow.getCell(0).setText("№")
        headerRow.getCell(1).setText("Наименование работ, услуг")
        headerRow.getCell(2).setText("Кол-во")
        headerRow.getCell(3).setText("Ед.")
        headerRow.getCell(4).setText("Сумма")

        // Данные услуги
        val dataRow = table.getRow(1)
        dataRow.getCell(0).setText("1")
        dataRow.getCell(1).setText(serviceName)
        dataRow.getCell(2).setText(editableQuantity.ifEmpty { "1" })
        dataRow.getCell(3).setText("шт")
        dataRow.getCell(4).setText(editableCost)

        // Итоговая строка
        val totalRow = table.getRow(2)
        totalRow.getCell(3).setText("Итого:")
        totalRow.getCell(4).setText(editableCost)

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // Текст о выполнении услуг
        val completionText = document.createParagraph()
        completionText.alignment = ParagraphAlignment.LEFT
        completionText.createRun().setText("Вышеперечисленные услуги выполнены полностью и в срок. Заказчик претензий по объему, качеству и срокам оказания услуг не имеет.")

        // Пустая строка
        document.createParagraph().createRun().addBreak()

        // Подписи сторон
        val signatures = document.createParagraph()
        signatures.alignment = ParagraphAlignment.LEFT

        val signaturesTable = document.createTable(1, 2)
        signaturesTable.setWidth("100%")

        val signaturesRow = signaturesTable.getRow(0)
        signaturesRow.getCell(0).setText("ИСПОЛНИТЕЛЬ:\n\n___________________\n($performerSigner)")
        signaturesRow.getCell(1).setText("ЗАКАЗЧИК:\n\n___________________\n($customerSigner)")

        return document
    }

    fun saveStandardActToWord(): XWPFDocument {
        val document = XWPFDocument() // Создаем пустой документ вместо загрузки шаблона
        val currentDate = SimpleDateFormat("dd.MM.yyyy").format(Date())

        // Создаем базовую структуру акта
        val title = document.createParagraph()
        title.alignment = ParagraphAlignment.CENTER
        val titleRun = title.createRun()
        titleRun.setText("АКТ №${contractNumber}")
        titleRun.setBold(true)
        titleRun.fontSize = 16

        document.createParagraph().createRun().addBreak()

        // Основные данные акта
        val basicInfo = document.createParagraph()
        basicInfo.alignment = ParagraphAlignment.LEFT
        basicInfo.createRun().setText("Дата: $currentDate")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("Основание: Договор №$contractNumber от $contractDate")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("Наименование услуги: $serviceName")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("Количество: ${editableQuantity.ifEmpty { "1" }}")
        basicInfo.createRun().addBreak()
        basicInfo.createRun().setText("Стоимость: $editableCost")

        // Подписи сторон
        document.createParagraph().createRun().addBreak()
        val signatures = document.createParagraph()
        signatures.alignment = ParagraphAlignment.LEFT
        signatures.createRun().setText("\nПОДПИСИ СТОРОН:\n")

        val customerSign = document.createParagraph()
        customerSign.alignment = ParagraphAlignment.LEFT
        customerSign.createRun().setText("Заказчик: ___________________ ($customerSigner)")

        val performerSign = document.createParagraph()
        performerSign.alignment = ParagraphAlignment.LEFT
        performerSign.createRun().setText("Исполнитель: ___________________ ($performerSigner)")

        return document
    }

    fun saveDocumentToWord() {
        try {
            // Проверяем, что выбран тип документа
            if (selectedOption.isEmpty()) {
                Toast.makeText(context, "Выберите тип закрывающего документа", Toast.LENGTH_SHORT).show()
                return
            }
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val currentDateFormatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            // Сохраняем акт в базу данных
            val actId = dbHelper.saveAct(
                actType = selectedOption,
                actNumber = "Акт_$currentDate",
                actDate = currentDateFormatted,
                contractNumber = editableContractNumber,
                contractDate = editableContractDate,
                serviceName = serviceName,
                quantity = editableQuantity,
                cost = editableCost,
                customerFIO = customerSigner,
                executorFIO = performerSigner,
                vatStatus = if (selectedOption.startsWith("УПД")) selectedVatStatus else null,
                contractorId = 1
            )

            if (actId == -1L) {
                Toast.makeText(context, "Ошибка при сохранении акта в базу данных", Toast.LENGTH_SHORT).show()
                return
            }

            val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            if (!docsDir.exists()) {
                docsDir.mkdirs() // Создаем папку, если её нет
            }

            val (actDocument, actFileName) = when {
                selectedOption.startsWith("УПД") -> {
                    val status = selectedOption.substringAfter("статус ").substringBefore(")").toInt()
                    val doc = saveUPDActToWord(status)
                    val fileName = "УПД_статус${status}_$currentDate.docx"
                    Pair(doc, fileName)
                }
                selectedOption == "по форме 1С" -> {
                    val doc = save1CActToWord()
                    val fileName = "Акт_по_форме_1С_$currentDate.docx"
                    Pair(doc, fileName)
                }
                else -> {
                    // Стандартный акт
                    val doc = XWPFDocument()
                    val title = doc.createParagraph()
                    title.alignment = ParagraphAlignment.CENTER
                    val titleRun = title.createRun()
                    titleRun.setText(selectedOption)
                    titleRun.setBold(true)
                    titleRun.fontSize = 16

                    doc.createParagraph().createRun().addBreak()

                    val docInfo = doc.createParagraph()
                    docInfo.alignment = ParagraphAlignment.LEFT
                    docInfo.createRun().setText("Тип документа: $selectedOption")

                    val parties = doc.createParagraph()
                    parties.alignment = ParagraphAlignment.LEFT
                    parties.createRun().setText("ЗАКАЗЧИК:\n$customerDetails\n\nИСПОЛНИТЕЛЬ:\n$executorDetails")

                    val basic = doc.createParagraph()
                    basic.alignment = ParagraphAlignment.LEFT
                    basic.createRun().setText("Основание: Договор №$editableContractNumber от $editableContractDate")

                    val service = doc.createParagraph()
                    service.alignment = ParagraphAlignment.LEFT
                    service.createRun().setText("Услуга: $serviceName")

                    if (editableQuantity.isNotEmpty()) {
                        service.createRun().addBreak()
                        service.createRun().setText("Количество: $editableQuantity")
                    }

                    service.createRun().addBreak()
                    service.createRun().setText("Стоимость: $editableCost")

                    val signatures = doc.createParagraph()
                    signatures.alignment = ParagraphAlignment.LEFT
                    signatures.createRun().setText("\nПОДПИСИ СТОРОН:\n")

                    val customerSign = doc.createParagraph()
                    customerSign.alignment = ParagraphAlignment.LEFT
                    customerSign.createRun().setText("Заказчик: ___________________ ($customerSigner)")

                    val performerSign = doc.createParagraph()
                    performerSign.alignment = ParagraphAlignment.LEFT
                    performerSign.createRun().setText("Исполнитель: ___________________ ($performerSigner)")

                    val fileName = "Акт_$currentDate.docx"
                    Pair(doc, fileName)
                }
            }

            // Сохраняем акт
            val actFile = File(docsDir, actFileName)
            FileOutputStream(actFile).use { actOut ->
                actDocument.write(actOut)
            }
            actDocument.close()

            // Создаем договор
            val agreementDocument = saveAgreementToWord()

            // Сохраняем договор
            val agreementFileName = "Договор_$currentDate.docx"
            val agreementFile = File(docsDir, agreementFileName)
            FileOutputStream(agreementFile).use { agreementOut ->
                agreementDocument.write(agreementOut)
            }
            agreementDocument.close()

            // Создаем дополнительное соглашение (если есть данные)
            val savedAdditionalAgreements = mutableListOf<String>()
            if (AddAgreementData.agreementNumber.isNotEmpty()) {
                try {
                    val additionalAgreementDocument = saveAdditionalAgreementToWord()
                    val additionalAgreementFileName = "ДопСоглашение_${AddAgreementData.agreementNumber}_$currentDate.docx"
                    val additionalAgreementFile = File(docsDir, additionalAgreementFileName)
                    FileOutputStream(additionalAgreementFile).use { aaOut ->
                        additionalAgreementDocument.write(aaOut)
                    }
                    additionalAgreementDocument.close()
                    savedAdditionalAgreements.add(additionalAgreementFileName)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "Ошибка при сохранении дополнительного соглашения",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val savedApplications = mutableListOf<String>()
            applications.forEach { app ->
                try {
                    // 1. Сохраняем основное приложение (документ с описанием)
                    val appDocument = XWPFDocument().apply {
                        createParagraph().createRun().apply {
                            setText("ПРИЛОЖЕНИЕ № ${app.id} К ДОГОВОРУ №${app.contractNumber}")
                            setBold(true)
                            fontSize = 16
                        }
                        createParagraph().createRun().addBreak()
                        createParagraph().createRun().setText("Дата договора: ${app.contractDate}")
                        createParagraph().createRun().addBreak()
                        createParagraph().createRun().setText("Дата приложения: ${SimpleDateFormat("dd.MM.yyyy").format(Date())}")

                        // Добавляем информацию о прикрепленном файле, если он есть
                        app.attachedFilePath?.let { filePath ->
                            createParagraph().createRun().addBreak()
                            createParagraph().createRun().setText("Прикрепленный файл: ${File(filePath).name}")
                        }
                    }

                    val appFileName = "Приложение_${app.id}_$currentDate.docx"
                    File(docsDir, appFileName).outputStream().use {
                        appDocument.write(it)
                    }
                    appDocument.close()

                    // 2. Копируем прикрепленный файл (если есть)
                    app.attachedFilePath?.let { sourcePath ->
                        val sourceFile = File(sourcePath)
                        if (sourceFile.exists()) {
                            val destFileName = "Приложение_${app.id}_файл_${sourceFile.name}"
                            val destFile = File(docsDir, destFileName)

                            try {
                                sourceFile.copyTo(destFile, overwrite = true)
                                savedApplications.add("Приложение №${app.id}: ${appFileName} + файл: ${destFile.name}")
                            } catch (e: Exception) {
                                Log.e("FileCopy", "Ошибка копирования файла", e)
                                savedApplications.add("Приложение №${app.id}: ${appFileName} (ошибка копирования файла)")
                            }
                        } else {
                            savedApplications.add("Приложение №${app.id}: ${appFileName} (исходный файл не найден)")
                        }
                    } ?: run {
                        savedApplications.add("Приложение №${app.id}: ${appFileName}")
                    }

                } catch (e: Exception) {
                    Log.e("SaveApp", "Ошибка сохранения приложения", e)
                    Toast.makeText(
                        context,
                        "Ошибка при сохранении приложения №${app.id}: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            // Формируем сообщение о сохраненных документах
            val message = buildString {
                appendLine("Документы сохранены:")
                appendLine("1. ${if (selectedOption.startsWith("УПД")) "УПД" else "Акт"}: ${actFile.name}")
                appendLine("2. Договор: ${agreementFile.name}")
                if (savedAdditionalAgreements.isNotEmpty()) {
                    appendLine("3. Доп. соглашение: ${savedAdditionalAgreements.joinToString(", ")}")
                }
                if (savedApplications.isNotEmpty()) {
                    appendLine("4. Приложения:")
                    savedApplications.forEach { appendLine("   - $it") }
                }
            }

            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()

            // Показываем диалог подтверждения
            showConfirmationDialog = true

        } catch (e: Exception) {
            Log.e("SaveDocument", "Общая ошибка сохранения", e)
            Toast.makeText(
                context,
                "Ошибка при сохранении документов: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myColor)
    ) {
        // Границы экрана
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .background(borderColor)
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .background(borderColor)
                .align(Alignment.CenterEnd)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(borderColor)
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(borderColor)
                .align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Закрывающие документы",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally),
                    color = textColor
                )

                // Выпадающее меню
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor)
                        .clickable { expanded = true }
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedOption.ifEmpty { "Выберите закрывающий документ" },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (selectedOption.isEmpty()) Color.Gray else Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                    // Обновленный обработчик в DropdownMenu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        acts.forEach { act ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = act,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    when (act) {
                                        "УПД (если НДС статус 1, если без НДС статус 2)" -> {
                                            showVatStatusDialog = true
                                        }
                                        "по форме 1С" -> {
                                            selectedOption = act
                                        }
                                        "Разработка исполнителя" -> {
                                            selectedOption = act
                                        }
                                        else -> selectedOption = act
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Поле с реквизитами
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .border(1.dp, borderColor)
                        .padding(16.dp)
                ) {
                    // Calculate required height based on content
                    val customerLines = customerDetails.lines().size
                    val executorLines = executorDetails.lines().size
                    val maxLines = maxOf(customerLines, executorLines)
                    val boxHeight = (maxLines * 30).dp + 28.dp // Approximate line height + padding

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Customer details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "ЗАКАЗЧИК:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 4.dp),
                                color = textColor
                            )
                            Box(
                                modifier = Modifier
                                    .height(boxHeight)
                                    .padding(end = 8.dp)
                                    .border(1.dp, Color.Gray)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = customerDetails,
                                    color = textColor,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Executor details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "ИСПОЛНИТЕЛЬ:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 4.dp),
                                color = textColor
                            )
                            Box(
                                modifier = Modifier
                                    .height(boxHeight)
                                    .padding(start = 8.dp)
                                    .border(1.dp, Color.Gray)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = executorDetails,
                                    color = textColor,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Text(
                        text = "Основание: Договор №${AgreementData.contractNumber} от ${AgreementData.date}",
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Поля услуги
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = {},
                        label = { Text("Наименование услуги") },
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        readOnly = true
                    )

                    Box {
                        OutlinedTextField(
                            value = editableQuantity,
                            onValueChange = { editableQuantity = it },
                            label = { Text("Количество") },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        IconButton(
                            onClick = { showQuantityTooltip = true },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(y = (-8).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Подсказка",
                                tint = Color.Gray
                            )
                        }

                        if (showQuantityTooltip) {
                            AlertDialog(
                                onDismissRequest = { showQuantityTooltip = false },
                                title = { Text("Подсказка") },
                                text = { Text("Если услуга содержит в себе количество") },
                                confirmButton = {
                                    TextButton(
                                        onClick = { showQuantityTooltip = false }
                                    ) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }

// Поле "Стоимость"
                    OutlinedTextField(
                        value = editableCost,
                        onValueChange = { editableCost = it },
                        label = { Text("Стоимость") },
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Блок приложений, если они есть
                    if (applications.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Приложения к договору:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = textColor
                        )

                        applications.forEach { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Приложение №${app.id}: ",
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (app.attachedFilePath != null)
                                        File(app.attachedFilePath).name else "не сохранено",
                                    color = if (app.attachedFilePath != null) textColor else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Блок подписей - измененный вариант
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Подписи сторон",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Подпись заказчика - изменено по аналогии с Agreement.kt
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(1.dp, Color.Gray)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    "Подпись",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "ФИО подписанта",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = customerSigner,
                                    onValueChange = { customerSigner = it },
                                    label = { Text("ФИО") },
                                    textStyle = LocalTextStyle.current.copy(
                                        color = textColor
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Подпись исполнителя - изменено по аналогии с Agreement.kt
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(1.dp, Color.Gray)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    "Подпись",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "ФИО подписанта",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = performerSigner,
                                    onValueChange = { performerSigner = it },
                                    label = { Text("ФИО") },
                                    textStyle = LocalTextStyle.current.copy(
                                        color = textColor
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            // Кнопки действий
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { saveDocumentToWord() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    )
                ) {
                    Text("Сохранить",
                        color = textColor,
                        textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* Действие сохранения и отправки */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    )
                ) {
                    Text("Сохранить и отправить",
                        color = textColor)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* Действие печати */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    )
                ) {
                    Text("Печать",
                        color = textColor)
                }
            }
        }
    }

    // Диалог выбора статуса НДС для УПД
    if (showVatStatusDialog) {
        AlertDialog(
            onDismissRequest = { showVatStatusDialog = false },
            title = { Text("Выберите статус НДС для УПД") },
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVatStatus = 1 }
                    ) {
                        RadioButton(
                            selected = selectedVatStatus == 1,
                            onClick = { selectedVatStatus = 1 }
                        )
                        Text("С НДС (статус 1)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVatStatus = 2 }
                    ) {
                        RadioButton(
                            selected = selectedVatStatus == 2,
                            onClick = { selectedVatStatus = 2 }
                        )
                        Text("Без НДС (статус 2)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVatStatusDialog = false
                        selectedOption = "УПД (статус $selectedVatStatus)"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = borderColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showVatStatusDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог подтверждения сохранения
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Подтверждение сохранения") },
            text = {
                Column {
                    Text("Вы сохраняете следующие документы:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Акт:", style = MaterialTheme.typography.bodyLarge)
                    Text("Тип документа: ${selectedOption.ifEmpty { "не выбран" }}")
                    Text("Заказчик: $customer")
                    Text("Исполнитель: $performer")
                    Text("Договор №${contractNumber.ifEmpty { "не указан" }} от ${contractDate.ifEmpty { "не указана" }}")
                    Text("Услуга: ${serviceName.ifEmpty { "не указана" }}")
                    if (quantity.isNotEmpty()) Text("Количество: $quantity")
                    Text("Стоимость: ${cost.ifEmpty { "не указана" }}")
                    Text("Подписант заказчика: ${customerSigner.ifEmpty { "не указан" }}")
                    Text("Подписант исполнителя: ${performerSigner.ifEmpty { "не указан" }}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("2. Договор:", style = MaterialTheme.typography.bodyLarge)
                    Text("Услуга: ${AgreementData.serviceName}")
                    Text("Дата: ${AgreementData.date}")
                    Text("Условия оплаты: ${AgreementData.selectedOption}")
                    if (AgreementData.selectedOption == "аванс (100%/частично)") {
                        Text("Размер аванса: ${AgreementData.selectedAdvanceOption}")
                    }
                    Text("Подписант заказчика: ${AgreementData.customerFIO}")
                    Text("Подписант исполнителя: ${AgreementData.executorFIO}")

                    // Отображаем информацию о доп. соглашении
                    if (AddAgreementData.agreementNumber.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("3. Доп. соглашение:", style = MaterialTheme.typography.bodyLarge)
                        Text("Номер: ${AddAgreementData.agreementNumber}")
                        Text("Дата: ${AddAgreementData.agreementDate}")
                        Text("К договору №${AddAgreementData.contractNumber} от ${AddAgreementData.contractDate}")
                        Text("Изменения:")
                        AddAgreementData.agreementItems.forEachIndexed { index, item ->
                            if (item.isNotEmpty()) {
                                Text("${index + 1}. $item")
                            }
                        }
                    }

                    // Отображаем информацию о приложениях
                    // In the confirmation dialog, update the applications display part:
                    if (applications.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("4. Приложения:", style = MaterialTheme.typography.bodyLarge)
                        applications.forEach { app ->
                            Text("Приложение №${app.id}: ${
                                app.attachedFilePath?.let { File(it).name } ?: "не сохранено"
                            }")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Документы сохранены в папке Documents вашего устройства.",
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        // Дополнительные действия после подтверждения
                        navController.popBackStack() // Возврат на предыдущий экран
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = borderColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Открыть папку с документами")
                }
            }
        )
    }
}

@Preview
@Composable
fun ActsPreview() {
    Package_of_DocumentsbyRogozinTheme {
        Acts(
            navController = rememberNavController(),
            customerType = "juridical",
            /*applications = listOf(
                ApplicationForm(id = 1, contractNumber = "123", contractDate = "01.01.2023", filePath = "/path/to/file1.docx"),
                ApplicationForm(id = 2, contractNumber = "123", contractDate = "01.01.2023", filePath = null)
            )*/
        )
    }
}
