package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateRedNeon
import com.example.ui.viewmodel.KoshvantaViewModel
import java.util.Locale

data class SafeVoiceParseResult(
    val rawText: String,
    val amountDisplay: Double,
    val transactionType: TransactionType,
    val paymentModeLabel: String,
    val preferredAccountType: AccountType,
    val hasExplicitWalletMode: Boolean,
    val categoryHint: String,
    val suggestedIconName: String,
    val suggestedColorHex: String
)

object SafeVoiceTextParser {

    private val NEON_COLORS = listOf(
        "#00FFCC",
        "#00E5FF",
        "#A855F7",
        "#FF3366",
        "#10B981",
        "#F59E0B"
    )

    private val INCOME_KEYWORDS = listOf(
        // English
        "received", "receive", "credited", "credit", "earned", "earn",
        "income", "salary", "bonus", "refund", "cashback", "got paid",
        "freelance", "dividend", "profit",
        // Hindi / Hinglish
        "mila", "mili", "mile", "aaya", "aaye", "jama", "tanakhwah", "vetan",
        "प्राप्त", "मिला", "मिले", "आय", "सैलरी", "कमाई", "कैशबैक", "बोनस", "वेतन",
        // Spanish / Portuguese / French / German / Arabic
        "recibi", "recibí", "ingreso", "salario", "sueldo", "reembolso",
        "recebi", "salário", "reçu", "salaire", "gehalt", "einnahme",
        "راتب", "دخل", "استلمت", "أرباح", "مكافأة"
    )

    private val EXPENSE_KEYWORDS = listOf(
        // English
        "spent", "spend", "paid", "pay", "bought", "buy", "purchase",
        "debited", "expense",
        // Hindi / Hinglish
        "kharch", "diya", "diye", "bheja",
        "खर्च", "दिया", "दिए", "भुगतान", "खरीदा",
        // Spanish / Portuguese / French / German / Arabic
        "gaste", "gasté", "pague", "pagué", "compre", "compré", "gasto",
        "dépensé", "payé", "ausgegeben", "bezahlt",
        "صرفت", "دفعت", "شراء", "مصروف"
    )

    private val UNIT_NUMBER_WORDS = mapOf(
        // English
        "zero" to 0.0, "one" to 1.0, "two" to 2.0, "three" to 3.0, "four" to 4.0,
        "five" to 5.0, "six" to 6.0, "seven" to 7.0, "eight" to 8.0, "nine" to 9.0,
        "ten" to 10.0, "eleven" to 11.0, "twelve" to 12.0, "thirteen" to 13.0,
        "fourteen" to 14.0, "fifteen" to 15.0, "sixteen" to 16.0, "seventeen" to 17.0,
        "eighteen" to 18.0, "nineteen" to 19.0,
        "twenty" to 20.0, "thirty" to 30.0, "forty" to 40.0, "fifty" to 50.0,
        "sixty" to 60.0, "seventy" to 70.0, "eighty" to 80.0, "ninety" to 90.0,
        // Hindi / Hinglish (Latin & Devanagari)
        "ek" to 1.0, "do" to 2.0, "teen" to 3.0, "char" to 4.0, "chaar" to 4.0,
        "panch" to 5.0, "paanch" to 5.0, "chhe" to 6.0, "saat" to 7.0, "aath" to 8.0,
        "nau" to 9.0, "das" to 10.0, "gyarah" to 11.0, "barah" to 12.0, "pandrah" to 15.0,
        "bees" to 20.0, "pachis" to 25.0, "pachees" to 25.0, "tees" to 30.0,
        "chalis" to 40.0, "pachas" to 50.0, "saath" to 60.0, "sattar" to 70.0,
        "assi" to 80.0, "nabbe" to 90.0,
        "एक" to 1.0, "दो" to 2.0, "तीन" to 3.0, "चार" to 4.0,
        "पांच" to 5.0, "पाँच" to 5.0, "छह" to 6.0, "सात" to 7.0, "आठ" to 8.0, "नौ" to 9.0,
        "दस" to 10.0, "ग्यारह" to 11.0, "बारह" to 12.0, "पंद्रह" to 15.0,
        "बीस" to 20.0, "पच्चीस" to 25.0, "तीस" to 30.0, "चालीस" to 40.0,
        "पचास" to 50.0, "साठ" to 60.0, "सत्तर" to 70.0, "अस्सी" to 80.0, "नब्बे" to 90.0,
        // Spanish
        "uno" to 1.0, "dos" to 2.0, "tres" to 3.0, "cuatro" to 4.0, "cinco" to 5.0,
        "seis" to 6.0, "siete" to 7.0, "ocho" to 8.0, "nueve" to 9.0, "diez" to 10.0,
        "quince" to 15.0, "veinte" to 20.0, "treinta" to 30.0, "cuarenta" to 40.0,
        "cincuenta" to 50.0, "sesenta" to 60.0, "setenta" to 70.0, "ochenta" to 80.0,
        "noventa" to 90.0, "cien" to 100.0, "ciento" to 100.0,
        "doscientos" to 200.0, "trescientos" to 300.0, "cuatrocientos" to 400.0,
        "quinientos" to 500.0, "seiscientos" to 600.0, "setecientos" to 700.0,
        "ochocientos" to 800.0, "novecientos" to 900.0,
        // Arabic
        "واحد" to 1.0, "اثنين" to 2.0, "ثلاثة" to 3.0, "أربعة" to 4.0, "خمسة" to 5.0,
        "عشرة" to 10.0, "عشرين" to 20.0, "ثلاثين" to 30.0, "أربعين" to 40.0,
        "خمسين" to 50.0, "مئة" to 100.0, "مائة" to 100.0, "مئتين" to 200.0,
        "خمسمائة" to 500.0, "khamsa" to 5.0, "ashara" to 10.0, "ishreen" to 20.0,
        "khamseen" to 50.0, "mithain" to 200.0
    )

