package com.package_of_documents.byrogozin

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme

object AccountData2{
    var selectedOption: String = "Выберите тип значения"
    var fioText: String = ""
    var selectedCountry: String = "РФ"
    var passportSeries: String = ""
    var passportNumber: String = ""
    var issuedBy: String = ""
    var issueDate: String = ""
    var departmentCode: String = ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FisicheskoeFace(navController: NavController) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    var showSuccessMessage by remember { mutableStateOf(false)}

    // Состояния для основного Dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(AccountData2.selectedOption) }

    // Состояния для ФИО
    var fioText by remember { mutableStateOf(AccountData2.fioText) }

    // Состояния для паспортных данных
    var countryExpanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(AccountData2.selectedCountry) }
    var passportSeries by remember { mutableStateOf(AccountData2.passportSeries) }
    var passportNumber by remember { mutableStateOf(AccountData2.passportNumber) }
    var issuedBy by remember { mutableStateOf(AccountData2.issuedBy) }
    var issueDate by remember { mutableStateOf(AccountData2.issueDate) }
    var departmentCode by remember { mutableStateOf(AccountData2.departmentCode) }

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
            Text(
                "Реквизиты физического лица",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 24.dp)
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

            // Основной DropdownMenu для выбора типа заполнения
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Тип заполнения:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor)
                        .clickable { expanded = true }
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedOption,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("ФИО") },
                            onClick = {
                                selectedOption = "ФИО"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ФИО + Паспорт") },
                            onClick = {
                                selectedOption = "ФИО + Паспорт"
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Поле для ФИО (отображается в обоих вариантах)
            if (selectedOption == "ФИО" || selectedOption == "ФИО + Паспорт") {
                OutlinedTextField(
                    value = fioText,
                    onValueChange = { fioText = it },
                    label = { Text("ФИО") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )
            }

            // Блок паспортных данных (только для "ФИО + Паспорт")
            if (selectedOption == "ФИО + Паспорт") {
                // Dropdown для выбора страны
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Гражданство:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderColor)
                            .clickable { countryExpanded = true }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = selectedCountry,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Country Dropdown",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )

                        DropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("РФ") },
                                onClick = {
                                    selectedCountry = "РФ"
                                    countryExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Иное государство") },
                                onClick = {
                                    selectedCountry = "Иное государство"
                                    countryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Поле "Паспорт + (страна)"
                OutlinedTextField(
                    value = "Паспорт $selectedCountry",
                    onValueChange = {},
                    label = { Text("Тип документа") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    ),
                    enabled = false
                )

                // Поля для паспортных данных
                OutlinedTextField(
                    value = passportSeries,
                    onValueChange = { passportSeries = it },
                    label = { Text("Серия") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = passportNumber,
                    onValueChange = { passportNumber = it },
                    label = { Text("Номер") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = issuedBy,
                    onValueChange = { issuedBy = it },
                    label = { Text("Кем выдан") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = issueDate,
                    onValueChange = { issueDate = it },
                    label = { Text("Дата выдачи (ДД.ММ.ГГГГ)") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = departmentCode,
                    onValueChange = { departmentCode = it },
                    label = { Text("Код подразделения") },
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.5f)
                    )
                )
            }

            // Добавленная кнопка "Перейти к созданию договора"
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Сохраняем все данные перед переходом
                    AccountData2.selectedOption = selectedOption
                    AccountData2.fioText = fioText
                    AccountData2.selectedCountry = selectedCountry
                    AccountData2.passportSeries = passportSeries
                    AccountData2.passportNumber = passportNumber
                    AccountData2.issuedBy = issuedBy
                    AccountData2.issueDate = issueDate
                    AccountData2.departmentCode = departmentCode

                    showSuccessMessage = true
                    navController.navigate("agreem/physical")
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
                Text("Перейти к созданию договора",
                    color = textColor,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FisicheskoeFacePreview() {
    Package_of_DocumentsbyRogozinTheme {
        FisicheskoeFace(navController = rememberNavController())
    }
}
