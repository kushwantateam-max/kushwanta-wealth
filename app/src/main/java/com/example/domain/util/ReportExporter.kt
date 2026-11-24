package com.example.domain.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DebtEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupPayload(
    val accounts: List<AccountEntity>,
    val categories: List<CategoryEntity>,
    val transactions: List<TransactionEntity>,
    val budgets: List<BudgetEntity>,
    val goals: List<SavingsGoalEntity>,
    val bills: List<BillReminderEntity>,
    val debts: List<DebtEntity>,
    val presets: List<QuickPresetEntity>
)

object ReportExporter {

    private fun getExportsDir(context: Context): File {
        val dir = File(context.cacheDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * Generates a professional multi-section A4 PDF Statement locally using Android PdfDocument (zero cost, offline).
     */
    fun generateAndSharePdfReport(
        context: Context,
        bookTitle: String,
        currencySymbol: String,
        accounts: List<AccountEntity>,
        transactions: List<TransactionWithDetails>,
        formatAmount: (Double) -> String
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas

        val headerBgPaint = Paint().apply { color = Color.rgb(30, 64, 175) }
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.rgb(219, 234, 254)
            textSize = 11f
            isAntiAlias = true
        }
        val sectionPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 10.5f
            isAntiAlias = true
        }
        val boldBodyPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val dividerPaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }

        // Top Banner
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 88f, headerBgPaint)
        canvas.drawText("KOSHVANTA FINANCIAL STATEMENT", 36f, 40f, titlePaint)
        val genDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
        canvas.drawText("Book: $bookTitle Ledger  |  Currency: $currencySymbol  |  Generated: $genDate", 36f, 64f, subtitlePaint)

        var y = 118f
        val netWorth = accounts.sumOf { it.balance }
        val totalIncome = transactions.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
        val totalExpense = transactions.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }

        // Executive Summary
        canvas.drawText("Executive Financial Summary", 36f, y, sectionPaint)
        y += 20f
        canvas.drawText("Total Net Worth: ${formatAmount(netWorth)}", 36f, y, boldBodyPaint)
        canvas.drawText("Total Income: ${formatAmount(totalIncome)}", 220f, y, bodyPaint)
        canvas.drawText("Total Expense: ${formatAmount(totalExpense)}", 400f, y, bodyPaint)
        y += 14f
        canvas.drawLine(36f, y, pageWidth - 36f, y, dividerPaint)
        y += 24f

        // Accounts Section
        canvas.drawText("Connected Accounts (${accounts.size})", 36f, y, sectionPaint)
        y += 18f
        accounts.forEach { acc ->
            canvas.drawText("• ${acc.name} (${acc.type.name})", 40f, y, bodyPaint)
            canvas.drawText(formatAmount(acc.balance), 420f, y, boldBodyPaint)
            y += 16f
        }
        y += 8f
        canvas.drawLine(36f, y, pageWidth - 36f, y, dividerPaint)
        y += 24f

        // Transactions Table
        canvas.drawText("Itemized Transaction Ledger (${transactions.size} Records)", 36f, y, sectionPaint)
        y += 18f
        canvas.drawText("Date", 36f, y, boldBodyPaint)
        canvas.drawText("Category / Note", 130f, y, boldBodyPaint)
        canvas.drawText("Type", 345f, y, boldBodyPaint)
        canvas.drawText("Amount", 450f, y, boldBodyPaint)
        y += 8f
        canvas.drawLine(36f, y, pageWidth - 36f, y, dividerPaint)
        y += 16f

        val df = SimpleDateFormat("dd MMM yy", Locale.US)
        transactions.forEach { item ->
            if (y > pageHeight - 60f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 48f
            }
            val tx = item.transaction
            val catName = item.category?.name ?: "General"
            val label = "$catName - ${tx.note}".take(34)
            val sign = if (tx.type == TransactionType.INCOME) "+" else "-"

            canvas.drawText(df.format(Date(tx.timestamp)), 36f, y, bodyPaint)
            canvas.drawText(label, 130f, y, bodyPaint)
            canvas.drawText(tx.type.name, 345f, y, bodyPaint)
            canvas.drawText("$sign${formatAmount(tx.amount)}", 450f, y, boldBodyPaint)
            y += 18f
        }

        pdfDocument.finishPage(page)

        val fileName = "Koshvanta_Statement_${System.currentTimeMillis()}.pdf"
        val file = File(getExportsDir(context), fileName)
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        shareFile(context, file, "application/pdf", "Share Koshvanta PDF Statement")
        return file
    }

    /**
     * Generates a clean RFC-4180 CSV spreadsheet of transactions and launches Share sheet.
     */
    fun generateAndShareCsvReport(
        context: Context,
        bookTitle: String,
        currencyCode: String,
        transactions: List<TransactionWithDetails>,
        convertToDisplay: (Double) -> Double
    ): File {
        val sb = StringBuilder()
        sb.append("ID,Date,Book,Type,Category,Account,Amount ($currencyCode),Note,IsRefund\n")
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        transactions.forEach { item ->
            val tx = item.transaction
            val date = df.format(Date(tx.timestamp))
            val cat = (item.category?.name ?: "General").replace(",", " ")
            val acc = (item.account?.name ?: "Account").replace(",", " ")
            val note = tx.note.replace("\"", "\"\"").replace(",", " ")
            val amountStr = String.format(Locale.US, "%.2f", convertToDisplay(tx.amount))
            sb.append("${tx.id},$date,$bookTitle,${tx.type.name},$cat,$acc,$amountStr,\"$note\",${tx.isRefund}\n")
        }

        val fileName = "Koshvanta_Ledger_${System.currentTimeMillis()}.csv"
        val file = File(getExportsDir(context), fileName)
        file.writeText(sb.toString(), Charsets.UTF_8)

        shareFile(context, file, "text/csv", "Share Koshvanta CSV Ledger")
        return file
    }

    /**
     * Generates a checksum-validated JSON Backup file and launches Share sheet.
     */
    fun generateJsonBackupString(payload: BackupPayload): String {
        val root = JSONObject()
        root.put("app", "Koshvanta")
        root.put("schemaVersion", 3)
        root.put("exportedAt", System.currentTimeMillis())

        val dataObj = JSONObject()

        val accArr = JSONArray()
        payload.accounts.forEach { a ->
            accArr.put(JSONObject().apply {
                put("id", a.id)
                put("bookId", a.bookId)
                put("name", a.name)
                put("type", a.type.name)
                put("balance", a.balance)
                put("last4", a.accountNumberLast4)
                put("color", a.cardColorHex)
                put("isDefault", a.isDefault)
                put("creditLimit", a.creditLimit)
            })
        }
        dataObj.put("accounts", accArr)

        val catArr = JSONArray()
        payload.categories.forEach { c ->
            catArr.put(JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("icon", c.iconName)
                put("color", c.colorHex)
                put("isExpense", c.isExpense)
            })
        }
        dataObj.put("categories", catArr)

        val txArr = JSONArray()
        payload.transactions.forEach { t ->
            txArr.put(JSONObject().apply {
                put("id", t.id)
                put("bookId", t.bookId)
                put("accountId", t.accountId)
                put("toAccountId", t.toAccountId ?: -1L)
                put("categoryId", t.categoryId)
                put("amount", t.amount)
                put("type", t.type.name)
                put("note", t.note)
                put("timestamp", t.timestamp)
                put("isRecurring", t.isRecurring)
                put("recurrenceInterval", t.recurrenceInterval)
                put("isRefund", t.isRefund)
                put("profileName", t.profileName)
            })
        }
        dataObj.put("transactions", txArr)

        val budgetArr = JSONArray()
        payload.budgets.forEach { b ->
            budgetArr.put(JSONObject().apply {
                put("id", b.id)
                put("bookId", b.bookId)
                put("categoryId", b.categoryId)
                put("monthlyLimit", b.monthlyLimit)
                put("month", b.periodMonth)
                put("year", b.periodYear)
            })
        }
        dataObj.put("budgets", budgetArr)

        val goalArr = JSONArray()
        payload.goals.forEach { g ->
            goalArr.put(JSONObject().apply {
                put("id", g.id)
                put("bookId", g.bookId)
                put("name", g.name)
                put("targetAmount", g.targetAmount)
                put("currentAmount", g.currentAmount)
                put("targetDate", g.targetDate)
                put("colorHex", g.colorHex)
            })
        }
        dataObj.put("goals", goalArr)

        val billArr = JSONArray()
        payload.bills.forEach { b ->
            billArr.put(JSONObject().apply {
                put("id", b.id)
                put("bookId", b.bookId)
                put("title", b.title)
                put("amount", b.amount)
                put("dueDate", b.dueDate)
                put("isPaid", b.isPaid)
            })
        }
        dataObj.put("bills", billArr)

        val debtArr = JSONArray()
        payload.debts.forEach { d ->
            debtArr.put(JSONObject().apply {
                put("id", d.id)
                put("bookId", d.bookId)
                put("personName", d.personName)
                put("amount", d.amount)
                put("isLent", d.isLent)
                put("dueDate", d.dueDate)
                put("isSettled", d.isSettled)
                put("note", d.note)
            })
        }
        dataObj.put("debts", debtArr)

        val presetArr = JSONArray()
        payload.presets.forEach { p ->
            presetArr.put(JSONObject().apply {
                put("id", p.id)
                put("bookId", p.bookId)
                put("title", p.title)
                put("emoji", p.emoji)
                put("amountUsd", p.amountUsd)
                put("categoryName", p.categoryName)
                put("isExpense", p.isExpense)
            })
        }
        dataObj.put("presets", presetArr)

        root.put("data", dataObj)
        root.put("checksum", sha256(dataObj.toString()))
        return root.toString(2)
    }

    fun exportAndShareJsonBackup(context: Context, payload: BackupPayload): File {
        val jsonText = generateJsonBackupString(payload)
        val fileName = "Koshvanta_Backup_${System.currentTimeMillis()}.json"
        val file = File(getExportsDir(context), fileName)
        file.writeText(jsonText, Charsets.UTF_8)
        shareFile(context, file, "application/json", "Share Koshvanta Backup (.json)")
        return file
    }

    /**
     * Validates and parses a user-selected JSON backup string (Features 91, 118).
     */
    fun validateAndParseBackupJson(jsonString: String): Result<BackupPayload> {
        return try {
            val root = JSONObject(jsonString)
            if (root.optString("app") != "Koshvanta") {
                return Result.failure(IllegalArgumentException("Invalid backup: Not a Koshvanta archive."))
            }
            val dataObj = root.optJSONObject("data")
                ?: return Result.failure(IllegalArgumentException("Corrupted backup: Missing data payload."))
            val expectedChecksum = root.optString("checksum", "")
            val actualChecksum = sha256(dataObj.toString())
            if (expectedChecksum.isNotEmpty() && expectedChecksum != actualChecksum) {
                return Result.failure(IllegalArgumentException("Integrity check failed: File checksum mismatch!"))
            }

            val accounts = mutableListOf<AccountEntity>()
            val accArr = dataObj.optJSONArray("accounts") ?: JSONArray()
            for (i in 0 until accArr.length()) {
                val o = accArr.getJSONObject(i)
                accounts.add(
                    AccountEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        name = o.getString("name"),
                        type = runCatching { AccountType.valueOf(o.getString("type")) }.getOrDefault(AccountType.BANK),
                        balance = o.optDouble("balance", 0.0),
                        accountNumberLast4 = o.optString("last4", ""),
                        cardColorHex = o.optString("color", "#1D4ED8"),
                        isDefault = o.optBoolean("isDefault", false),
                        creditLimit = o.optDouble("creditLimit", 0.0)
                    )
                )
            }

            val categories = mutableListOf<CategoryEntity>()
            val catArr = dataObj.optJSONArray("categories") ?: JSONArray()
            for (i in 0 until catArr.length()) {
                val o = catArr.getJSONObject(i)
                categories.add(
                    CategoryEntity(
                        id = o.optLong("id", 0),
                        name = o.getString("name"),
                        iconName = o.optString("icon", "payments"),
                        colorHex = o.optString("color", "#2563EB"),
                        isExpense = o.optBoolean("isExpense", true)
                    )
                )
            }

            val transactions = mutableListOf<TransactionEntity>()
            val txArr = dataObj.optJSONArray("transactions") ?: JSONArray()
            for (i in 0 until txArr.length()) {
                val o = txArr.getJSONObject(i)
                val toAcc = o.optLong("toAccountId", -1L)
                transactions.add(
                    TransactionEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        accountId = o.getLong("accountId"),
                        toAccountId = if (toAcc > 0) toAcc else null,
                        categoryId = o.getLong("categoryId"),
                        amount = o.getDouble("amount"),
                        type = runCatching { TransactionType.valueOf(o.getString("type")) }.getOrDefault(TransactionType.EXPENSE),
                        note = o.optString("note", ""),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                        isRecurring = o.optBoolean("isRecurring", false),
                        recurrenceInterval = o.optString("recurrenceInterval", ""),
                        isRefund = o.optBoolean("isRefund", false),
                        profileName = o.optString("profileName", "Primary")
                    )
                )
            }

            val budgets = mutableListOf<BudgetEntity>()
            val bArr = dataObj.optJSONArray("budgets") ?: JSONArray()
            for (i in 0 until bArr.length()) {
                val o = bArr.getJSONObject(i)
                budgets.add(
                    BudgetEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        categoryId = o.getLong("categoryId"),
                        monthlyLimit = o.getDouble("monthlyLimit"),
                        periodMonth = o.getInt("month"),
                        periodYear = o.getInt("year")
                    )
                )
            }

            val goals = mutableListOf<SavingsGoalEntity>()
            val gArr = dataObj.optJSONArray("goals") ?: JSONArray()
            for (i in 0 until gArr.length()) {
                val o = gArr.getJSONObject(i)
                goals.add(
                    SavingsGoalEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        name = o.getString("name"),
                        targetAmount = o.getDouble("targetAmount"),
                        currentAmount = o.optDouble("currentAmount", 0.0),
                        targetDate = o.optLong("targetDate", System.currentTimeMillis()),
                        colorHex = o.optString("colorHex", "#10B981")
                    )
                )
            }

            val bills = mutableListOf<BillReminderEntity>()
            val billArr = dataObj.optJSONArray("bills") ?: JSONArray()
            for (i in 0 until billArr.length()) {
                val o = billArr.getJSONObject(i)
                bills.add(
                    BillReminderEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        title = o.getString("title"),
                        amount = o.getDouble("amount"),
                        dueDate = o.optLong("dueDate", System.currentTimeMillis()),
                        isPaid = o.optBoolean("isPaid", false)
                    )
                )
            }

            val debts = mutableListOf<DebtEntity>()
            val dArr = dataObj.optJSONArray("debts") ?: JSONArray()
            for (i in 0 until dArr.length()) {
                val o = dArr.getJSONObject(i)
                debts.add(
                    DebtEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        personName = o.getString("personName"),
                        amount = o.getDouble("amount"),
                        isLent = o.optBoolean("isLent", true),
                        dueDate = o.optLong("dueDate", System.currentTimeMillis()),
                        isSettled = o.optBoolean("isSettled", false),
                        note = o.optString("note", "")
                    )
                )
            }

            val presets = mutableListOf<QuickPresetEntity>()
            val pArr = dataObj.optJSONArray("presets") ?: JSONArray()
            for (i in 0 until pArr.length()) {
                val o = pArr.getJSONObject(i)
                presets.add(
                    QuickPresetEntity(
                        id = o.optLong("id", 0),
                        bookId = o.optString("bookId", "personal"),
                        title = o.getString("title"),
                        emoji = o.optString("emoji", "💳"),
                        amountUsd = o.getDouble("amountUsd"),
                        categoryName = o.optString("categoryName", "Food & Drinks"),
                        isExpense = o.optBoolean("isExpense", true)
                    )
                )
            }

            Result.success(BackupPayload(accounts, categories, transactions, budgets, goals, bills, debts, presets))
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to parse backup file: ${e.message}"))
        }
    }

    fun readTextFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