    private val SCALE_WORDS = mapOf(
        "hundred" to 100.0,
        "sau" to 100.0,
        "सौ" to 100.0,
        "thousand" to 1000.0,
        "hazar" to 1000.0,
        "hazaar" to 1000.0,
        "हजार" to 1000.0,
        "हज़ार" to 1000.0,
        "mil" to 1000.0,
        "ألف" to 1000.0,
        "الف" to 1000.0,
        "alf" to 1000.0,
        "lakh" to 100_000.0,
        "lac" to 100_000.0,
        "लाख" to 100_000.0,
        "million" to 1_000_000.0
    )

    private val STOP_WORDS = setOf(
        "i", "me", "my", "spent", "spend", "paid", "pay", "bought", "buy", "purchase",
        "received", "receive", "credited", "earned", "earn", "got", "debited", "added",
        "on", "for", "via", "by", "with", "from", "in", "to", "at", "of", "the", "a", "an",
        "and", "using", "through", "into", "is", "was", "expense", "income", "transaction",
        "rupees", "rupee", "rupaye", "rupaya", "rs", "inr", "dollar", "dollars", "usd",
        "euro", "euros", "eur", "pound", "pounds", "gbp", "yen", "jpy", "aed", "dirham",
        "sar", "riyal", "pesos", "peso", "real", "reais", "bucks", "taka",
        "रुपये", "रुपए", "रुपया", "पैसा", "पैसे",
        "se", "ke", "liye", "mein", "me", "ko", "ka", "ki", "par", "pe", "kharch", "diya", "diye",
        "mila", "mili", "mile", "aaya", "aaye", "bheja", "से", "के", "लिए", "में", "को", "का", "की", "पर",
        "खर्च", "दिया", "दिए", "भुगतान", "खरीदा", "मिला", "मिले", "प्राप्त", "यूपीआई",
        "gaste", "gasté", "pague", "pagué", "compre", "compré", "en", "con", "por", "para", "de",
        "صرفت", "دفعت", "في", "من", "على"
    ) + UNIT_NUMBER_WORDS.keys + SCALE_WORDS.keys

    private val MODE_STOP_WORDS = setOf(
        "upi", "gpay", "google", "phonepe", "phone", "pe", "paytm", "bhim", "amazonpay",
        "cash", "wallet", "pocket", "bank", "account", "card", "credit", "debit",
        "visa", "mastercard", "hdfc", "axis", "sbi", "icici", "kotak", "paypal", "apple",
        "कैश", "नकद", "बैंक", "कार्ड", "क्रेडिट", "यूपीआई", "पेटीएम", "फोनपे",
        "efectivo", "tarjeta", "banco", "نقد", "بطاقة", "بنك"
    )

    fun normalizeDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                in '०'..'९' -> sb.append(('0'.code + (ch.code - '०'.code)).toChar())
                in '٠'..'٩' -> sb.append(('0'.code + (ch.code - '٠'.code)).toChar())
                in '۰'..'۹' -> sb.append(('0'.code + (ch.code - '۰'.code)).toChar())
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun parse(rawInput: String): SafeVoiceParseResult {
        val normalized = normalizeDigits(rawInput).trim()
        val lower = normalized.lowercase(Locale.ROOT)

        val amount = extractAmount(normalized)
        val txType = detectTransactionType(lower)
        val (modeLabel, accType, explicitMode) = detectPaymentMode(lower)
        val (catHint, iconName, colorHex) = detectOrExtractCategory(normalized, lower, txType)

        return SafeVoiceParseResult(
            rawText = rawInput.trim(),
            amountDisplay = amount,
            transactionType = txType,
            paymentModeLabel = modeLabel,
            preferredAccountType = accType,
            hasExplicitWalletMode = explicitMode,
            categoryHint = catHint,
            suggestedIconName = iconName,
            suggestedColorHex = colorHex
        )
    }

