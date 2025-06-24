package com.package_of_documents.byrogozin

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String) -> Unit = { _ -> }
) {
    val myColor = Color(0xFFF5F7FC)
    val textColor = Color(0xFF6A1D24)
    val buttonColor = Color(0xFF1D246A)
    val errorColor = Color(0xFFD32F2F)
    val context = LocalContext.current

    // Состояния для полей ввода
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Функция для обработки авторизации
    fun handleLogin() {
        val currentLogin = login.trim()
        val currentPassword = password.trim()

        if (currentLogin.isBlank()) {
            errorMessage = "Введите логин"
            return
        }

        if (currentPassword.isBlank()) {
            errorMessage = "Введите пароль"
            return
        }

        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("=== LOGIN ATTEMPT ===")
                println("Login: '$currentLogin'")
                println("Password: '$currentPassword'")

                val passwordHash = hashPassword(currentPassword)
                val dbHelper = DatabaseHelper(context)
                dbHelper.debugAllUsers()

                // Проверяем существование пользователя
                if (!dbHelper.checkUserExists(currentLogin)) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Пользователь с таким логином не найден"
                        isLoading = false
                    }
                    return@launch
                }

                // Получаем хэш из базы
                val storedHash = dbHelper.getUserPasswordHash(currentLogin)
                println("Stored hash from DB: $storedHash")

                // Сравниваем хэши
                if (passwordHash != storedHash) {
                    println("=== HASH COMPARISON FAILED ===")
                    println("Input hash: $passwordHash")
                    println("Stored hash: $storedHash")
                    println("Hashes equal: ${passwordHash == storedHash}")

                    withContext(Dispatchers.Main) {
                        errorMessage = "Неверный пароль"
                        isLoading = false
                    }
                    return@launch
                }

                // Успешная авторизация
                withContext(Dispatchers.Main) {
                    val role = dbHelper.getUserRole(currentLogin)
                    onLoginSuccess(role)
                    navController.navigate("person") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Ошибка авторизации: ${e.localizedMessage}"
                    println("Auth error: ${e.stackTraceToString()}")
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Авторизация",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
                color = textColor
            )

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorMessage != null
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = errorColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { handleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Авторизация", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Функция для хэширования пароля (SHA-256)
private fun hashPassword(password: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray(Charsets.UTF_8)
        println("Password bytes before hash: ${bytes.contentToString()}")
        val hash = digest.digest(bytes)
        val hexString = hash.fold("") { str, it -> str + "%02x".format(it) }
        println("Generated hash: $hexString")
        hexString
    } catch (e: Exception) {
        println("Hashing error: ${e.message}")
        password
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    Package_of_DocumentsbyRogozinTheme {
        LoginScreen(
            navController = rememberNavController(),
            onLoginSuccess = { _ -> }
        )
    }
}