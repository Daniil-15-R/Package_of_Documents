package com.package_of_documents.byrogozin

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme
import com.package_of_documents.byrogozin.AccountData
import com.package_of_documents.byrogozin.AccountData1
import com.package_of_documents.byrogozin.AccountData2
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

object AddAgreementData {
    var agreementNumber: String = ""
    var agreementDate: String = ""
    var contractNumber: String = ""
    var contractDate: String = ""
    var agreementItems: List<String> = listOf()
    var customerName: String = ""
    var executorName: String = ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAgreement(
    navController: NavController,
    customerType: String,
    contractNumberFromParent: String = "",
    contractDateFromParent: String = ""
) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showConfirmationDialog by remember { mutableStateOf(false) }
    // States for all fields
    var agreementNumber by remember { mutableStateOf(AddAgreementData.agreementNumber) }
    var agreementDate by remember { mutableStateOf(AddAgreementData.agreementDate) }
    var contractNumber by remember {
        mutableStateOf(contractNumberFromParent.ifEmpty { AddAgreementData.contractNumber })
    }
    var contractDate by remember {
        mutableStateOf(contractDateFromParent.ifEmpty { AddAgreementData.contractDate })
    }
    var agreementItems by remember { mutableStateOf(AddAgreementData.agreementItems.ifEmpty { listOf("", "") }) }
    var customerName by remember { mutableStateOf(AddAgreementData.customerName) }
    var executorName by remember { mutableStateOf(AddAgreementData.executorName) }