    private fun extractAmount(normalized: String): Double {
        if (normalized.isBlank()) return 0.0

        // Support compact suffixes like "5k", "1.5k"
        val kMatch = Regex("(\\d+(?:\\.\\d{1,2})?)\\s*k(?:\\b|\$)", RegexOption.IGNORE_CASE).find(normalized)
        if (kMatch != null) {
            val base = kMatch.groupValues[1].toDoubleOrNull()
            if (base != null && base > 0.0) return base * 1000.0
        }

        // Support "lakh"/"lac" multiplier with digits e.g. "1.5 lakh"
        val lakhMatch = Regex("(\\d+(?:\\.\\d{1,2})?)\\s*(?:lakh|lac|लाख)", RegexOption.IGNORE_CASE).find(normalized)
        if (lakhMatch != null) {
            val base = lakhMatch.groupValues[1].toDoubleOrNull()
            if (base != null && base > 0.0) return base * 100_000.0
        }

        val compactNumbers = normalized.replace(Regex("(?<=\\d),(?=\\d)"), "")
        val numberRegex = Regex("(\\d+(?:\\.\\d{1,2})?)")
        val matches = numberRegex.findAll(compactNumbers).toList()
        if (matches.isNotEmpty()) {
            if (matches.size == 1) {
                return matches.first().value.toDoubleOrNull() ?: 0.0
            }
            val currencyMatch = Regex(
                "(?:rs\\.?|inr|usd|eur|gbp|aed|sar|jpy|₹|\\$|€|£|¥|₩|₽|฿|rupees?|rupaye|dollars?|euros?|bucks|pesos?)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:rs\\.?|inr|usd|eur|gbp|aed|sar|jpy|₹|\\$|€|£|¥|₩|₽|฿|rupees?|rupaye|dollars?|euros?|bucks|pesos?)",
                RegexOption.IGNORE_CASE
            ).find(compactNumbers)
            if (currencyMatch != null) {
                val rawNum = currencyMatch.groupValues.drop(1).firstOrNull { it.isNotBlank() }
                rawNum?.toDoubleOrNull()?.let { if (it > 0.0) return it }
            }
            return matches.mapNotNull { it.value.toDoubleOrNull() }.maxOrNull() ?: 0.0
        }

        return parseWrittenNumberWords(normalized.lowercase(Locale.ROOT))
    }

