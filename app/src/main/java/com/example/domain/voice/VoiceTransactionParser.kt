package com.example.domain.voice

import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType

data class VoiceParseResult(
    val rawText: String,
    val amountDisplay: Double,
    val transactionType: TransactionType,
    val paymentModeLabel: String,
    val preferredAccountType: AccountType,
    val hasExplicitWalletMode: Boolean,
    val categoryHint: String,
    val note: String,
    val suggestedIconName: String,
    val suggestedColorHex: String
)

object VoiceTransactionParser {

    private val NEON_COLORS = listOf(
        "#00FFCC", // Cyber Neon Mint
        "#00E5FF", // Electric Cyan
        "#A855F7", // Neon Violet
        "#FF3366", // Neon Crimson
        "#10B981", // Neon Emerald
        "#F59E0B"  // Neon Amber
    )

    private val INCOME_KEYWORDS = listOf(
        "received", "receive", "credited", "credit", "earned", "earn",
        "income", "salary", "bonus", "refund", "cashback", "got paid",
        "freelance", "dividend", "profit",
        "mila", "mili", "mile", "aaya", "aaye", "jama", "tanakhwah", "vetan",
        "प्राप्त", "मिला", "मिले", "आय", "सैलरी", "कमाई", "कैशबैक", "बोनस"
    )

    private val EXPENSE_KEYWORDS = listOf(
        "spent", "spend", "paid", "pay", "bought", "buy", "purchase",
        "debited", "expense", "kharch", "diya", "diye", "bheja",
        "खर्च", "दिया", "दिए", "भुगतान", "खरीदा"
    )

    private val STOP_WORDS = setOf(
        "i", "me", "my", "spent", "spend", "paid", "pay", "bought", "buy", "purchase",
        "received", "receive", "credited", "earned", "earn", "got", "debited", "added",
        "on", "for", "via", "by", "with", "from", "in", "to", "at", "of", "the", "a", "an",
        "and", "using", "through", "into", "is", "was", "expense", "income", "transaction",
        "rupees", "rupee", "rupaye", "rupaya", "rs", "inr", "dollar", "dollars", "usd",
        "euro", "euros", "eur", "pound", "pounds", "gbp", "yen", "jpy", "aed", "dirham",
        "bucks", "taka", "रुपये", "रुपए", "रुपया", "पैसा", "पैसे",
        "se", "ke", "liye", "mein", "me", "ko", "ka", "ki", "par", "pe", "kharch", "diya", "diye",
        "mila", "mili", "mile", "aaya", "aaye", "bheja", "से", "के", "लिए", "में", "को", "का", "की", "पर",
        "खर्च", "दिया", "दिए", "भुगतान", "खरीदा", "मिला", "मिले", "प्राप्त", "यूपीआई"
    )

