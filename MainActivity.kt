package com.package_of_documents.byrogozin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.package_of_documents.byrogozin.ui.theme.Package_of_DocumentsbyRogozinTheme

class MainActivity : ComponentActivity() {
    private val sharedPrefName = "AccountDataPrefs"
    private val authPrefName = "AuthPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Загрузка данных аккаунта
        loadAccountData()
        loadAccountData1()
        loadAccountData2()

        // Проверка авторизации
        val isLoggedIn = checkAuthStatus()

        setContent {
            Package_of_DocumentsbyRogozinTheme {
                AppNavigation(isLoggedIn)
            }
        }
    }

    // Проверка статуса авторизации
    private fun checkAuthStatus(): Boolean {
        val sharedPref = getSharedPreferences(authPrefName, Context.MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    // Сохранение статуса авторизации
    fun saveAuthStatus(token: String) {
        val sharedPref = getSharedPreferences(authPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_logged_in", true)
            putString("auth_token", token)
            apply()
        }
    }

    // Очистка статуса авторизации (для выхода)
    fun clearAuthStatus() {
        val sharedPref = getSharedPreferences(authPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        saveAccountData()
        saveAccountData1()
        saveAccountData2()
    }

    override fun onStop() {
        super.onStop()
        saveAccountData()
        saveAccountData1()
        saveAccountData2()
    }

    private fun saveAccountData() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("organizationName", AccountData.organizationName)
            putString("legalAddress", AccountData.legalAddress)
            putString("actualAddress", AccountData.actualAddress)
            putString("postalAddress", AccountData.postalAddress)
            putString("inn", AccountData.inn)
            putString("kpp", AccountData.kpp)
            putString("ogrn", AccountData.ogrn)
            putString("accountNumber", AccountData.accountNumber)
            putString("correspondentAccount", AccountData.correspondentAccount)
            putString("bic", AccountData.bic)
            putString("bankName", AccountData.bankName)
            putString("phone", AccountData.phone)
            putString("email", AccountData.email)
            putString("director", AccountData.director)
            putString("authority", AccountData.authority)
            putString("fullName", AccountData.fullName)
            putBoolean("addressesMatch", AccountData.addressesMatch)
            putBoolean("isEditingEnabled", AccountData.isEditingEnabled)
            apply()
        }
    }

    private fun loadAccountData() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        AccountData.organizationName = sharedPref.getString("organizationName", "") ?: ""
        AccountData.legalAddress = sharedPref.getString("legalAddress", "") ?: ""
        AccountData.actualAddress = sharedPref.getString("actualAddress", "") ?: ""
        AccountData.postalAddress = sharedPref.getString("postalAddress", "") ?: ""
        AccountData.inn = sharedPref.getString("inn", "") ?: ""
        AccountData.kpp = sharedPref.getString("kpp", "") ?: ""
        AccountData.ogrn = sharedPref.getString("ogrn", "") ?: ""
        AccountData.accountNumber = sharedPref.getString("accountNumber", "") ?: ""
        AccountData.correspondentAccount = sharedPref.getString("correspondentAccount", "") ?: ""
        AccountData.bic = sharedPref.getString("bic", "") ?: ""
        AccountData.bankName = sharedPref.getString("bankName", "") ?: ""
        AccountData.phone = sharedPref.getString("phone", "") ?: ""
        AccountData.email = sharedPref.getString("email", "") ?: ""
        AccountData.director = sharedPref.getString("director", "") ?: ""
        AccountData.authority = sharedPref.getString("authority", "") ?: ""
        AccountData.fullName = sharedPref.getString("fullName", "") ?: ""
        AccountData.addressesMatch = sharedPref.getBoolean("addressesMatch", false)
        AccountData.isEditingEnabled = sharedPref.getBoolean("isEditingEnabled", true)
    }

    private fun saveAccountData1() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("organizationName1", AccountData1.organizationName)
            putString("legalAddress1", AccountData1.legalAddress)
            putString("actualAddress1", AccountData1.actualAddress)
            putString("postalAddress1", AccountData1.postalAddress)
            putString("inn1", AccountData1.inn)
            putString("kpp1", AccountData1.kpp)
            putString("ogrn1", AccountData1.ogrn)
            putString("accountNumber1", AccountData1.accountNumber)
            putString("correspondentAccount1", AccountData1.correspondentAccount)
            putString("bic1", AccountData1.bic)
            putString("bankName1", AccountData1.bankName)
            putString("phone1", AccountData1.phone)
            putString("email1", AccountData1.email)
            putString("director1", AccountData1.director)
            putString("authority1", AccountData1.authority)
            putString("fullName1", AccountData1.fullName)
            putBoolean("addressesMatch1", AccountData1.addressesMatch)
            putBoolean("isEditingEnabled1", AccountData1.isEditingEnabled)
            apply()
        }
    }

    private fun loadAccountData1() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        AccountData1.organizationName = sharedPref.getString("organizationName1", "") ?: ""
        AccountData1.legalAddress = sharedPref.getString("legalAddress1", "") ?: ""
        AccountData1.actualAddress = sharedPref.getString("actualAddress1", "") ?: ""
        AccountData1.postalAddress = sharedPref.getString("postalAddress1", "") ?: ""
        AccountData1.inn = sharedPref.getString("inn1", "") ?: ""
        AccountData1.kpp = sharedPref.getString("kpp1", "") ?: ""
        AccountData1.ogrn = sharedPref.getString("ogrn1", "") ?: ""
        AccountData1.accountNumber = sharedPref.getString("accountNumber1", "") ?: ""
        AccountData1.correspondentAccount = sharedPref.getString("correspondentAccount1", "") ?: ""
        AccountData1.bic = sharedPref.getString("bic1", "") ?: ""
        AccountData1.bankName = sharedPref.getString("bankName1", "") ?: ""
        AccountData1.phone = sharedPref.getString("phone1", "") ?: ""
        AccountData1.email = sharedPref.getString("email1", "") ?: ""
        AccountData1.director = sharedPref.getString("director1", "") ?: ""
        AccountData1.authority = sharedPref.getString("authority1", "") ?: ""
        AccountData1.fullName = sharedPref.getString("fullName1", "") ?: ""
        AccountData1.addressesMatch = sharedPref.getBoolean("addressesMatch1", false)
        AccountData1.isEditingEnabled = sharedPref.getBoolean("isEditingEnabled1", true)
    }

    private fun saveAccountData2() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selectedOption2", AccountData2.selectedOption)
            putString("fioText2", AccountData2.fioText)
            putString("selectedCountry2", AccountData2.selectedCountry)
            putString("passportSeries2", AccountData2.passportSeries)
            putString("passportNumber2", AccountData2.passportNumber)
            putString("issuedBy2", AccountData2.issuedBy)
            putString("issueDate2", AccountData2.issueDate)
            putString("departmentCode2", AccountData2.departmentCode)
            apply()
        }
    }

    private fun loadAccountData2() {
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        AccountData2.selectedOption = sharedPref.getString("selectedOption2", "Выберите тип значения") ?: "Выберите тип значения"
        AccountData2.fioText = sharedPref.getString("fioText2", "") ?: ""
        AccountData2.selectedCountry = sharedPref.getString("selectedCountry2", "РФ") ?: "РФ"
        AccountData2.passportSeries = sharedPref.getString("passportSeries2", "") ?: ""
        AccountData2.passportNumber = sharedPref.getString("passportNumber2", "") ?: ""
        AccountData2.issuedBy = sharedPref.getString("issuedBy2", "") ?: ""
        AccountData2.issueDate = sharedPref.getString("issueDate2", "") ?: ""
        AccountData2.departmentCode = sharedPref.getString("departmentCode2", "") ?: ""
    }
}

