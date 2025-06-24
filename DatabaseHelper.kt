package com.package_of_documents.byrogozin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contracts_db"
        private const val DATABASE_VERSION = 1

        // Таблица Roles
        const val TABLE_ROLES = "Roles"
        const val COLUMN_ROLE_ID = "RoleId"
        const val COLUMN_ROLE_NAME = "RoleName"
        const val COLUMN_DESCRIPTION = "Description"

        // Таблица Users
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "UserId"
        const val COLUMN_USERNAME = "Username"
        const val COLUMN_PASSWORD_HASH = "PasswordHash"
        /*const val COLUMN_EMAIL = "Email"
        const val COLUMN_FULL_NAME = "FullName"
        const val COLUMN_PHONE = "Phone"*/
        const val COLUMN_ROLE_ID_FK = "RoleId"
        const val COLUMN_CREATED_AT = "CreatedAt"
        const val COLUMN_LAST_LOGIN = "LastLogin"
        const val COLUMN_IS_ACTIVE = "IsActive"

        const val TABLE_CONTRACTOR_DETAILS = "ContractorDetails"
        const val COLUMN_CONTRACTOR_ID = "ContractorId"
        const val COLUMN_ORGANIZATION_NAME = "OrganizationName"
        const val COLUMN_LEGAL_ADDRESS = "LegalAddress"
        const val COLUMN_ACTUAL_ADDRESS = "ActualAddress"
        const val COLUMN_POSTAL_ADDRESS = "PostalAddress"
        const val COLUMN_INN = "INN"
        const val COLUMN_KPP = "KPP"
        const val COLUMN_OGRN = "OGRN"
        const val COLUMN_ACCOUNT_NUMBER = "AccountNumber"
        const val COLUMN_CORRESPONDENT_ACCOUNT = "CorrespondentAccount"
        const val COLUMN_BIC = "BIC"
        const val COLUMN_BANK_NAME = "BankName"
        const val COLUMN_PHONE = "Phone"
        const val COLUMN_EMAIL = "Email"
        const val COLUMN_DIRECTOR = "Director"
        const val COLUMN_AUTHORITY = "Authority"
        const val COLUMN_FULL_NAME = "FullName"
        const val COLUMN_ADDRESSES_MATCH = "AddressesMatch"
        const val COLUMN_USER_ID_FK = "UserId"

        const val TABLE_CONTRACTS = "Contracts"
        const val COLUMN_CONTRACT_ID = "ContractId"
        const val COLUMN_CONTRACT_NUMBER = "ContractNumber"
        const val COLUMN_SERVICE_NAME = "ServiceName"
        const val COLUMN_CONTRACT_DATE = "ContractDate"
        const val COLUMN_CUSTOMER_FIO = "CustomerFIO"
        const val COLUMN_EXECUTOR_FIO = "ExecutorFIO"
        const val COLUMN_CUSTOMER_TYPE = "CustomerType" // "juridical" или "physical"
        const val COLUMN_CONTRACTOR_ID_FK = "ContractorId" // Ссылка на таблицу ContractorDetails

        const val TABLE_APPLICATIONS = "Applications"
        const val COLUMN_APPLICATION_ID = "ApplicationId"
        const val COLUMN_APPLICATION_NUMBER = "ApplicationNumber"
        const val COLUMN_APPLICATION_DATE = "ApplicationDate"
        const val COLUMN_ATTACHED_FILE_PATH = "AttachedFilePath"
        const val COLUMN_CONTRACT_ID_FK = "ContractId"

        const val TABLE_ADDITIONAL_AGREEMENTS = "AdditionalAgreements"
        const val COLUMN_AGREEMENT_ID = "AgreementId"
        const val COLUMN_AGREEMENT_NUMBER = "AgreementNumber"
        const val COLUMN_AGREEMENT_DATE = "AgreementDate"
        const val COLUMN_CONTRACT_NUMBER_REF = "ContractNumber"
        const val COLUMN_CONTRACT_DATE_REF = "ContractDate"
        const val COLUMN_CUSTOMER_FIO_1 = "CustomerFIO"
        const val COLUMN_EXECUTOR_FIO_1 = "ExecutorFIO"
        const val COLUMN_AGREEMENT_ITEMS = "AgreementItems" // JSON-строка с пунктами соглашения
        const val COLUMN_CONTRACTOR_ID_FK_AGREEMENT = "ContractorId" // Ссылка на ContractorDetails

        // Добавьте эти константы в companion object класса DatabaseHelper
        const val TABLE_ACTS = "Acts"
        const val COLUMN_ACT_ID = "ActId"
        const val COLUMN_ACT_TYPE = "ActType" // Тип акта (УПД, 1С и т.д.)
        const val COLUMN_ACT_NUMBER = "ActNumber" // Номер акта
        const val COLUMN_ACT_DATE = "ActDate" // Дата акта
        const val COLUMN_CONTRACT_NUMBER_REF_ACT = "ContractNumber" // Ссылка на номер договора
        const val COLUMN_CONTRACT_DATE_REF_ACT = "ContractDate" // Ссылка на дату договора
        const val COLUMN_SERVICE_NAME_ACT = "ServiceName" // Наименование услуги
        const val COLUMN_QUANTITY = "Quantity" // Количество
        const val COLUMN_COST = "Cost" // Стоимость
        const val COLUMN_CUSTOMER_FIO_ACT = "CustomerFIO" // ФИО заказчика
        const val COLUMN_EXECUTOR_FIO_ACT = "ExecutorFIO" // ФИО исполнителя
        const val COLUMN_VAT_STATUS = "VatStatus" // Статус НДС (для УПД)
        const val COLUMN_CONTRACTOR_ID_FK_ACT = "ContractorId" // Ссылка на ContractorDetails

    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создание таблицы Roles
        val createRolesTable = """
            CREATE TABLE $TABLE_ROLES (
                $COLUMN_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ROLE_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()

        // Создание таблицы Users
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD_HASH TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL UNIQUE,
                $COLUMN_ROLE_ID_FK INTEGER NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_LAST_LOGIN DATETIME,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1,
                FOREIGN KEY ($COLUMN_ROLE_ID_FK) REFERENCES $TABLE_ROLES($COLUMN_ROLE_ID)
            )
        """.trimIndent()

        val createContractorDetailsTable = """
        CREATE TABLE $TABLE_CONTRACTOR_DETAILS (
            $COLUMN_CONTRACTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ORGANIZATION_NAME TEXT NOT NULL,
            $COLUMN_LEGAL_ADDRESS TEXT NOT NULL,
            $COLUMN_ACTUAL_ADDRESS TEXT NOT NULL,
            $COLUMN_POSTAL_ADDRESS TEXT NOT NULL,
            $COLUMN_INN TEXT NOT NULL,
            $COLUMN_KPP TEXT NOT NULL,
            $COLUMN_OGRN TEXT NOT NULL,
            $COLUMN_ACCOUNT_NUMBER TEXT NOT NULL,
            $COLUMN_CORRESPONDENT_ACCOUNT TEXT NOT NULL,
            $COLUMN_BIC TEXT NOT NULL,
            $COLUMN_BANK_NAME TEXT NOT NULL,
            $COLUMN_PHONE TEXT NOT NULL,
            $COLUMN_EMAIL TEXT NOT NULL,
            $COLUMN_DIRECTOR TEXT NOT NULL,
            $COLUMN_AUTHORITY TEXT NOT NULL,
            $COLUMN_FULL_NAME TEXT NOT NULL,
            $COLUMN_ADDRESSES_MATCH INTEGER DEFAULT 0,
            $COLUMN_USER_ID_FK INTEGER NOT NULL,
            FOREIGN KEY ($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
        )
    """.trimIndent()

        val createContractsTable = """
    CREATE TABLE $TABLE_CONTRACTS (
        $COLUMN_CONTRACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_CONTRACT_NUMBER TEXT NOT NULL UNIQUE,
        $COLUMN_SERVICE_NAME TEXT NOT NULL,
        $COLUMN_CONTRACT_DATE TEXT NOT NULL,
        $COLUMN_CUSTOMER_FIO TEXT NOT NULL,
        $COLUMN_EXECUTOR_FIO TEXT NOT NULL,
        $COLUMN_CUSTOMER_TYPE TEXT NOT NULL,
        $COLUMN_CONTRACTOR_ID_FK INTEGER NOT NULL,
        FOREIGN KEY ($COLUMN_CONTRACTOR_ID_FK) REFERENCES $TABLE_CONTRACTOR_DETAILS($COLUMN_CONTRACTOR_ID)
    )
""".trimIndent()
        val createApplicationsTable = """
    CREATE TABLE $TABLE_APPLICATIONS (
        $COLUMN_APPLICATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_APPLICATION_NUMBER TEXT NOT NULL,
        $COLUMN_APPLICATION_DATE TEXT NOT NULL,
        $COLUMN_ATTACHED_FILE_PATH TEXT,
        $COLUMN_CONTRACT_ID_FK INTEGER NOT NULL,
        FOREIGN KEY ($COLUMN_CONTRACT_ID_FK) REFERENCES $TABLE_CONTRACTS($COLUMN_CONTRACT_ID)
    )
""".trimIndent()
        val createAdditionalAgreementsTable = """
    CREATE TABLE $TABLE_ADDITIONAL_AGREEMENTS (
        $COLUMN_AGREEMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_AGREEMENT_NUMBER TEXT NOT NULL UNIQUE,
        $COLUMN_AGREEMENT_DATE TEXT NOT NULL,
        $COLUMN_CONTRACT_NUMBER_REF TEXT NOT NULL,
        $COLUMN_CONTRACT_DATE_REF TEXT NOT NULL,
        $COLUMN_CUSTOMER_FIO_1 TEXT NOT NULL,
        $COLUMN_EXECUTOR_FIO_1 TEXT NOT NULL,
        $COLUMN_AGREEMENT_ITEMS TEXT NOT NULL,
        $COLUMN_CONTRACTOR_ID_FK_AGREEMENT INTEGER NOT NULL,
        FOREIGN KEY ($COLUMN_CONTRACTOR_ID_FK_AGREEMENT) REFERENCES $TABLE_CONTRACTOR_DETAILS($COLUMN_CONTRACTOR_ID)
    )
""".trimIndent()

        // Добавьте этот SQL в метод onCreate() после создания других таблиц
        val createActsTable = """
    CREATE TABLE $TABLE_ACTS (
        $COLUMN_ACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_ACT_TYPE TEXT NOT NULL,
        $COLUMN_ACT_NUMBER TEXT NOT NULL,
        $COLUMN_ACT_DATE TEXT NOT NULL,
        $COLUMN_CONTRACT_NUMBER_REF_ACT TEXT NOT NULL,
        $COLUMN_CONTRACT_DATE_REF_ACT TEXT NOT NULL,
        $COLUMN_SERVICE_NAME_ACT TEXT NOT NULL,
        $COLUMN_QUANTITY TEXT,
        $COLUMN_COST TEXT NOT NULL,
        $COLUMN_CUSTOMER_FIO_ACT TEXT NOT NULL,
        $COLUMN_EXECUTOR_FIO_ACT TEXT NOT NULL,
        $COLUMN_VAT_STATUS INTEGER,
        $COLUMN_CONTRACTOR_ID_FK_ACT INTEGER NOT NULL,
        FOREIGN KEY ($COLUMN_CONTRACTOR_ID_FK_ACT) REFERENCES $TABLE_CONTRACTOR_DETAILS($COLUMN_CONTRACTOR_ID)
    )
""".trimIndent()

        db.execSQL(createActsTable)
        db.execSQL(createRolesTable)
        db.execSQL(createUsersTable)
        db.execSQL(createContractorDetailsTable)
        db.execSQL(createContractsTable)
        db.execSQL(createApplicationsTable)
        db.execSQL(createAdditionalAgreementsTable)
        // Вставка начальных данных
        insertInitialData(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Вставка ролей
        db.execSQL("INSERT INTO $TABLE_ROLES ($COLUMN_ROLE_NAME, $COLUMN_DESCRIPTION) VALUES ('Client', 'Клиент - может заполнять и сохранять договоры')")
        db.execSQL("INSERT INTO $TABLE_ROLES ($COLUMN_ROLE_NAME, $COLUMN_DESCRIPTION) VALUES ('Lawyer', 'Юрист - может создавать шаблоны и управлять клиентами')")

        // Хэши для тестовых паролей (SHA-256)
        val lawyerPasswordHash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8" // "password"
        val clientPasswordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f" // "password123"

        // Вставка тестового пользователя (юриста)
        db.execSQL("""
            INSERT INTO $TABLE_USERS (
                $COLUMN_USERNAME, $COLUMN_PASSWORD_HASH, $COLUMN_EMAIL,
                $COLUMN_FULL_NAME, $COLUMN_PHONE, $COLUMN_ROLE_ID_FK
            ) VALUES (
                'lawyer1',
                '$lawyerPasswordHash',
                'lawyer1@example.com',
                'Иванов Иван Иванович',
                '+79991234567',
                2
            )
        """.trimIndent())

        // Вставка тестового пользователя (клиента)
        db.execSQL("""
            INSERT INTO $TABLE_USERS (
                $COLUMN_USERNAME, $COLUMN_PASSWORD_HASH, $COLUMN_EMAIL,
                $COLUMN_FULL_NAME, $COLUMN_PHONE, $COLUMN_ROLE_ID_FK
            ) VALUES (
                'client1',
                '$clientPasswordHash',
                'client1@example.com',
                'Петров Петр Петрович',
                '+79998765432',
                1
            )
        """.trimIndent())
    }

    fun saveContractorDetails(
        userId: Long,
        organizationName: String,
        legalAddress: String,
        actualAddress: String,
        postalAddress: String,
        inn: String,
        kpp: String,
        ogrn: String,
        accountNumber: String,
        correspondentAccount: String,
        bic: String,
        bankName: String,
        phone: String,
        email: String,
        director: String,
        authority: String,
        fullName: String,
        addressesMatch: Boolean
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ORGANIZATION_NAME, organizationName)
            put(COLUMN_LEGAL_ADDRESS, legalAddress)
            put(COLUMN_ACTUAL_ADDRESS, actualAddress)
            put(COLUMN_POSTAL_ADDRESS, postalAddress)
            put(COLUMN_INN, inn)
            put(COLUMN_KPP, kpp)
            put(COLUMN_OGRN, ogrn)
            put(COLUMN_ACCOUNT_NUMBER, accountNumber)
            put(COLUMN_CORRESPONDENT_ACCOUNT, correspondentAccount)
            put(COLUMN_BIC, bic)
            put(COLUMN_BANK_NAME, bankName)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_DIRECTOR, director)
            put(COLUMN_AUTHORITY, authority)
            put(COLUMN_FULL_NAME, fullName)
            put(COLUMN_ADDRESSES_MATCH, if (addressesMatch) 1 else 0)
            put(COLUMN_USER_ID_FK, userId)
        }

        return db.insert(TABLE_CONTRACTOR_DETAILS, null, values)
    }

    fun updateContractorDetails(
        contractorId: Long,
        organizationName: String,
        legalAddress: String,
        actualAddress: String,
        postalAddress: String,
        inn: String,
        kpp: String,
        ogrn: String,
        accountNumber: String,
        correspondentAccount: String,
        bic: String,
        bankName: String,
        phone: String,
        email: String,
        director: String,
        authority: String,
        fullName: String,
        addressesMatch: Boolean
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ORGANIZATION_NAME, organizationName)
            put(COLUMN_LEGAL_ADDRESS, legalAddress)
            put(COLUMN_ACTUAL_ADDRESS, actualAddress)
            put(COLUMN_POSTAL_ADDRESS, postalAddress)
            put(COLUMN_INN, inn)
            put(COLUMN_KPP, kpp)
            put(COLUMN_OGRN, ogrn)
            put(COLUMN_ACCOUNT_NUMBER, accountNumber)
            put(COLUMN_CORRESPONDENT_ACCOUNT, correspondentAccount)
            put(COLUMN_BIC, bic)
            put(COLUMN_BANK_NAME, bankName)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_DIRECTOR, director)
            put(COLUMN_AUTHORITY, authority)
            put(COLUMN_FULL_NAME, fullName)
            put(COLUMN_ADDRESSES_MATCH, if (addressesMatch) 1 else 0)
        }

        return db.update(
            TABLE_CONTRACTOR_DETAILS,
            values,
            "$COLUMN_CONTRACTOR_ID = ?",
            arrayOf(contractorId.toString())
        )
    }

    fun getContractorDetails(userId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_CONTRACTOR_DETAILS 
        WHERE $COLUMN_USER_ID_FK = ?
        """.trimIndent(),
            arrayOf(userId.toString())
        )
    }

    fun deleteContractorDetails(contractorId: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_CONTRACTOR_DETAILS,
            "$COLUMN_CONTRACTOR_ID = ?",
            arrayOf(contractorId.toString())
        )
    }

    fun authenticateUser(username: String, passwordHash: String): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT $COLUMN_PASSWORD_HASH 
        FROM $TABLE_USERS 
        WHERE $COLUMN_USERNAME = ? AND $COLUMN_IS_ACTIVE = 1
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username))
        return if (cursor.moveToFirst()) {
            val hashIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)
            val storedHash = cursor.getString(hashIndex)
            storedHash.equals(passwordHash, ignoreCase = true)
        } else {
            false
        }.also { cursor.close() }
    }

    fun debugAllUsers() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)

        println("=== DATABASE USERS ===")
        while (cursor.moveToNext()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val hash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH))
            println("User: $username, Hash: $hash")
        }
        cursor.close()
    }

    fun debugUserInfo(username: String) {
        val db = this.readableDatabase
        val query = """
        SELECT * FROM $TABLE_USERS 
        WHERE $COLUMN_USERNAME = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username))
        if (cursor.moveToFirst()) {
            val usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_USERNAME)
            val hashIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)
            println("User found: ${cursor.getString(usernameIndex)}")
            println("Stored hash: ${cursor.getString(hashIndex)}")
        } else {
            println("User not found")
        }
        cursor.close()
    }

    fun getUserPasswordHash(username: String): String? {
        val db = this.readableDatabase
        val query = """
        SELECT $COLUMN_PASSWORD_HASH 
        FROM $TABLE_USERS 
        WHERE $COLUMN_USERNAME = ? AND $COLUMN_IS_ACTIVE = 1
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username))
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }.also { cursor.close() }
    }

    fun getUserRole(username: String): String {
        val db = this.readableDatabase
        val query = """
        SELECT r.$COLUMN_ROLE_NAME 
        FROM $TABLE_USERS u
        JOIN $TABLE_ROLES r ON u.$COLUMN_ROLE_ID_FK = r.$COLUMN_ROLE_ID
        WHERE u.$COLUMN_USERNAME = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username))
        return if (cursor.moveToFirst()) {
            val roleIndex = cursor.getColumnIndexOrThrow(COLUMN_ROLE_NAME)
            cursor.getString(roleIndex)
        } else {
            throw IllegalStateException("User or role not found")
        }.also { cursor.close() }
    }

    fun updateLastLogin(username: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LAST_LOGIN, System.currentTimeMillis().toString())
        }
        db.update(TABLE_USERS, values, "$COLUMN_USERNAME = ?", arrayOf(username))
    }

    fun saveContract(
        contractNumber: String,
        serviceName: String,
        contractDate: String,
        customerFIO: String,
        executorFIO: String,
        customerType: String,
        contractorId: Long
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTRACT_NUMBER, contractNumber)
            put(COLUMN_SERVICE_NAME, serviceName)
            put(COLUMN_CONTRACT_DATE, contractDate)
            put(COLUMN_CUSTOMER_FIO, customerFIO)
            put(COLUMN_EXECUTOR_FIO, executorFIO)
            put(COLUMN_CUSTOMER_TYPE, customerType)
            put(COLUMN_CONTRACTOR_ID_FK, contractorId)
        }

        return db.insert(TABLE_CONTRACTS, null, values)
    }

    // Получение списка договоров для исполнителя
    fun getContractsForContractor(contractorId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_CONTRACTS 
        WHERE $COLUMN_CONTRACTOR_ID_FK = ?
        ORDER BY $COLUMN_CONTRACT_DATE DESC
        """.trimIndent(),
            arrayOf(contractorId.toString())
        )
    }

    // Получение договора по номеру
    fun getContractByNumber(contractNumber: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_CONTRACTS 
        WHERE $COLUMN_CONTRACT_NUMBER = ?
        """.trimIndent(),
            arrayOf(contractNumber)
        )
    }

    // Обновление договора
    fun updateContract(
        contractId: Long,
        contractNumber: String,
        serviceName: String,
        contractDate: String,
        customerFIO: String,
        executorFIO: String,
        customerType: String
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTRACT_NUMBER, contractNumber)
            put(COLUMN_SERVICE_NAME, serviceName)
            put(COLUMN_CONTRACT_DATE, contractDate)
            put(COLUMN_CUSTOMER_FIO, customerFIO)
            put(COLUMN_EXECUTOR_FIO, executorFIO)
            put(COLUMN_CUSTOMER_TYPE, customerType)
        }

        return db.update(
            TABLE_CONTRACTS,
            values,
            "$COLUMN_CONTRACT_ID = ?",
            arrayOf(contractId.toString())
        )
    }

    // Удаление договора
    fun deleteContract(contractId: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_CONTRACTS,
            "$COLUMN_CONTRACT_ID = ?",
            arrayOf(contractId.toString())
        )
    }
    fun saveApplication(
        applicationNumber: String,
        applicationDate: String,
        attachedFilePath: String?,
        contractId: Long
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_APPLICATION_NUMBER, applicationNumber)
            put(COLUMN_APPLICATION_DATE, applicationDate)
            put(COLUMN_ATTACHED_FILE_PATH, attachedFilePath)
            put(COLUMN_CONTRACT_ID_FK, contractId)
        }

        return db.insert(TABLE_APPLICATIONS, null, values)
    }

    // Получение списка приложений для договора
    fun getApplicationsForContract(contractId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_APPLICATIONS 
        WHERE $COLUMN_CONTRACT_ID_FK = ?
        ORDER BY $COLUMN_APPLICATION_DATE DESC
        """.trimIndent(),
            arrayOf(contractId.toString())
        )
    }

    // Получение приложения по ID
    fun getApplicationById(applicationId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_APPLICATIONS 
        WHERE $COLUMN_APPLICATION_ID = ?
        """.trimIndent(),
            arrayOf(applicationId.toString())
        )
    }

    // Обновление приложения
    fun updateApplication(
        applicationId: Long,
        applicationNumber: String,
        applicationDate: String,
        attachedFilePath: String?
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_APPLICATION_NUMBER, applicationNumber)
            put(COLUMN_APPLICATION_DATE, applicationDate)
            put(COLUMN_ATTACHED_FILE_PATH, attachedFilePath)
        }

        return db.update(
            TABLE_APPLICATIONS,
            values,
            "$COLUMN_APPLICATION_ID = ?",
            arrayOf(applicationId.toString())
        )
    }

    // Удаление приложения
    fun deleteApplication(applicationId: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_APPLICATIONS,
            "$COLUMN_APPLICATION_ID = ?",
            arrayOf(applicationId.toString())
        )
    }

    fun saveAdditionalAgreement(
        agreementNumber: String,
        agreementDate: String,
        contractNumber: String,
        contractDate: String,
        customerFIO: String,
        executorFIO: String,
        agreementItems: List<String>,
        contractorId: Long
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AGREEMENT_NUMBER, agreementNumber)
            put(COLUMN_AGREEMENT_DATE, agreementDate)
            put(COLUMN_CONTRACT_NUMBER_REF, contractNumber)
            put(COLUMN_CONTRACT_DATE_REF, contractDate)
            put(COLUMN_CUSTOMER_FIO, customerFIO)
            put(COLUMN_EXECUTOR_FIO, executorFIO)
            put(COLUMN_AGREEMENT_ITEMS, agreementItems.joinToString("|")) // Простой способ сериализации списка
            put(COLUMN_CONTRACTOR_ID_FK_AGREEMENT, contractorId)
        }

        return db.insert(TABLE_ADDITIONAL_AGREEMENTS, null, values)
    }

    fun getAdditionalAgreementsForContractor(contractorId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_ADDITIONAL_AGREEMENTS 
        WHERE $COLUMN_CONTRACTOR_ID_FK_AGREEMENT = ?
        ORDER BY $COLUMN_AGREEMENT_DATE DESC
        """.trimIndent(),
            arrayOf(contractorId.toString())
        )
    }

    fun getAdditionalAgreementById(agreementId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_ADDITIONAL_AGREEMENTS 
        WHERE $COLUMN_AGREEMENT_ID = ?
        """.trimIndent(),
            arrayOf(agreementId.toString())
        )
    }

    fun deleteAdditionalAgreement(agreementId: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_ADDITIONAL_AGREEMENTS,
            "$COLUMN_AGREEMENT_ID = ?",
            arrayOf(agreementId.toString())
        )
    }

    fun saveAct(
        actType: String,
        actNumber: String,
        actDate: String,
        contractNumber: String,
        contractDate: String,
        serviceName: String,
        quantity: String?,
        cost: String,
        customerFIO: String,
        executorFIO: String,
        vatStatus: Int?,
        contractorId: Long
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACT_TYPE, actType)
            put(COLUMN_ACT_NUMBER, actNumber)
            put(COLUMN_ACT_DATE, actDate)
            put(COLUMN_CONTRACT_NUMBER_REF_ACT, contractNumber)
            put(COLUMN_CONTRACT_DATE_REF_ACT, contractDate)
            put(COLUMN_SERVICE_NAME_ACT, serviceName)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_COST, cost)
            put(COLUMN_CUSTOMER_FIO_ACT, customerFIO)
            put(COLUMN_EXECUTOR_FIO_ACT, executorFIO)
            put(COLUMN_VAT_STATUS, vatStatus)
            put(COLUMN_CONTRACTOR_ID_FK_ACT, contractorId)
        }

        return db.insert(TABLE_ACTS, null, values)
    }

    fun getActsForContractor(contractorId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_ACTS 
        WHERE $COLUMN_CONTRACTOR_ID_FK_ACT = ?
        ORDER BY $COLUMN_ACT_DATE DESC
        """.trimIndent(),
            arrayOf(contractorId.toString())
        )
    }

    fun getActsForContract(contractNumber: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_ACTS 
        WHERE $COLUMN_CONTRACT_NUMBER_REF_ACT = ?
        ORDER BY $COLUMN_ACT_DATE DESC
        """.trimIndent(),
            arrayOf(contractNumber)
        )
    }

    fun getActById(actId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
        SELECT * FROM $TABLE_ACTS 
        WHERE $COLUMN_ACT_ID = ?
        """.trimIndent(),
            arrayOf(actId.toString())
        )
    }

    fun deleteAct(actId: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_ACTS,
            "$COLUMN_ACT_ID = ?",
            arrayOf(actId.toString())
        )
    }

    fun checkUserExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT COUNT(*) 
        FROM $TABLE_USERS 
        WHERE $COLUMN_USERNAME = ? AND $COLUMN_IS_ACTIVE = 1
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROLES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTRACTOR_DETAILS")
        onCreate(db)
    }
}