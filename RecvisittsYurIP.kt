package com.package_of_documents.byrogozin

import android.R
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

import kotlinx.coroutines.delay

object AccountData1 {
    var organizationName: String = ""
    var legalAddress: String = ""
    var actualAddress: String = ""
    var postalAddress: String = ""
    var inn: String = ""
    var kpp: String = ""
    var ogrn: String = ""
    var accountNumber: String = ""
    var correspondentAccount: String = ""
    var bic: String = ""
    var bankName: String = ""
    var phone: String = ""
    var email: String = ""
    var director: String = ""
    var authority: String = ""
    var fullName: String = ""
    var addressesMatch: Boolean = false
    var isEditingEnabled: Boolean = true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecvisittsYurIP(navController: NavController){
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    // Состояния для полей ввода
    var organizationName by remember { mutableStateOf(AccountData1.organizationName) }
    var legalAddress by remember { mutableStateOf(AccountData1.legalAddress) }
    var actualAddress by remember { mutableStateOf(AccountData1.actualAddress) }
    var postalAddress by remember { mutableStateOf(AccountData1.postalAddress) }
    var inn by remember { mutableStateOf(AccountData1.inn) }
    var kpp by remember { mutableStateOf(AccountData1.kpp) }
    var ogrn by remember { mutableStateOf(AccountData1.ogrn) }
    var accountNumber by remember { mutableStateOf(AccountData1.accountNumber) }
    var correspondentAccount by remember { mutableStateOf(AccountData1.correspondentAccount) }
    var bic by remember { mutableStateOf(AccountData1.bic) }
    var bankName by remember { mutableStateOf(AccountData1.bankName) }
    var phone by remember { mutableStateOf(AccountData1.phone) }
    var email by remember { mutableStateOf(AccountData1.email) }
    var director by remember { mutableStateOf(AccountData1.director) }
    var authority by remember { mutableStateOf(AccountData1.authority) }
    var fullName by remember { mutableStateOf(AccountData1.fullName) }
    var addressesMatch by remember { mutableStateOf(AccountData1.addressesMatch) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isEditingEnabled by remember { mutableStateOf(AccountData1.isEditingEnabled) }

    // Состояния для выпадающих меню
    var directorExpanded by remember { mutableStateOf(false) }
    var authorityExpanded by remember { mutableStateOf(false) }

    // Варианты для выпадающих меню
    val directors = listOf(
        "Директор",
        "Генеральный директор",
        "Исполнительный директор",
        "Президент компании",
        "Другое"
    )

    val authorities = listOf(
        "Устав",
        "Доверенность",
        "Приказ",
        "Решение собрания",
        "Другое"
    )

    // Эффекты
    LaunchedEffect(addressesMatch) {
        if (addressesMatch) {
            actualAddress = legalAddress
        }
    }

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            AccountData1.organizationName = organizationName
            AccountData1.legalAddress = legalAddress
            AccountData1.actualAddress = actualAddress
            AccountData1.postalAddress = postalAddress
            AccountData1.inn = inn
            AccountData1.kpp = kpp
            AccountData1.ogrn = ogrn
            AccountData1.accountNumber = accountNumber
            AccountData1.correspondentAccount = correspondentAccount
            AccountData1.bic = bic
            AccountData1.bankName = bankName
            AccountData1.phone = phone
            AccountData1.email = email
            AccountData1.director = director
            AccountData1.authority = authority
            AccountData1.fullName = fullName
            AccountData1.addressesMatch = addressesMatch

            delay(5000)
            navController.popBackStack()
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
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Реквизиты юридического лица/ИП",
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Первая группа полей
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = organizationName,
                            onValueChange = { organizationName = it },
                            label = { Text("Наименование организации", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = legalAddress,
                            onValueChange = {
                                legalAddress = it
                                if (addressesMatch) {
                                    actualAddress = it
                                }
                            },
                            label = { Text("Юридический адрес", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = addressesMatch,
                                onCheckedChange = { checked ->
                                    if (isEditingEnabled) {
                                        addressesMatch = checked
                                        if (checked) {
                                            actualAddress = legalAddress
                                        }
                                    }
                                },
                                enabled = isEditingEnabled
                            )
                            Text("Адреса совпадают", modifier = Modifier.padding(start = 8.dp), color = textColor)
                        }
                        OutlinedTextField(
                            value = actualAddress,
                            onValueChange = { if (!addressesMatch) actualAddress = it },
                            label = { Text("Фактический адрес", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled && !addressesMatch,
                            readOnly = !isEditingEnabled || addressesMatch
                        )
                        OutlinedTextField(
                            value = postalAddress,
                            onValueChange = { postalAddress = it },
                            label = { Text("Почтовый адрес", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = inn,
                            onValueChange = { inn = it },
                            label = { Text("ИНН", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = kpp,
                            onValueChange = { kpp = it },
                            label = { Text("КПП", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = ogrn,
                            onValueChange = { ogrn = it },
                            label = { Text("ОГРН/ОГРНИП", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("Расчетный счет", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = correspondentAccount,
                            onValueChange = { correspondentAccount = it },
                            label = { Text("К/счет", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = bic,
                            onValueChange = { bic = it },
                            label = { Text("БИК", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Наименование банка", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Контактные данные
                Text(
                    "Контактные данные:",
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    color = textColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Телефон", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Руководитель и полномочия
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = director,
                        onValueChange = { director = it },
                        label = { Text("Руководитель", color = textColor) },
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (isEditingEnabled) directorExpanded = true },
                        enabled = isEditingEnabled,
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { if (isEditingEnabled) directorExpanded = true }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = directorExpanded,
                        onDismissRequest = { directorExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        directors.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    director = item
                                    directorExpanded = false
                                }
                            )
                        }
                    }
                }

                // Основные полномочия с выпадающим меню
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = authority,
                        onValueChange = { authority = it },
                        label = { Text("Основные полномочия", color = textColor) },
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (isEditingEnabled) authorityExpanded = true },
                        enabled = isEditingEnabled,
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { if (isEditingEnabled) authorityExpanded = true }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = authorityExpanded,
                        onDismissRequest = { authorityExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        authorities.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    authority = item
                                    authorityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("ФИО", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // Сохраняем все введенные данные перед переходом
                        AccountData1.organizationName = organizationName
                        AccountData1.legalAddress = legalAddress
                        AccountData1.actualAddress = actualAddress
                        AccountData1.postalAddress = postalAddress
                        AccountData1.inn = inn
                        AccountData1.kpp = kpp
                        AccountData1.ogrn = ogrn
                        AccountData1.accountNumber = accountNumber
                        AccountData1.correspondentAccount = correspondentAccount
                        AccountData1.bic = bic
                        AccountData1.bankName = bankName
                        AccountData1.phone = phone
                        AccountData1.email = email
                        AccountData1.director = director
                        AccountData1.authority = authority
                        AccountData1.fullName = fullName
                        AccountData1.addressesMatch = addressesMatch

                        // Переходим на страницу создания договора с указанием типа заказчика
                        showSuccessMessage = true
                        navController.navigate("agreem/juridical")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    )
                ) {
                    Text("Перейти к созданию договора",
                        color = textColor,
                        textAlign = TextAlign.Center)
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    )
                ) {
                    Text("Загрузить реквизиты",
                        color = textColor,
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecvisittsYurIPPreview() {
    Package_of_DocumentsbyRogozinTheme {
        RecvisittsYurIP(navController = rememberNavController())
    }
}