@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController) { token ->
                // Сохраняем статус авторизации при успешном входе
                (navController.context as MainActivity).saveAuthStatus(token)
                // Навигация будет обрабатываться внутри LoginScreen в зависимости от наличия реквизитов
            }
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onLogout = {
                    (navController.context as MainActivity).clearAuthStatus()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("person") { PersonalAccountScreen(navController) }
        composable("face") { TypeOfFaceScreen(navController) }
        composable("recyur") { RecvisittsYurIP(navController) }
        composable("fisface") { FisicheskoeFace(navController) }
        composable(
            route = "agreem/{customerType}",
            arguments = listOf(navArgument("customerType") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerType = backStackEntry.arguments?.getString("customerType") ?: ""
            Agreement(navController, customerType)
        }
        composable(
            route = "applic/{contractNumber}",
            arguments = listOf(navArgument("contractNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val contractNumber = backStackEntry.arguments?.getString("contractNumber") ?: ""
            Application(navController, contractNumber)
        }
        composable(
            route = "addagr/{customerType}/{contractNumber}/{contractDate}",
            arguments = listOf(
                navArgument("customerType") { type = NavType.StringType },
                navArgument("contractNumber") { type = NavType.StringType },
                navArgument("contractDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerType = backStackEntry.arguments?.getString("customerType") ?: ""
            val contractNumber = backStackEntry.arguments?.getString("contractNumber") ?: ""
            val contractDate = backStackEntry.arguments?.getString("contractDate") ?: ""
            AddAgreement(
                navController = navController,
                customerType = customerType,
                contractNumberFromParent = contractNumber,
                contractDateFromParent = contractDate
            )
        }
        composable(
            route = "acts/{customerType}",
            arguments = listOf(
                navArgument("customerType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerType = backStackEntry.arguments?.getString("customerType") ?: ""
            val applications = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<ApplicationForm>>("applications") ?: emptyList()
            val contractNumber = applications.firstOrNull()?.contractNumber ?: ""
            val contractDate = applications.firstOrNull()?.contractDate ?: ""

            Acts(
                navController = navController,
                customerType = customerType,
                contractNumber = contractNumber,
                contractDate = contractDate,
                applications = applications
            )
        }
    }
}