    private val MODE_STOP_WORDS = setOf(
        "upi", "gpay", "google", "phonepe", "phone", "pe", "paytm", "bhim",
        "cash", "wallet", "pocket", "bank", "account", "card", "credit", "debit",
        "visa", "mastercard", "hdfc", "axis", "sbi", "icici", "kotak", "chase", "citi",
        "कैश", "नकद", "बैंक", "कार्ड", "क्रेडिट", "यूपीआई", "पेटीएम", "फोनपे"
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

    fun parse(rawInput: String): VoiceParseResult {
        val normalized = normalizeDigits(rawInput).trim()
        val lower = normalized.lowercase()

        val amount = extractAmount(normalized)
        val txType = detectTransactionType(lower)
        val (modeLabel, accType, explicitMode) = detectPaymentMode(lower)
        val (catHint, iconName, colorHex) = detectOrExtractCategory(normalized, lower, txType)

        val cleanNote = normalized.ifBlank { "$catHint ($modeLabel)" }

        return VoiceParseResult(
            rawText = rawInput.trim(),
            amountDisplay = amount,
            transactionType = txType,
            paymentModeLabel = modeLabel,
            preferredAccountType = accType,
            hasExplicitWalletMode = explicitMode,
            categoryHint = catHint,
            note = cleanNote,
            suggestedIconName = iconName,
            suggestedColorHex = colorHex
        )
    }

    private fun extractAmount(normalized: String): Double {
        // Remove commas inside digits like 1,250.50 -> 1250.50
        val compactNumbers = normalized.replace(Regex("(?<=\\d),(?=\\d)"), "")
        val numberRegex = Regex("(\\d+(?:\\.\\d{1,2})?)")
        val matches = numberRegex.findAll(compactNumbers).toList()
        if (matches.isNotEmpty()) {
            // Prefer number closest to currency markers if multiple numbers exist
            if (matches.size == 1) {
                return matches.first().value.toDoubleOrNull() ?: 0.0
            }
            val currencyMatch = Regex("(?:rs\\.?|inr|usd|eur|gbp|₹|\\$|€|£|rupees?|rupaye|dollars?|bucks)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:rs\\.?|inr|usd|eur|gbp|₹|\\$|€|£|rupees?|rupaye|dollars?|bucks)", RegexOption.IGNORE_CASE)
                .find(compactNumbers)
            if (currencyMatch != null) {
                val rawNum = currencyMatch.groupValues.drop(1).firstOrNull { it.isNotBlank() }
                rawNum?.toDoubleOrNull()?.let { if (it > 0.0) return it }
            }
            return matches.mapNotNull { it.value.toDoubleOrNull() }.maxOrNull() ?: 0.0
        }

        // Fallback word-based numbers for spoken phrases without digits
        val lower = normalized.lowercase()
        return when {
            lower.contains("thousand") || lower.contains("hazaar") || lower.contains("hazar") -> 1000.0
            lower.contains("five hundred") || lower.contains("panch sau") -> 500.0
            lower.contains("two hundred") || lower.contains("do sau") -> 200.0
            lower.contains("hundred") || lower.contains("sau") -> 100.0
            lower.contains("fifty") || lower.contains("pachas") -> 50.0
            else -> 0.0
        }
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
            lower.contains("upi") || lower.contains("यूपीआई") -> Triple("UPI Wallet", AccountType.BANK, true)
            lower.contains("credit card") || lower.contains("visa") || lower.contains("mastercard") || lower.contains("क्रेडिट कार्ड") || lower.contains("card") ->
                Triple("Credit Card", AccountType.CREDIT_CARD, true)
            lower.contains("cash") || lower.contains("pocket") || lower.contains("नकद") || lower.contains("कैश") ->
                Triple("Cash", AccountType.CASH, true)
            lower.contains("hdfc") -> Triple("HDFC Bank", AccountType.BANK, true)
            lower.contains("axis") -> Triple("Axis Bank", AccountType.BANK, true)
            lower.contains("sbi") -> Triple("SBI Bank", AccountType.BANK, true)
            lower.contains("icici") -> Triple("ICICI Bank", AccountType.BANK, true)
            lower.contains("kotak") -> Triple("Kotak Bank", AccountType.BANK, true)
            lower.contains("bank") || lower.contains("बैंक") -> Triple("Bank Account", AccountType.BANK, true)
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
                lower.contains("salary") || lower.contains("paycheck") || lower.contains("vetan") || lower.contains("सैलरी") ->
                    Triple("Salary", "payments", "#10B981")
                lower.contains("freelance") || lower.contains("client") || lower.contains("project") ->
                    Triple("Freelancing Work", "laptop", "#10B981")
                lower.contains("bonus") || lower.contains("gift") || lower.contains("cashback") ->
                    Triple("Bonus & Rewards", "card_giftcard", "#00FFCC")
                else -> {
                    val custom = extractCustomSectionName(normalized) ?: "Side Income"
                    Triple(custom, "payments", "#00FFCC")
                }
            }
        }

