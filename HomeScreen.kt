package com.package_of_documents.byrogozin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


import androidx.compose.material.icons.filled.ExitToApp

import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit // Добавлен параметр для обработки выхода
) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    val buttonColor = Color(0xFF1D246A)

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
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя панель с иконками
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                // Иконка личного кабинета
                IconButton(
                    onClick = { navController.navigate("person") },
                    modifier = Modifier.size(65.dp)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Личный кабинет",
                        modifier = Modifier.size(100.dp)
                    )
                }

                // Кнопка выхода в правом верхнем углу
                IconButton(
                    onClick = { onLogout() },
                    modifier = Modifier
                        .size(65.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.ExitToApp,
                        contentDescription = "Выход",
                        modifier = Modifier.size(60.dp),
                        tint = textColor
                    )
                }
            }

            // Центральный элемент - кнопка создания договора
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate("face") },
                    modifier = Modifier
                        .width(250.dp)
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Создать договор",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Пустое пространство внизу для баланса
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Package_of_DocumentsbyRogozinTheme {
        HomeScreen(
            navController = rememberNavController(),
            onLogout = {} // Пустой обработчик для превью
        )
    }
}