    // Customer details - depends on customer type
    val customerDetails = remember(customerType) {
        when (customerType) {
            "juridical" -> {
                """
                Организация: ${AccountData1.organizationName}
                Юридический адрес: ${AccountData1.legalAddress}
                Фактический адрес: ${AccountData1.actualAddress}
                Почтовый адрес: ${AccountData1.postalAddress}
                ИНН: ${AccountData1.inn} КПП: ${AccountData1.kpp}
                ОГРН/ОГРНИП: ${AccountData1.ogrn}
                Расчетный счет: ${AccountData1.accountNumber}
                К/счет: ${AccountData1.correspondentAccount}
                БИК: ${AccountData1.bic}
                Наименование банка: ${AccountData1.bankName}
                Контактные данные:
                Телефон: ${AccountData1.phone}
                Email: ${AccountData1.email}
                Руководитель: ${AccountData1.director}
                Основные полномочия: ${AccountData1.authority}
                ФИО: ${AccountData1.fullName}
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

    // Executor details (always from AccountData)
    val executorDetails = remember {
        """
        Оганизация: ${AccountData.organizationName}
        Юридический адрес: ${AccountData.legalAddress}
        Фактический адрес: ${AccountData.actualAddress}
        Почтовый адрес: ${AccountData.postalAddress}
        ИНН: ${AccountData.inn} КПП: ${AccountData.kpp}
        ОГРН/ОГРНИП: ${AccountData.ogrn}
        Расчетный счет: ${AccountData.accountNumber}
        К/счет: ${AccountData.correspondentAccount}
        БИК: ${AccountData.bic}
        Наименование банка: ${AccountData.bankName}
        Контактные данные:
        Телефон: ${AccountData.phone}
        Email: ${AccountData.email}
        Руководитель: ${AccountData.director}
        Основные полномочия: ${AccountData.authority}
        ФИО: ${AccountData.fullName}
        """.trimIndent()
    }

    // Function to format FIO
    fun formatFIO(fullName: String): String {
        val parts = fullName.split(" ").filter { it.isNotBlank() }
        return when (parts.size) {
            3 -> "${parts[0]} ${parts[1].first()}.${parts[2].first()}."
            2 -> "${parts[0]} ${parts[1].first()}."
            else -> fullName
        }
    }

    // Function to save all data
    fun saveData() {
        AddAgreementData.agreementNumber = agreementNumber
        AddAgreementData.agreementDate = agreementDate
        AddAgreementData.contractNumber = contractNumber
        AddAgreementData.contractDate = contractDate
        AddAgreementData.agreementItems = agreementItems
        AddAgreementData.customerName = customerName
        AddAgreementData.executorName = executorName
    }

    // Function to save agreement to database
    fun saveAgreementToDatabase(scope: CoroutineScope) {
        scope.launch {
            try {
                val dbHelper = DatabaseHelper(context)

                // Получаем contractorId из базы данных по userId
                val userId = AccountData.userId ?: run {
                    showErrorMessage = true
                    showSuccessMessage = false
                    return@launch
                }

                val cursor = dbHelper.getContractorDetails(userId)
                val contractorId = if (cursor.moveToFirst()) {
                    cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTRACTOR_ID))
                } else {
                    showErrorMessage = true
                    showSuccessMessage = false
                    cursor.close()
                    return@launch
                }
                cursor.close()

                // Проверяем, что agreementItems не пуст
                if (AddAgreementData.agreementItems.isEmpty()) {
                    showErrorMessage = true
                    showSuccessMessage = false
                    return@launch
                }

                // Сохраняем в базу данных
                dbHelper.saveAdditionalAgreement(
                    agreementNumber = AddAgreementData.agreementNumber,
                    agreementDate = AddAgreementData.agreementDate,
                    contractNumber = AddAgreementData.contractNumber,
                    contractDate = AddAgreementData.contractDate,
                    customerFIO = AddAgreementData.customerName,
                    executorFIO = AddAgreementData.executorName,
                    agreementItems = AddAgreementData.agreementItems,
                    contractorId = contractorId
                )

                showSuccessMessage = true
                showErrorMessage = false

                // Очищаем форму после успешного сохранения
                AddAgreementData.agreementNumber = ""
                AddAgreementData.agreementDate = ""
                AddAgreementData.contractNumber = ""
                AddAgreementData.contractDate = ""
                AddAgreementData.agreementItems = listOf("", "")
                AddAgreementData.customerName = ""
                AddAgreementData.executorName = ""

                // Обновляем локальные состояния
                agreementNumber = ""
                agreementDate = ""
                contractNumber = contractNumberFromParent
                contractDate = contractDateFromParent
                agreementItems = listOf("", "")
                customerName = when (customerType) {
                    "juridical" -> formatFIO(AccountData1.fullName)
                    "physical" -> formatFIO(AccountData2.fioText)
                    else -> ""
                }
                executorName = formatFIO(AccountData.fullName)

            } catch (e: Exception) {
                showErrorMessage = true
                showSuccessMessage = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myColor)
    ) {
        // Screen borders
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .background(borderColor)
                .align(Alignment.CenterStart))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .background(borderColor)
                .align(Alignment.CenterEnd))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(borderColor)
                .align(Alignment.TopCenter))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(borderColor)
                .align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                "Дополнительное соглашение",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                color = textColor
            )

            if (showSuccessMessage) {
                Text(
                    "Данные успешно сохранены",
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (showErrorMessage) {
                Text(
                    "Ошибка при сохранении данных",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Agreement number and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = agreementNumber,
                    onValueChange = { agreementNumber = it },
                    label = { Text("№ соглашения") },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = agreementDate,
                    onValueChange = { agreementDate = it },
                    label = { Text("дата") },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    modifier = Modifier.weight(1f)
                )
            }

            // Related contract
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "к Договору",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = contractNumber,
                    onValueChange = { contractNumber = it },
                    label = { Text("№") },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = contractDate,
                    onValueChange = { contractDate = it },
                    label = { Text("дата") },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Заказчик и Исполнитель
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "ЗАКАЗЧИК:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (customerType == "juridical") {
                    Column {
                        Text(
                            text = "${AccountData1.organizationName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "в лице ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                            Text(
                                text = "${AccountData1.director} ${AccountData1.fullName}, ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "действующего на основании ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                            Text(
                                text = AccountData1.authority,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                    }
                } else {
                    Text(
                        AccountData2.fioText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Text(
                    "ИСПОЛНИТЕЛЬ:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Column {
                    Text(
                        text = "${AccountData.organizationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "в лице ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        Text(
                            text = "${AccountData.director} ${AccountData.fullName}, ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "действующего на основании ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        Text(
                            text = AccountData.authority,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }
            }

            // Agreement subject
            Text(
                "Предмет соглашения",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dynamic agreement items
            Column {
                agreementItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1})",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = item,
                            onValueChange = { newValue ->
                                agreementItems = agreementItems.toMutableList().apply {
                                    this[index] = newValue
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Введите пункт соглашения") },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Add item button
                IconButton(
                    onClick = { agreementItems = agreementItems + "" },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить пункт",
                        tint = borderColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signatures section
            Text(
                "Реквизиты сторон и подписи",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Parties details section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val customerLines = customerDetails.lines().size
                val executorLines = executorDetails.lines().size
                val maxLines = maxOf(customerLines, executorLines)
                val boxHeight = (maxLines * 30).dp + 28.dp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Customer details
                    Box(
                        modifier = Modifier
                            .weight(1f)
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

                    // Executor details
                    Box(
                        modifier = Modifier
                            .weight(1f)
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

            // Signatures fields with signature boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Customer signature
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
                            value = customerName.ifEmpty {
                                when (customerType) {
                                    "juridical" -> formatFIO(AccountData1.fullName)
                                    "physical" -> formatFIO(AccountData2.fioText)
                                    else -> ""
                                }
                            },
                            onValueChange = { customerName = it },
                            label = { Text("ФИО") },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Executor signature
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
                            value = executorName.ifEmpty { formatFIO(AccountData.fullName) },
                            onValueChange = { executorName = it },
                            label = { Text("ФИО") },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Button(
                onClick = {
                    saveData()
                    showSuccessMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp)
                    .border(width = 2.dp, color = Color.Black),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F7FC)
                )
            ) {
                Text("Создать еще доп.соглашение", color = textColor)
            }

            Button(
                onClick = {
                    // Проверяем заполнение обязательных полей
                    if (agreementNumber.isBlank() || agreementDate.isBlank() ||
                        contractNumber.isBlank() || contractDate.isBlank()) {

                        showErrorMessage = true
                        showSuccessMessage = false
                    } else {
                        saveData() // Сохраняем данные в объект AddAgreementData
                        showConfirmationDialog = true // Показываем диалог вместо прямого перехода
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp)
                    .border(width = 2.dp, color = Color.Black),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F7FC)
                )
            ) {
                Text("Перейти к акту", color = textColor)
            }

// Добавляем диалог подтверждения
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Подтверждение") },
                    text = { Text("Вы уже готовы закрыть документ?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmationDialog = false
                                scope.launch {
                                    try {
                                        saveAgreementToDatabase(scope)
                                        navController.navigate("acts/$customerType") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } catch (e: Exception) {
                                        showErrorMessage = true
                                        showSuccessMessage = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = borderColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Да")
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
                            Text("Нет")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddAgreementPreview() {
    Package_of_DocumentsbyRogozinTheme {
        AddAgreement(
            navController = rememberNavController(),
            customerType = "juridical",
            contractNumberFromParent = "123"
        )
    }
}
