package com.package_of_documents.byrogozin

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.package_of_documents.byrogozin.AccountData1
import com.package_of_documents.byrogozin.AccountData2
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.content.Context
import android.webkit.MimeTypeMap
import java.io.BufferedReader
import java.io.InputStreamReader

@Parcelize
data class ApplicationForm(
    val id: Int,
    var contractNumber: String = "",
    var contractDate: String = "",
    var attachedFilePath: String? = null // Теперь храним только путь к прикрепленному файлу
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application(
    navController: NavController,
    contractNumber: String? = null
) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    val fieldColor = Color.White
    val context = LocalContext.current

    // Keep only this single declaration of customerType
    val customerType = when {
        AccountData1.organizationName.isNotEmpty() -> "juridical"
        AccountData2.fioText.isNotEmpty() -> "physical"
        else -> "juridical"
    }

    var forms by remember {
        mutableStateOf(
            listOf(
                ApplicationForm(
                    id = 1,
                    contractNumber = contractNumber ?: "",
                    contractDate = AgreementData.date
                )
            )
        )
    }
    var showSuccessMessage by remember { mutableStateOf(false) }

    var fileContent by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val currentForm = forms.lastOrNull() ?: return@let

            try {
                // Чтение содержимого файла как текста
                context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = reader.readText()
                    fileContent = content

                    // Сохраняем файл как обычно
                    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
                    if (!docsDir.exists()) {
                        docsDir.mkdirs()
                    }

                    val fileName = "Приложение_${currentForm.id}_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_attached${getFileExtension(selectedUri, context)}"
                    val file = File(docsDir, fileName)

                    FileOutputStream(file).use { output ->
                        inputStream.copyTo(output)
                    }

                    forms = forms.map {
                        if (it.id == currentForm.id) it.copy(attachedFilePath = file.absolutePath) else it
                    }

                    Toast.makeText(context, "Файл загружен и прочитан", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Ошибка чтения файла", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(contractNumber) {
        if (contractNumber != null && forms.firstOrNull()?.contractNumber.isNullOrEmpty()) {
            forms = listOf(
                ApplicationForm(
                    id = 1,
                    contractNumber = contractNumber,
                    contractDate = AgreementData.date
                )
            )
        }
    }

    fun addNewForm() {
        forms = forms + ApplicationForm(
            id = if (forms.isEmpty()) 1 else forms.maxOf { it.id } + 1,
            contractNumber = contractNumber ?: "",
            contractDate = AgreementData.date
        )
    }


    fun handleFileAttach(form: ApplicationForm) {
        filePickerLauncher.launch("*/*")
    }

    // Функция для сохранения приложения в базу данных
    fun saveApplicationToDatabase(form: ApplicationForm, contractId: Long): Boolean {
        return try {
            val dbHelper = DatabaseHelper(context)
            val userId = AccountData.userId ?: return false

            val cursor = dbHelper.getContractorDetails(userId)
            val contractorId = if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTRACTOR_ID))
            } else {
                cursor.close()
                return false
            }
            cursor.close()

            // Сохраняем контракт, если его еще нет
            val contractCursor = dbHelper.getContractByNumber(form.contractNumber)
            val contractIdToUse = if (contractCursor.moveToFirst()) {
                contractCursor.getLong(contractCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTRACT_ID))
            } else {
                dbHelper.saveContract(
                    contractNumber = form.contractNumber,
                    serviceName = "Приложение №${form.id}",
                    contractDate = form.contractDate,
                    customerFIO = AgreementData.customerFIO,
                    executorFIO = AgreementData.executorFIO,
                    customerType = customerType,
                    contractorId = contractorId
                )
            }
            contractCursor.close()

            // Сохраняем приложение
            dbHelper.saveApplication(
                applicationNumber = "Приложение №${form.id}",
                applicationDate = SimpleDateFormat("dd.MM.yyyy").format(Date()),
                attachedFilePath = form.attachedFilePath,
                contractId = contractIdToUse
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveAllApplications(): Boolean {
        return try {
            forms.forEach { form ->
                saveApplicationToDatabase(form, 0) // Убрана проверка на false
            }

            AgreementData.serviceName = forms.joinToString { "Приложение №${it.id}" }
            AccountData.director = AccountData.director
            AccountData.authority = AccountData.authority
            AgreementData.customerFIO = AgreementData.customerFIO
            AgreementData.executorFIO = AgreementData.executorFIO

            showSuccessMessage = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при сохранении приложений", Toast.LENGTH_SHORT).show()
            false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myColor)
    ) {
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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

            forms.forEach { form ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Приложение № ${form.id}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = textColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = form.contractNumber,
                            onValueChange = { newValue ->
                                forms = forms.map {
                                    if (it.id == form.id) it.copy(contractNumber = newValue) else it
                                }
                            },
                            label = { Text("№ договора") },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = form.contractDate,
                            onValueChange = { newValue ->
                                forms = forms.map {
                                    if (it.id == form.id) it.copy(contractDate = newValue) else it
                                }
                            },
                            label = { Text("дата") },
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp) // Уменьшили высоту для кнопки прикрепления
                                .background(fieldColor)
                                .border(1.dp, textColor)
                                .clickable { handleFileAttach(form) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (form.attachedFilePath != null) {
                                Text(
                                    "Файл прикреплен\n${File(form.attachedFilePath).name}",
                                    color = textColor,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    "Нажмите для прикрепления файла",
                                    color = textColor
                                )
                            }
                        }

                        // Добавьте новый блок для отображения содержимого файла
                        if (fileContent != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Высота для отображения текста
                                    .background(fieldColor)
                                    .border(1.dp, textColor)
                                    .verticalScroll(rememberScrollState()),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text(
                                    text = fileContent ?: "",
                                    color = textColor,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        saveAllApplications()
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "applications",
                            forms
                        )
                        navController.navigate("acts/$customerType")
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

                Button(
                    onClick = {
                        if (forms.isNotEmpty() && forms.last().contractNumber.isNotEmpty()) {
                            addNewForm()
                        } else {
                            Toast.makeText(context, "Заполните текущее приложение перед созданием нового", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp)
                        .border(width = 2.dp, color = Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F7FC),
                    )
                ) {
                    Text("Создать еще приложение", color = textColor)
                }

                Button(
                    onClick = {
                        if (saveAllApplications()) {
                            val firstForm = forms.firstOrNull()
                            if (firstForm != null) {
                                navController.navigate("addagr/${customerType}/${firstForm.contractNumber}/${AgreementData.date}")
                            } else {
                                Toast.makeText(context, "Нет данных для создания доп. соглашения", Toast.LENGTH_SHORT).show()
                            }
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
                    Text("Создать доп. соглашение", color = textColor)
                }
            }
        }
    }
}

// Функция для получения расширения файла
fun getFileExtension(uri: Uri, context: Context): String {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.let { ".$it" } ?: ""
}

@Preview(showBackground = true)
@Composable
fun ApplicationPreview() {
    Package_of_DocumentsbyRogozinTheme {
        Application(
            navController = rememberNavController(),
            //contractNumber = "123"
        )
    }
}