package com.package_of_documents.byrogozin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeOfFaceScreen(navController: NavController) {
    val myColor = Color(0xFFF5F7FC)
    val borderColor = Color(0xFF1D246A)
    val textColor = Color(0xFF6A1D24)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

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
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Выберите тип лица",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
                    .clickable { expanded = true }
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedOption.ifEmpty { "Выберите тип" },
                    fontSize = 18.sp,
                    color = if (selectedOption.isEmpty()) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Юридическое лицо/ИП", color = textColor) },
                        onClick = {
                            selectedOption = "Юридическое лицо/ИП"
                            expanded = false
                            navController.navigate("recyur") // Переход к экрану реквизитов
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Физическое лицо", color = textColor) },
                        onClick = {
                            selectedOption = "Физическое лицо"
                            expanded = false
                            navController.navigate("fisface")
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TypeOfFaceScreenPreview() {
    MaterialTheme {
        TypeOfFaceScreen(navController = rememberNavController())
    }
}