    private fun parseWrittenNumberWords(lower: String): Double {
        val tokens = lower
            .replace(Regex("[₹$€£¥₩₽฿,!?.:;\"'()\\[\\]-]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        var total = 0.0
        var current = 0.0
        var foundAny = false

        for (token in tokens) {
            val unitVal = UNIT_NUMBER_WORDS[token]
            val scaleVal = SCALE_WORDS[token]
            when {
                token == "dedh" || token == "डेढ़" -> {
                    current += 150.0
                    foundAny = true
                }
                token == "dhai" || token == "ढाई" -> {
                    current += 250.0
                    foundAny = true
                }
                unitVal != null -> {
                    current += unitVal
                    foundAny = true
                }
                scaleVal != null -> {
                    if (scaleVal == 100.0) {
                        current = if (current == 0.0) 100.0 else current * 100.0
                    } else {
                        val factor = if (current == 0.0) 1.0 else current
                        total += factor * scaleVal
                        current = 0.0
                    }
                    foundAny = true
                }
            }
        }
        return if (foundAny) total + current else 0.0
    }

    private fun detectTransactionType(lower: String): TransactionType {
        val hasIncome = INCOME_KEYWORDS.any { lower.contains(it) }
        val hasExpense = EXPENSE_KEYWORDS.any { lower.contains(it) }
        return if (hasIncome && !hasExpense) TransactionType.INCOME else TransactionType.EXPENSE
    }

    private fun detectPaymentMode(lower: String): Triple<String, AccountType, Boolean> {
        return when {
            lower.contains("gpay") || lower.contains("google pay") -> Triple("GPay UPI", AccountType.BANK, true)
            lower.contains("phonepe") || lower.contains("phone pe") || lower.contains("फोनपे") -> Triple("PhonePe UPI", AccountType.BANK, true)
            lower.contains("paytm") || lower.contains("पेटीएम") -> Triple("Paytm UPI", AccountType.BANK, true)
            lower.contains("bhim") -> Triple("BHIM UPI", AccountType.BANK, true)
            lower.contains("amazon pay") || lower.contains("amazonpay") -> Triple("Amazon Pay", AccountType.BANK, true)
            lower.contains("upi") || lower.contains("यूपीआई") -> Triple("UPI Wallet", AccountType.BANK, true)
            lower.contains("credit card") || lower.contains("visa") || lower.contains("mastercard") ||
                lower.contains("क्रेडिट कार्ड") || lower.contains("tarjeta") || lower.contains("بطاقة") || lower.contains("card") ->
                Triple("Credit Card", AccountType.CREDIT_CARD, true)
            lower.contains("cash") || lower.contains("pocket") || lower.contains("नकद") ||
                lower.contains("कैश") || lower.contains("efectivo") || lower.contains("نقد") ->
                Triple("Cash", AccountType.CASH, true)
            lower.contains("hdfc") -> Triple("HDFC Bank", AccountType.BANK, true)
            lower.contains("axis") -> Triple("Axis Bank", AccountType.BANK, true)
            lower.contains("sbi") -> Triple("SBI Bank", AccountType.BANK, true)
            lower.contains("icici") -> Triple("ICICI Bank", AccountType.BANK, true)
            lower.contains("kotak") -> Triple("Kotak Bank", AccountType.BANK, true)
            lower.contains("bank") || lower.contains("बैंक") || lower.contains("banco") || lower.contains("بنك") ->
                Triple("Bank Account", AccountType.BANK, true)
            else -> Triple("Primary Account", AccountType.BANK, false)
        }
    }

    private fun detectOrExtractCategory(
        normalized: String,
        lower: String,
        txType: TransactionType
    ): Triple<String, String, String> {
        if (txType == TransactionType.INCOME) {
            return when {
                lower.containsAny("salary", "paycheck", "vetan", "सैलरी", "वेतन", "salario", "sueldo", "salaire", "راتب") ->
                    Triple("Salary", "payments", "#10B981")
                lower.containsAny("freelance", "client", "project") ->
                    Triple("Freelancing Work", "laptop", "#10B981")
                lower.containsAny("bonus", "gift", "cashback", "reembolso", "مكافأة") ->
                    Triple("Bonus & Rewards", "card_giftcard", "#00FFCC")
                else -> {
                    val custom = extractCustomSectionName(normalized) ?: "Side Income"
                    Triple(custom, "payments", "#00FFCC")
                }
            }
        }

        when {
            lower.containsAny("coffee", "tea", "chai", "starbucks", "lunch", "dinner", "breakfast", "restaurant", "pizza", "burger", "biryani", "food", "snack", "cafe", "café", "comida", "चाय", "कॉफी", "खाना", "नाश्ता", "قهوة", "طعام") ->
                return Triple("Food & Drinks", "restaurant", "#06B6D4")
            lower.containsAny("grocery", "groceries", "vegetable", "vegetables", "milk", "fruit", "mart", "supermarket", "ration", "kirana", "सब्जी", "दूध", "किराना", "राशन", "supermercado", "بقالة") ->
                return Triple("Groceries", "local_grocery_store", "#F59E0B")
            lower.containsAny("fuel", "petrol", "diesel", "uber", "ola", "taxi", "cab", "metro", "bus", "train", "auto", "rickshaw", "parking", "transport", "gasolina", "पेट्रोल", "डीजल", "मेट्रो", "टैक्सी", "وقود", "بنزين") ->
                return Triple("Transport & Fuel", "directions_car", "#0EA5E9")
            lower.containsAny("rent", "electric", "electricity", "water", "wifi", "internet", "broadband", "bill", "utility", "recharge", "mobile", "alquiler", "factura", "किराया", "बिजली", "बिल", "रिचार्ज", "إيجار", "فاتورة") ->
                return Triple("Rent & Bills", "home", "#6366F1")
            lower.containsAny("shopping", "amazon", "flipkart", "clothes", "shoes", "dress", "mall", "compras", "ropa", "शॉपिंग", "कपड़े", "تسوق") ->
                return Triple("Shopping", "shopping_bag", "#EC4899")
            lower.containsAny("movie", "cinema", "netflix", "spotify", "youtube", "concert", "game", "gaming", "फिल्म", "मूवी", "cine") ->
                return Triple("Entertainment", "movie", "#8B5CF6")
            lower.containsAny("sip", "mutual", "stock", "share", "invest", "investment") ->
                return Triple("Mutual Funds & Stocks", "trending_up", "#3B82F6")
        }

        val customWord = extractCustomSectionName(normalized)
        return when {
            lower.containsAny("gym", "fitness", "workout", "yoga", "protein", "जिम", "gimnasio") ->
                Triple(customWord ?: "Gym & Fitness", "fitness_center", "#00FFCC")
            lower.containsAny("medical", "medicine", "pharmacy", "doctor", "hospital", "clinic", "health", "tablet", "farmacia", "दवा", "डॉक्टर", "صيدلية") ->
                Triple(customWord ?: "Medical & Health", "medical_services", "#FF3366")
            lower.containsAny("pet", "dog", "cat", "vet", "mascota") ->
                Triple(customWord ?: "Pet Care", "pets", "#A855F7")
            lower.containsAny("flight", "hotel", "travel", "trip", "vacation", "ticket", "viaje", "यात्रा", "سفر") ->
                Triple(customWord ?: "Travel & Trips", "flight", "#00E5FF")
            lower.containsAny("book", "books", "school", "college", "course", "tuition", "education", "study", "किताब", "स्कूल") ->
                Triple(customWord ?: "Education", "school", "#3B82F6")
            lower.containsAny("gift", "birthday", "party", "wedding", "regalo", "गिफ्ट", "तोहफा", "هدية") ->
                Triple(customWord ?: "Gifts & Events", "card_giftcard", "#EC4899")
            lower.containsAny("repair", "service", "plumber", "mechanic", "bike", "car wash") ->
                Triple(customWord ?: "Repairs & Service", "build", "#F59E0B")
            else -> {
                val sectionName = customWord ?: "Voice Expense"
                val color = NEON_COLORS[kotlin.math.abs(sectionName.hashCode()) % NEON_COLORS.size]
                Triple(sectionName, "auto_awesome", color)
            }
        }
    }

    private fun extractCustomSectionName(normalized: String): String? {
        val tokens = normalized
            .replace(Regex("[₹$€£¥₩₽฿,!?.:;\"'()\\[\\]]"), " ")
            .replace(Regex("\\d+(?:\\.\\d+)?\\s*k?", RegexOption.IGNORE_CASE), " ")
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { token ->
                val low = token.lowercase(Locale.ROOT)
                token.length >= 2 &&
                    !STOP_WORDS.contains(low) &&
                    !MODE_STOP_WORDS.contains(low)
            }

        if (tokens.isEmpty()) return null
        return tokens.take(2).joinToString(" ") { word ->
            word.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
        }
    }

    fun resolveMatchingAccount(
        parseResult: SafeVoiceParseResult,
        accounts: List<AccountEntity>
    ): AccountEntity? {
        if (accounts.isEmpty()) return null
        if (!parseResult.hasExplicitWalletMode) {
            return accounts.firstOrNull { it.isDefault } ?: accounts.first()
        }
        val mode = parseResult.paymentModeLabel.lowercase(Locale.ROOT)
        val firstToken = mode.split(" ").firstOrNull() ?: mode
        return accounts.firstOrNull { acc ->
            val accLow = acc.name.lowercase(Locale.ROOT)
            accLow == mode ||
                (firstToken.length >= 3 && accLow.contains(firstToken)) ||
                (parseResult.preferredAccountType == AccountType.CASH && acc.type == AccountType.CASH) ||
                (parseResult.preferredAccountType == AccountType.CREDIT_CARD && acc.type == AccountType.CREDIT_CARD)
        }
    }

    fun resolveMatchingCategory(
        parseResult: SafeVoiceParseResult,
        categories: List<CategoryEntity>
    ): CategoryEntity? {
        val isExp = parseResult.transactionType == TransactionType.EXPENSE
        val hintLow = parseResult.categoryHint.lowercase(Locale.ROOT)
        val sameType = categories.filter { it.isExpense == isExp }
        return sameType.firstOrNull { it.name.equals(parseResult.categoryHint, ignoreCase = true) }
            ?: sameType.firstOrNull { cat ->
                val catLow = cat.name.lowercase(Locale.ROOT)
                catLow.contains(hintLow) || hintLow.contains(catLow)
            }
            ?: categories.firstOrNull { it.name.equals(parseResult.categoryHint, ignoreCase = true) }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}

class SafeSpeechWrapper(
    context: Context,
    private val onReady: () -> Unit,
    private val onRmsChanged: (Float) -> Unit,
    private val onPartialResult: (String) -> Unit,
    private val onFinalResult: (String) -> Unit,
    private val onUnavailableOrFallback: (String) -> Unit
) {
    private val appContext: Context = try {
        context.applicationContext ?: context
    } catch (_: Throwable) {
        context
    }

    private val mainHandler: Handler? = try {
        Handler(Looper.getMainLooper())
    } catch (_: Throwable) {
        null
    }

    private var recognizer: SpeechRecognizer? = null
    private var sessionToken: Long = 0L

    private fun dispatchMain(block: () -> Unit) {
        try {
            val isMain = try {
                Looper.myLooper() == Looper.getMainLooper()
            } catch (_: Throwable) {
                true
            }
            if (isMain || mainHandler == null) {
                block()
            } else {
                mainHandler.post {
                    try {
                        block()
                    } catch (_: Throwable) {
                    }
                }
            }
        } catch (_: Throwable) {
        }
    }

    fun hasRecordAudioPermission(): Boolean {
        return try {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) {
            false
        }
    }

    fun isAvailable(): Boolean {
        return try {
            SpeechRecognizer.isRecognitionAvailable(appContext)
        } catch (_: Throwable) {
            false
        }
    }

    fun startListening() {
        // Always destroy any stale recognizer instance cleanly before creating a fresh one
        stopAndDestroy()
        val activeToken = ++sessionToken

        if (!hasRecordAudioPermission()) {
            dispatchMain {
                onUnavailableOrFallback("Mic permission needed. Use the Neon Command Box or 1-tap chips below.")
            }
            return
        }

        if (!isAvailable()) {
            dispatchMain {
                onUnavailableOrFallback("Speech hardware unavailable. Use the Neon Command Box or 1-tap chips below.")
            }
            return
        }

        try {
            val freshRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext)
            recognizer = freshRecognizer
            freshRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    if (activeToken != sessionToken) return
                    dispatchMain { onReady() }
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {
                    if (activeToken != sessionToken) return
                    dispatchMain { onRmsChanged(rmsdB) }
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    if (activeToken != sessionToken) return
                    stopAndDestroy()
                    dispatchMain {
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                                "Mic permission denied. Type or tap a Neon Command chip below."
                            SpeechRecognizer.ERROR_NO_MATCH ->
                                "No voice match. Speak again or tap a quick Neon chip below."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                                "Listening timed out. Tap Mic to retry or type a Neon Command."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                                "Speech service busy after restart. Use the Neon Command Box below."
                            else ->
                                "Speech engine unavailable right now. Use the Neon Command Box below."
                        }
                        onUnavailableOrFallback(msg)
                    }
                }

                override fun onResults(results: Bundle?) {
                    if (activeToken != sessionToken) return
                    try {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val best = matches?.firstOrNull()?.trim().orEmpty()
                        stopAndDestroy()
                        dispatchMain {
                            if (best.isNotEmpty()) {
                                onFinalResult(best)
                            } else {
                                onUnavailableOrFallback("No speech captured. Type or tap a 1-tap chip below.")
                            }
                        }
                    } catch (_: Throwable) {
                        stopAndDestroy()
                        dispatchMain {
                            onUnavailableOrFallback("Use the Neon Command Box or 1-tap chips below.")
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    if (activeToken != sessionToken) return
                    try {
                        val stable = partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            ?.trim()
                            .orEmpty()
                        val unstable = partialResults
                            ?.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
                            ?.firstOrNull()
                            ?.trim()
                            .orEmpty()
                        val streamingText = listOf(stable, unstable)
                            .filter { it.isNotEmpty() }
                            .joinToString(" ")
                            .trim()
                        if (streamingText.isNotEmpty()) {
                            dispatchMain { onPartialResult(streamingText) }
                        }
                    } catch (_: Throwable) {
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val locale = try {
                Locale.getDefault()
            } catch (_: Throwable) {
                Locale.US
            }
            val languageTag = try {
                locale.toLanguageTag().ifBlank { "en-US" }
            } catch (_: Throwable) {
                "en-US"
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
            }
            freshRecognizer.startListening(intent)
        } catch (_: Throwable) {
            stopAndDestroy()
            dispatchMain {
                onUnavailableOrFallback("Speech service unavailable after restart. Use Neon Command Box below.")
            }
        }
    }

    fun stopListening() {
        try {
            recognizer?.stopListening()
        } catch (_: Throwable) {
        }
    }

    fun stopAndDestroy() {
        sessionToken++
        val old = recognizer
        recognizer = null
        if (old != null) {
            try {
                old.cancel()
            } catch (_: Throwable) {
            }
            try {
                old.destroy()
            } catch (_: Throwable) {
            }
        }
    }
}

@Composable
fun RollingCounterText(
    targetValue: Double,
    formatText: (Double) -> String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    maxLines: Int = 1
) {
    val animatable = remember { Animatable(targetValue.toFloat()) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    Text(
        text = formatText(animatable.value.toDouble()),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun GlowingNeonMicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micNeonGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.52f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micGlowAlpha"
    )

    Box(
        modifier = modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonMint.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )
        FloatingActionButton(
            onClick = onClick,
            containerColor = DarkCanvasBg,
            contentColor = NeonMint,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(52.dp)
                .border(1.5.dp, NeonMint, CircleShape)
                .testTag("voice_mic_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Global Voice Transaction Entry",
                tint = NeonMint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun VoiceTransactionBottomSheet(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    hasAudioPermission: Boolean,
    onRequestAudioPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var spokenText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var fallbackActive by remember { mutableStateOf(!hasAudioPermission) }
    var statusMessage by remember {
        mutableStateOf(
            if (hasAudioPermission) "Tap mic to speak or type in Neon Command Box"
            else "Mic permission optional — use Neon Command Box or 1-tap chips"
        )
    }
    var rmsDb by remember { mutableFloatStateOf(0f) }
    var typeOverride by remember { mutableStateOf<TransactionType?>(null) }

    val appContext = context.applicationContext ?: context
    val speechWrapper = remember(appContext) {
        SafeSpeechWrapper(
            context = appContext,
            onReady = {
                isListening = true
                fallbackActive = false
                statusMessage = "Listening (${Locale.getDefault().displayLanguage})... Live transcription active"
            },
            onRmsChanged = { rms ->
                rmsDb = rms.coerceIn(0f, 12f)
            },
            onPartialResult = { partial ->
                spokenText = partial
                statusMessage = "Streaming voice..."
            },
            onFinalResult = { finalText ->
                spokenText = finalText
                isListening = false
                rmsDb = 0f
                statusMessage = "Captured! Ready to save."
            },
            onUnavailableOrFallback = { fallbackMsg ->
                isListening = false
                fallbackActive = true
                rmsDb = 0f
                statusMessage = fallbackMsg
            }
        )
    }

    val safeStartSpeech: () -> Unit = {
        try {
            val livePerm = speechWrapper.hasRecordAudioPermission()
            if (!livePerm) {
                isListening = false
                fallbackActive = true
                statusMessage = "Mic permission required — Neon Command Box ready below"
                try {
                    onRequestAudioPermission()
                } catch (_: Throwable) {
                    fallbackActive = true
                }
            } else if (!speechWrapper.isAvailable()) {
                isListening = false
                fallbackActive = true
                statusMessage = "Speech hardware unavailable — Neon Command Mode active"
            } else {
                speechWrapper.startListening()
            }
        } catch (_: Throwable) {
            speechWrapper.stopAndDestroy()
            isListening = false
            fallbackActive = true
            statusMessage = "Speech engine busy — Neon Command Box active"
        }
    }

    LaunchedEffect(hasAudioPermission) {
        try {
            if (hasAudioPermission && speechWrapper.hasRecordAudioPermission() && speechWrapper.isAvailable()) {
                speechWrapper.startListening()
            } else {
                fallbackActive = true
                statusMessage = if (!speechWrapper.isAvailable()) {
                    "Speech hardware unavailable — Neon Command Mode active"
                } else {
                    "Mic permission optional — Neon Command Mode active"
                }
            }
        } catch (_: Throwable) {
            speechWrapper.stopAndDestroy()
            isListening = false
            fallbackActive = true
            statusMessage = "Speech engine busy — Neon Command Box active"
        }
    }

    DisposableEffect(speechWrapper) {
        onDispose {
            speechWrapper.stopAndDestroy()
        }
    }

    val parsed = remember(spokenText, typeOverride) {
        val base = SafeVoiceTextParser.parse(spokenText)
        if (typeOverride != null) base.copy(transactionType = typeOverride!!) else base
    }
    val matchedAccount = remember(parsed, accounts) {
        SafeVoiceTextParser.resolveMatchingAccount(parsed, accounts)
    }
    val matchedCategory = remember(parsed, categories) {
        SafeVoiceTextParser.resolveMatchingCategory(parsed, categories)
    }
    val isSaveEnabled = spokenText.isNotBlank()
    val willAutoCreateWallet = isSaveEnabled && parsed.hasExplicitWalletMode && matchedAccount == null
    val willAutoCreateCategory = isSaveEnabled && matchedCategory == null

    val animatedWaveScale by animateFloatAsState(
        targetValue = if (isListening) (1.0f + (rmsDb / 24f)).coerceIn(1.0f, 1.45f) else 1.0f,
        label = "safeVoiceWaveScale"
    )

    val quickChips = listOf(
        "500 UPI Petrol",
        "1200 Food",
        "3000 Gym",
        "400 PhonePe Medical",
        "15000 Salary HDFC"
    )

    Dialog(
        onDismissRequest = {
            speechWrapper.stopAndDestroy()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.62f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    speechWrapper.stopAndDestroy()
                    onDismiss()
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = DarkCanvasBg,
                border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonMint.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = NeonMint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Global Smart Voice Engine",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${Locale.getDefault().displayName} • Streaming • ₹0",
                                    color = NeonMint,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                speechWrapper.stopAndDestroy()
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Voice Sheet", tint = Color.White)
                        }
                    }

                    // Mic Ring & Live Status Banner
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(84.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .scale(animatedWaveScale)
                                    .clip(CircleShape)
                                    .background(NeonMint.copy(alpha = if (isListening) 0.28f else 0.10f))
                            )
                            Surface(
                                shape = CircleShape,
                                color = if (isListening) NeonMint else DarkSurfaceElevated,
                                border = BorderStroke(2.dp, NeonMint),
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable {
                                        try {
                                            if (isListening) {
                                                speechWrapper.stopListening()
                                                isListening = false
                                            } else {
                                                safeStartSpeech()
                                            }
                                        } catch (_: Throwable) {
                                            speechWrapper.stopAndDestroy()
                                            isListening = false
                                            fallbackActive = true
                                            statusMessage = "Use Neon Command Box below"
                                        }
                                    }
                                    .testTag("voice_sheet_mic_trigger")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = if (isListening) "Stop Listening" else "Tap Mic to Speak",
                                        tint = if (isListening) DarkCanvasBg else NeonMint,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (fallbackActive || isListening) NeonMint.copy(alpha = 0.12f) else DarkSurfaceElevated
                        ) {
                            Text(
                                text = statusMessage,
                                color = if (isListening || fallbackActive) NeonMint else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                maxLines = 2
                            )
                        }
                    }

                    // In-App Neon Command Text Box (Updates live while speaking)
                    OutlinedTextField(
                        value = spokenText,
                        onValueChange = { spokenText = it },
                        label = { Text("Neon Command Box", color = NeonMint, fontWeight = FontWeight.SemiBold) },
                        placeholder = { Text("Speak or type (e.g. '500 UPI Petrol', 'दो सौ चाय')", color = Color(0xFF64748B)) },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = NeonMint.copy(alpha = 0.55f),
                            cursorColor = NeonMint,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_transcript_input")
                    )

                    // Interactive Live Preview Pills ([Amount], [Expense/Income], [Category], [Wallet])
                    AnimatedVisibility(
                        visible = isSaveEnabled,
                        enter = fadeIn(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Live Parsed Preview",
                                color = NeonMint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val amountLabel = if (parsed.amountDisplay > 0.0) {
                                "${viewModel.activeSymbol()}${String.format(Locale.US, "%.2f", parsed.amountDisplay)}"
                            } else {
                                "Add Amount"
                            }
                            val isExp = parsed.transactionType == TransactionType.EXPENSE

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("voice_live_preview_pills"),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    VoicePreviewPill(
                                        icon = Icons.Default.Payments,
                                        text = amountLabel,
                                        accent = if (parsed.amountDisplay > 0.0) NeonMint else StateRedNeon,
                                        modifier = Modifier.weight(1f),
                                        testTag = "voice_pill_amount"
                                    )
                                    VoicePreviewPill(
                                        icon = Icons.Default.SwapVert,
                                        text = if (isExp) "Expense" else "Income",
                                        accent = if (isExp) StateRed else StateGreen,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            typeOverride = if (isExp) TransactionType.INCOME else TransactionType.EXPENSE
                                        },
                                        modifier = Modifier.weight(1f),
                                        testTag = "voice_pill_type"
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    VoicePreviewPill(
                                        icon = Icons.Default.Category,
                                        text = matchedCategory?.name ?: "${parsed.categoryHint}${if (willAutoCreateCategory) " • NEW" else ""}",
                                        accent = NeonMint,
                                        modifier = Modifier.weight(1f),
                                        testTag = "voice_pill_category"
                                    )
                                    VoicePreviewPill(
                                        icon = Icons.Default.AccountBalanceWallet,
                                        text = matchedAccount?.name ?: "${parsed.paymentModeLabel}${if (willAutoCreateWallet) " • NEW" else ""}",
                                        accent = ElectricCyan,
                                        modifier = Modifier.weight(1f),
                                        testTag = "voice_pill_wallet"
                                    )
                                }
                            }
                        }
                    }

                    // 1-Tap Neon Test Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "1-Tap Neon Command Chips",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickChips.forEach { sample ->
                                val selected = spokenText.equals(sample, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) NeonMint.copy(alpha = 0.22f) else DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, if (selected) NeonMint else Color(0xFF283548)),
                                    modifier = Modifier
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            spokenText = sample
                                            typeOverride = null
                                        }
                                        .testTag("voice_chip_${sample.replace(" ", "_").lowercase()}")
                                ) {
                                    Text(
                                        text = sample,
                                        color = if (selected) NeonMint else Color(0xFFE2E8F0),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Parsed Transaction Summary Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Detected Amount", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    Text(
                                        text = if (parsed.amountDisplay > 0.0) {
                                            "${viewModel.activeSymbol()}${String.format(Locale.US, "%.2f", parsed.amountDisplay)}"
                                        } else {
                                            "${viewModel.activeSymbol()}0.00"
                                        },
                                        color = if (parsed.amountDisplay > 0.0) NeonMint else Color(0xFF64748B),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { txType ->
                                        val selected = parsed.transactionType == txType
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = when {
                                                selected && txType == TransactionType.EXPENSE -> StateRed
                                                selected && txType == TransactionType.INCOME -> StateGreen
                                                else -> Color(0xFF1E293B)
                                            },
                                            modifier = Modifier.clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                typeOverride = txType
                                            }
                                        ) {
                                            Text(
                                                text = if (txType == TransactionType.EXPENSE) "Expense" else "Income",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkCanvasBg,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = CategoryIconHelper.iconFor(matchedCategory?.iconName ?: parsed.suggestedIconName),
                                                contentDescription = null,
                                                tint = NeonMint,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text("Section", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = matchedCategory?.name ?: parsed.categoryHint,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (willAutoCreateCategory) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = NeonMint.copy(alpha = 0.16f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonMint, modifier = Modifier.size(10.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("AUTO-CREATE", color = NeonMint, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkCanvasBg,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = ElectricCyan,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text("Mode / Wallet", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = matchedAccount?.name ?: parsed.paymentModeLabel,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (willAutoCreateWallet) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = ElectricCyan.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = "AUTO-CREATE WALLET",
                                                    color = ElectricCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons (Save Voice Entry enabled directly whenever spokenText.isNotBlank())
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                safeStartSpeech()
                            },
                            border = BorderStroke(1.dp, NeonMint),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = NeonMint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListening) "Restart Mic" else "Speak",
                                color = NeonMint,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                speechWrapper.stopAndDestroy()
                                isListening = false

                                if (parsed.amountDisplay <= 0.0) {
                                    statusMessage = "Please speak or enter an amount (e.g. 250) to save"
                                    return@Button
                                }

                                // Ensure ViewModel's parser always receives an explicit numeric amount even for
                                // spoken multilingual words (e.g., "दो सौ पचास", "quinientos", "1.5k").
                                val hasAsciiDigits = Regex("\\d").containsMatchIn(spokenText)
                                val submissionText = if (hasAsciiDigits && !spokenText.contains(Regex("\\d\\s*k\\b", RegexOption.IGNORE_CASE))) {
                                    spokenText.trim()
                                } else {
                                    val amtStr = if (parsed.amountDisplay % 1.0 == 0.0) {
                                        parsed.amountDisplay.toLong().toString()
                                    } else {
                                        String.format(Locale.US, "%.2f", parsed.amountDisplay)
                                    }
                                    "$amtStr ${parsed.paymentModeLabel} ${parsed.categoryHint}"
                                }

                                viewModel.processVoiceTransaction(
                                    rawText = submissionText,
                                    availableAccounts = accounts,
                                    availableCategories = categories,
                                    overrideType = typeOverride ?: parsed.transactionType
                                )
                            },
                            enabled = isSaveEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonMint,
                                contentColor = DarkCanvasBg,
                                disabledContainerColor = Color(0xFF1E293B),
                                disabledContentColor = Color(0xFF64748B)
                            ),
                            border = if (isSaveEnabled) BorderStroke(1.5.dp, ElectricCyan) else null,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("voice_save_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Voice Entry", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoicePreviewPill(
    icon: ImageVector,
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