        // Known built-in defaults first
        when {
            lower.containsAny("coffee", "tea", "chai", "starbucks", "lunch", "dinner", "breakfast", "restaurant", "pizza", "burger", "biryani", "food", "snack", "चाय", "कॉफी", "खाना", "नाश्ता") ->
                return Triple("Food & Drinks", "restaurant", "#06B6D4")
            lower.containsAny("grocery", "groceries", "vegetable", "vegetables", "milk", "fruit", "mart", "supermarket", "ration", "kirana", "सब्जी", "दूध", "किराना", "राशन") ->
                return Triple("Groceries", "local_grocery_store", "#F59E0B")
            lower.containsAny("fuel", "petrol", "diesel", "uber", "ola", "taxi", "cab", "metro", "bus", "train", "auto", "rickshaw", "parking", "transport", "पेट्रोल", "डीजल", "मेट्रो", "टैक्सी") ->
                return Triple("Transport & Fuel", "directions_car", "#0EA5E9")
            lower.containsAny("rent", "electric", "electricity", "water", "wifi", "internet", "broadband", "bill", "utility", "recharge", "mobile", "किराया", "बिजली", "बिल", "रिचार्ज") ->
                return Triple("Rent & Bills", "home", "#6366F1")
            lower.containsAny("shopping", "amazon", "flipkart", "clothes", "shoes", "dress", "mall", "शॉपिंग", "कपड़े") ->
                return Triple("Shopping", "shopping_bag", "#EC4899")
            lower.containsAny("movie", "cinema", "netflix", "spotify", "youtube", "concert", "game", "gaming", "फिल्म", "मूवी") ->
                return Triple("Entertainment", "movie", "#8B5CF6")
            lower.containsAny("sip", "mutual", "stock", "share", "invest", "investment") ->
                return Triple("Mutual Funds & Stocks", "trending_up", "#3B82F6")
        }

        // Specialized auto-created custom sections with matching neon icon/color
        val customWord = extractCustomSectionName(normalized)
        return when {
            lower.containsAny("gym", "fitness", "workout", "yoga", "protein", "जिम") ->
                Triple(customWord ?: "Gym & Fitness", "fitness_center", "#00FFCC")
            lower.containsAny("medical", "medicine", "pharmacy", "doctor", "hospital", "clinic", "health", "tablet", "दवा", "डॉक्टर") ->
                Triple(customWord ?: "Medical & Health", "medical_services", "#FF3366")
            lower.containsAny("pet", "dog", "cat", "vet") ->
                Triple(customWord ?: "Pet Care", "pets", "#A855F7")
            lower.containsAny("flight", "hotel", "travel", "trip", "vacation", "ticket", "यात्रा") ->
                Triple(customWord ?: "Travel & Trips", "flight", "#00E5FF")
            lower.containsAny("book", "books", "school", "college", "course", "tuition", "education", "study", "किताब", "स्कूल") ->
                Triple(customWord ?: "Education", "school", "#3B82F6")
            lower.containsAny("gift", "birthday", "party", "wedding", "गिफ्ट", "तोहफा") ->
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
            .replace(Regex("[₹$€£¥,!?.:;\"'()\\[\\]]"), " ")
            .replace(Regex("\\d+(?:\\.\\d+)?"), " ")
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { token ->
                val low = token.lowercase()
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
        parseResult: VoiceParseResult,
        accounts: List<AccountEntity>
    ): AccountEntity? {
        if (accounts.isEmpty()) return null
        if (!parseResult.hasExplicitWalletMode) {
            return accounts.firstOrNull { it.isDefault } ?: accounts.first()
        }
        val mode = parseResult.paymentModeLabel.lowercase()
        val firstToken = mode.split(" ").firstOrNull() ?: mode
        // Match exact or token inside existing account name (e.g., "HDFC Bank" -> "HDFC Bank", "Cash" -> "Cash & Physical Wallet", "UPI" -> "UPI Wallet")
        return accounts.firstOrNull { acc ->
            val accLow = acc.name.lowercase()
            accLow == mode ||
                (firstToken.length >= 3 && accLow.contains(firstToken)) ||
                (parseResult.preferredAccountType == AccountType.CASH && acc.type == AccountType.CASH) ||
                (parseResult.preferredAccountType == AccountType.CREDIT_CARD && acc.type == AccountType.CREDIT_CARD)
        }
    }

    fun resolveMatchingCategory(
        parseResult: VoiceParseResult,
        categories: List<CategoryEntity>
    ): CategoryEntity? {
        val isExp = parseResult.transactionType == TransactionType.EXPENSE
        val hintLow = parseResult.categoryHint.lowercase()
        val sameType = categories.filter { it.isExpense == isExp }
        return sameType.firstOrNull { it.name.equals(parseResult.categoryHint, ignoreCase = true) }
            ?: sameType.firstOrNull { cat ->
                val catLow = cat.name.lowercase()
                catLow.contains(hintLow) || hintLow.contains(catLow)
            }
            ?: categories.firstOrNull { it.name.equals(parseResult.categoryHint, ignoreCase = true) }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}
