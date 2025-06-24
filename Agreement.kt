package com.package_of_documents.byrogozin

import android.R
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import kotlinx.coroutines.delay

object AgreementData {
    var contractNumber: String = ""
    var serviceName: String = ""
    var date: String = ""
    var customer: String = ""
    var executor: String = ""
    var selectedOption: String = "аванс (100%/частично)"
    var selectedAdvanceOption: String = "100%"
    var customerFIO: String = ""
    var executorFIO: String = ""
    var userId:Long = -1L
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Agreement(navController: NavController, customerType: String) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    var showSuccessMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // State for dropdown menus
    var showMainDropdown by remember { mutableStateOf(false) }
    var showAdvanceDropdown by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(AgreementData.selectedOption) }
    var selectedAdvanceOption by remember { mutableStateOf(AgreementData.selectedAdvanceOption) }

    // State for input fields
    var serviceName by remember { mutableStateOf(AgreementData.serviceName) }
    var date by remember { mutableStateOf(AgreementData.date) }
    var customer by remember { mutableStateOf(AgreementData.customer) }
    var executor by remember { mutableStateOf(AgreementData.executor) }
    var customerFIO by remember { mutableStateOf(AgreementData.customerFIO) }
    var executorFIO by remember { mutableStateOf(AgreementData.executorFIO) }

    // State for editable executor header
    var director by remember { mutableStateOf(AccountData.director) }
    var authority by remember { mutableStateOf(AccountData.authority) }

    var contractNumber by remember { mutableStateOf(AgreementData.contractNumber) }

    // Customer details based on customerType
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

    fun formatFIO(fullName: String): String {
        val parts = fullName.split(" ").filter { it.isNotBlank() }
        return when (parts.size) {
            3 -> { // Фамилия Имя Отчество
                "${parts[0]} ${parts[1].first()}.${parts[2].first()}."
            }
            2 -> { // Фамилия Имя
                "${parts[0]} ${parts[1].first()}."
            }
            else -> fullName // Если не удалось разобрать, возвращаем как есть
        }
    }

    fun saveContractToDatabase() {
        val dbHelper = DatabaseHelper(context)

        // Сохраняем данные в объект AgreementData перед сохранением в базу данных
        AgreementData.contractNumber = contractNumber
        AgreementData.date = date
        AgreementData.serviceName = serviceName
        AgreementData.selectedOption = selectedOption
        AgreementData.selectedAdvanceOption = selectedAdvanceOption
        AgreementData.customerFIO = customerFIO
        AgreementData.executorFIO = executorFIO

        // Обновляем также данные исполнителя
        AccountData.director = director
        AccountData.authority = authority

        // Получаем contractorId (ID исполнителя) из базы данных
        val userId = AgreementData.userId
        if (userId == -1L) {
            return
        }

        val contractorCursor = dbHelper.getContractorDetails(userId)
        val contractorId = if (contractorCursor.moveToFirst()) {
            contractorCursor.getLong(contractorCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTRACTOR_ID))
        } else {
            -1L
        }
        contractorCursor.close()

        // Сохраняем договор в базу данных
        if (contractorId != -1L) {
            dbHelper.saveContract(
                contractNumber = AgreementData.contractNumber,
                serviceName = AgreementData.serviceName,
                contractDate = AgreementData.date,
                customerFIO = AgreementData.customerFIO,
                executorFIO = AgreementData.executorFIO,
                customerType = customerType,
                contractorId = contractorId
            )
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
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Договор №",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = contractNumber,
                    onValueChange = { contractNumber = it },
                    modifier = Modifier.width(100.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        color = textColor
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = textColor,
                        unfocusedIndicatorColor = textColor
                    ),
                    singleLine = true
                )
            }

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

            // Basic information fields
            OutlinedTextField(
                value = serviceName,
                onValueChange = {
                    serviceName = it
                    AgreementData.serviceName = it
                },
                label = { Text("Наименование услуги") },
                textStyle = LocalTextStyle.current.copy(
                    color = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Дата") },
                textStyle = LocalTextStyle.current.copy(
                    color = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

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

                            OutlinedTextField(
                                value = director,
                                onValueChange = { director = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = textColor,
                                    unfocusedIndicatorColor = Color.Gray
                                )
                            )

                            Text(
                                text = "${AccountData.fullName}, ",
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

                            OutlinedTextField(
                                value = authority,
                                onValueChange = { authority = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = textColor,
                                    unfocusedIndicatorColor = Color.Gray
                                )
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

                        OutlinedTextField(
                            value = director,
                            onValueChange = { director = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = textColor,
                                unfocusedIndicatorColor = Color.Gray
                            )
                        )

                        Text(
                            text = "${AccountData.fullName}, ",
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

                        OutlinedTextField(
                            value = authority,
                            onValueChange = { authority = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = textColor,
                                unfocusedIndicatorColor = Color.Gray
                            )
                        )
                    }
                }
            }

            // Остальные секции договора...
            ContractSectionWithUpload(
                title = "1. Предмет договора",
                onUploadClick = { /* Handle file upload */ }
            )

            ContractSectionWithUpload(
                title = "2. Обязанности сторон",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "3. Порядок оказания услуг",
                onUploadClick = { /* Handle file upload */ }
            )

            // Price section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "4. Стоимость и порядок расчетов",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    color = textColor
                )
                Text(
                    "Порядок расчетов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Other conditions with dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    selectedOption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { showMainDropdown = true }
                        .padding(8.dp)
                        .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                        .padding(8.dp)
                )

                DropdownMenu(
                    expanded = showMainDropdown,
                    onDismissRequest = { showMainDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("аванс (100%/частично)") },
                        onClick = {
                            selectedOption = "аванс (100%/частично)"
                            showMainDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Иные условия") },
                        onClick = {
                            selectedOption = "Иные условия"
                            showMainDropdown = false
                        }
                    )
                }

                if (selectedOption == "аванс (100%/частично)") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            selectedAdvanceOption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier
                                .clickable { showAdvanceDropdown = true }
                                .padding(8.dp)
                                .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                                .padding(8.dp)
                        )

                        DropdownMenu(
                            expanded = showAdvanceDropdown,
                            onDismissRequest = { showAdvanceDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("100%") },
                                onClick = {
                                    selectedAdvanceOption = "100%"
                                    showAdvanceDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("частично") },
                                onClick = {
                                    selectedAdvanceOption = "частично"
                                    showAdvanceDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Remaining sections with file upload buttons
            ContractSectionWithUpload(
                title = "5. Сроки выполнения",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "6. Ответственность сторон",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "7. Порядок разрешения споров",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "8. Форс-мажор",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "9. Изменение и расторжение договора",
                onUploadClick = { /* Handle file upload */ }
            )
            ContractSectionWithUpload(
                title = "10. Другие условия",
                onUploadClick = { /* Handle file upload */ }
            )

            // Parties details section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    "11. Реквизиты сторон и подписи",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Заказчик",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Text(
                        "Исполнитель",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

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

                // Подписи сторон
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                                value = customerFIO.ifEmpty {
                                    when (customerType) {
                                        "juridical" -> formatFIO(AccountData1.fullName)
                                        "physical" -> formatFIO(AccountData2.fioText)
                                        else -> ""
                                    }
                                },
                                onValueChange = { customerFIO = it },
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
                                value = executorFIO.ifEmpty { formatFIO(AccountData.fullName) },
                                onValueChange = { executorFIO = it },
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

            // Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        saveContractToDatabase()
                        showSuccessMessage = true
                        navController.navigate("acts/$customerType?contractNumber=${AgreementData.contractNumber}&date=${AgreementData.date}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Перейти к созданию акта", color = textColor)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        saveContractToDatabase()
                        showSuccessMessage = true
                        navController.navigate("applic/${AgreementData.contractNumber}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Перейти к приложению", color = textColor)
                }
            }
        }
    }
}

@Composable
fun ContractSectionWithUpload(title: String, onUploadClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6A1D24)
            )

            Button(
                onClick = onUploadClick,
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Gray),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    "Загрузить",
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            Text(
                "Файл не загружен",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun AgreementPreview() {
    Package_of_DocumentsbyRogozinTheme {
        Agreement(navController = rememberNavController(), customerType = "juridical")
    }
}