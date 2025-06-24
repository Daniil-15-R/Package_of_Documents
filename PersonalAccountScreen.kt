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

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme
import kotlinx.coroutines.delay

object AccountData {
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
    var userId: Long? = null

    fun copyFrom(other: AccountData) {
        organizationName = other.organizationName
        legalAddress = other.legalAddress
        actualAddress = other.actualAddress
        postalAddress = other.postalAddress
        inn = other.inn
        kpp = other.kpp
        ogrn = other.ogrn
        accountNumber = other.accountNumber
        correspondentAccount = other.correspondentAccount
        bic = other.bic
        bankName = other.bankName
        phone = other.phone
        email = other.email
        director = other.director
        authority = other.authority
        fullName = other.fullName
        addressesMatch = other.addressesMatch
        isEditingEnabled = other.isEditingEnabled
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalAccountScreen(navController: NavController) {
    val context = LocalContext.current
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)

    // Стили текста
    val headerTextStyle = MaterialTheme.typography.headlineMedium.copy(
        color = textColor,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    val successTextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )

    val sectionTextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )

    val textFieldTextStyle = LocalTextStyle.current.copy(
        color = textColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )

    val textFieldLabelStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )

    val buttonTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )

    // Состояния для полей ввода
    var organizationName by remember { mutableStateOf(AccountData.organizationName) }
    var legalAddress by remember { mutableStateOf(AccountData.legalAddress) }
    var actualAddress by remember { mutableStateOf(AccountData.actualAddress) }
    var postalAddress by remember { mutableStateOf(AccountData.postalAddress) }
    var inn by remember { mutableStateOf(AccountData.inn) }
    var kpp by remember { mutableStateOf(AccountData.kpp) }
    var ogrn by remember { mutableStateOf(AccountData.ogrn) }
    var accountNumber by remember { mutableStateOf(AccountData.accountNumber) }
    var correspondentAccount by remember { mutableStateOf(AccountData.correspondentAccount) }
    var bic by remember { mutableStateOf(AccountData.bic) }
    var bankName by remember { mutableStateOf(AccountData.bankName) }
    var phone by remember { mutableStateOf(AccountData.phone) }
    var email by remember { mutableStateOf(AccountData.email) }
    var director by remember { mutableStateOf(AccountData.director) }
    var authority by remember { mutableStateOf(AccountData.authority) }
    var fullName by remember { mutableStateOf(AccountData.fullName) }
    var addressesMatch by remember { mutableStateOf(AccountData.addressesMatch) }
    var isEditingEnabled by remember { mutableStateOf(AccountData.isEditingEnabled) }
    var showSuccessMessage by remember { mutableStateOf(false) }

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
            AccountData.organizationName = organizationName
            AccountData.legalAddress = legalAddress
            AccountData.actualAddress = actualAddress
            AccountData.postalAddress = postalAddress
            AccountData.inn = inn
            AccountData.kpp = kpp
            AccountData.ogrn = ogrn
            AccountData.accountNumber = accountNumber
            AccountData.correspondentAccount = correspondentAccount
            AccountData.bic = bic
            AccountData.bankName = bankName
            AccountData.phone = phone
            AccountData.email = email
            AccountData.director = director
            AccountData.authority = authority
            AccountData.fullName = fullName
            AccountData.addressesMatch = addressesMatch

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
                "Реквизиты исполнителя",
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
                            label = { Text("Наименование организации", style = textFieldLabelStyle) },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditingEnabled,
                            readOnly = !isEditingEnabled,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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

                // Вторая группа полей
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

            // Измененные кнопки действий в PersonalAccountScreen.kt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // Сохраняем данные в AccountData
                        AccountData.organizationName = organizationName
                        AccountData.legalAddress = legalAddress
                        AccountData.actualAddress = actualAddress
                        AccountData.postalAddress = postalAddress
                        AccountData.inn = inn
                        AccountData.kpp = kpp
                        AccountData.ogrn = ogrn
                        AccountData.accountNumber = accountNumber
                        AccountData.correspondentAccount = correspondentAccount
                        AccountData.bic = bic
                        AccountData.bankName = bankName
                        AccountData.phone = phone
                        AccountData.email = email
                        AccountData.director = director
                        AccountData.authority = authority
                        AccountData.fullName = fullName
                        AccountData.addressesMatch = addressesMatch
                        AccountData.isEditingEnabled = false

                        // Сохраняем в базу данных
                        val dbHelper = DatabaseHelper(context)
                        val userId = 1L // Здесь нужно получить реальный ID пользователя из сессии/авторизации

                        // Проверяем, есть ли уже запись для этого пользователя
                        val cursor = dbHelper.getContractorDetails(userId)
                        if (cursor.moveToFirst()) {
                            // Обновляем существующую запись
                            dbHelper.updateContractorDetails(
                                contractorId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTRACTOR_ID)),
                                organizationName = organizationName,
                                legalAddress = legalAddress,
                                actualAddress = actualAddress,
                                postalAddress = postalAddress,
                                inn = inn,
                                kpp = kpp,
                                ogrn = ogrn,
                                accountNumber = accountNumber,
                                correspondentAccount = correspondentAccount,
                                bic = bic,
                                bankName = bankName,
                                phone = phone,
                                email = email,
                                director = director,
                                authority = authority,
                                fullName = fullName,
                                addressesMatch = addressesMatch
                            )
                        } else {
                            // Создаем новую запись
                            dbHelper.saveContractorDetails(
                                userId = userId,
                                organizationName = organizationName,
                                legalAddress = legalAddress,
                                actualAddress = actualAddress,
                                postalAddress = postalAddress,
                                inn = inn,
                                kpp = kpp,
                                ogrn = ogrn,
                                accountNumber = accountNumber,
                                correspondentAccount = correspondentAccount,
                                bic = bic,
                                bankName = bankName,
                                phone = phone,
                                email = email,
                                director = director,
                                authority = authority,
                                fullName = fullName,
                                addressesMatch = addressesMatch
                            )
                        }
                        cursor.close()

                        isEditingEnabled = false
                        showSuccessMessage = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    ),
                    enabled = isEditingEnabled // Кнопка активна только в режиме редактирования
                ) {
                    Text("Сохранить",
                        color = textColor,
                        textAlign = TextAlign.Center)
                }
                Button(
                    onClick = {
                        isEditingEnabled = true
                        AccountData.isEditingEnabled = true
                        showSuccessMessage = false

                        // Загружаем данные из базы данных при входе в режим редактирования
                        val dbHelper = DatabaseHelper(context)
                        val userId = 1L // Здесь нужно получить реальный ID пользователя из сессии/авторизации
                        val cursor = dbHelper.getContractorDetails(userId)

                        if (cursor.moveToFirst()) {
                            organizationName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORGANIZATION_NAME))
                            legalAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEGAL_ADDRESS))
                            actualAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTUAL_ADDRESS))
                            postalAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POSTAL_ADDRESS))
                            inn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INN))
                            kpp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KPP))
                            ogrn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OGRN))
                            accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_NUMBER))
                            correspondentAccount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CORRESPONDENT_ACCOUNT))
                            bic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BIC))
                            bankName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BANK_NAME))
                            phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE))
                            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL))
                            director = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DIRECTOR))
                            authority = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHORITY))
                            fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME))
                            addressesMatch = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESSES_MATCH)) == 1

                            // Обновляем AccountData
                            AccountData.copyFrom(AccountData.apply {
                                this.organizationName = organizationName
                                this.legalAddress = legalAddress
                                this.actualAddress = actualAddress
                                this.postalAddress = postalAddress
                                this.inn = inn
                                this.kpp = kpp
                                this.ogrn = ogrn
                                this.accountNumber = accountNumber
                                this.correspondentAccount = correspondentAccount
                                this.bic = bic
                                this.bankName = bankName
                                this.phone = phone
                                this.email = email
                                this.director = director
                                this.authority = authority
                                this.fullName = fullName
                                this.addressesMatch = addressesMatch
                            })
                        }
                        cursor.close()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC)
                    ),
                    enabled = !isEditingEnabled // Кнопка активна только вне режима редактирования
                ) {
                    Text("Редактировать",
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
fun PersonalAccountScreenPreview() {
    Package_of_DocumentsbyRogozinTheme {
        PersonalAccountScreen(navController = rememberNavController())
    }
}