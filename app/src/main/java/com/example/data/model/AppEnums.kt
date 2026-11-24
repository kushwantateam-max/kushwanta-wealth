package com.example.data.model

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

enum class AccountType {
    BANK,
    CASH,
    CREDIT_CARD,
    INVESTMENT
}

enum class FinancialBook(val id: String, val title: String) {
    PERSONAL("personal", "Personal"),
    FAMILY("family", "Family"),
    BUSINESS("business", "Business")
}

enum class ThemeMode(val id: String, val label: String) {
    SYSTEM("SYSTEM", "System Auto"),
    LIGHT("LIGHT", "White Glass"),
    DARK("DARK", "Dark Neon")
}

enum class AccentPalette(val id: String, val label: String, val primaryHex: Long, val secondaryHex: Long) {
    COBALT("COBALT", "Cobalt Blue", 0xFF2563EB, 0xFF00B4D8),
    NEON_MINT("NEON_MINT", "Cyber Neon", 0xFF00B894, 0xFF00FFCC),
    CYAN("CYAN", "Electric Cyan", 0xFF00B4D8, 0xFF00E5FF),
    EMERALD("EMERALD", "Neon Emerald", 0xFF059669, 0xFF10B981),
    PURPLE("PURPLE", "Cyber Violet", 0xFF7C3AED, 0xFFA855F7),
    ROSE("ROSE", "Neon Crimson", 0xFFE11D48, 0xFFFF3366)
}

enum class CurrencyCode(
    val code: String,
    val symbol: String,
    val flag: String,
    val countryName: String,
    val displayName: String,
    val exchangeRateToUsd: Double
) {
    USD("USD", "$", "🇺🇸", "United States", "US Dollar ($)", 1.0),
    INR("INR", "₹", "🇮🇳", "India", "Indian Rupee (₹)", 86.5),
    EUR("EUR", "€", "🇪🇺", "Eurozone", "Euro (€)", 0.95),
    GBP("GBP", "£", "🇬🇧", "United Kingdom", "British Pound (£)", 0.80),
    AED("AED", "د.إ", "🇦🇪", "UAE", "UAE Dirham (د.إ)", 3.67),
    SAR("SAR", "﷼", "🇸🇦", "Saudi Arabia", "Saudi Riyal (﷼)", 3.75),
    JPY("JPY", "¥", "🇯🇵", "Japan", "Japanese Yen (¥)", 154.0),
    AUD("AUD", "A$", "🇦🇺", "Australia", "Australian Dollar (A$)", 1.54),
    CAD("CAD", "C$", "🇨🇦", "Canada", "Canadian Dollar (C$)", 1.40),
    SGD("SGD", "S$", "🇸🇬", "Singapore", "Singapore Dollar (S$)", 1.34),
    CHF("CHF", "CHF", "🇨🇭", "Switzerland", "Swiss Franc (CHF)", 0.88),
    CNY("CNY", "¥", "🇨🇳", "China", "Chinese Yuan (¥)", 7.24),
    KRW("KRW", "₩", "🇰🇷", "South Korea", "South Korean Won (₩)", 1405.0),
    BRL("BRL", "R$", "🇧🇷", "Brazil", "Brazilian Real (R$)", 5.80),
    MXN("MXN", "Mex$", "🇲🇽", "Mexico", "Mexican Peso (Mex$)", 20.4),
    ZAR("ZAR", "R", "🇿🇦", "South Africa", "South African Rand (R)", 18.1),
    TRY("TRY", "₺", "🇹🇷", "Turkey", "Turkish Lira (₺)", 34.4),
    RUB("RUB", "₽", "🇷🇺", "Russia", "Russian Ruble (₽)", 98.0),
    IDR("IDR", "Rp", "🇮🇩", "Indonesia", "Indonesian Rupiah (Rp)", 15850.0),
    THB("THB", "฿", "🇹🇭", "Thailand", "Thai Baht (฿)", 34.8),
    MYR("MYR", "RM", "🇲🇾", "Malaysia", "Malaysian Ringgit (RM)", 4.46),
    PHP("PHP", "₱", "🇵🇭", "Philippines", "Philippine Peso (₱)", 58.7),
    VND("VND", "₫", "🇻🇳", "Vietnam", "Vietnamese Dong (₫)", 25350.0),
    PKR("PKR", "₨", "🇵🇰", "Pakistan", "Pakistani Rupee (₨)", 278.0),
    BDT("BDT", "৳", "🇧🇩", "Bangladesh", "Bangladeshi Taka (৳)", 119.5),
    NPR("NPR", "रू", "🇳🇵", "Nepal", "Nepalese Rupee (रू)", 138.4),
    LKR("LKR", "Rs", "🇱🇰", "Sri Lanka", "Sri Lankan Rupee (Rs)", 293.0),
    NGN("NGN", "₦", "🇳🇬", "Nigeria", "Nigerian Naira (₦)", 1670.0),
    EGP("EGP", "E£", "🇪🇬", "Egypt", "Egyptian Pound (E£)", 49.3),
    NZD("NZD", "NZ$", "🇳🇿", "New Zealand", "New Zealand Dollar (NZ$)", 1.69),
    SEK("SEK", "kr", "🇸🇪", "Sweden", "Swedish Krona (kr)", 10.9),
    NOK("NOK", "kr", "🇳🇴", "Norway", "Norwegian Krone (kr)", 11.1),
    PLN("PLN", "zł", "🇵🇱", "Poland", "Polish Zloty (zł)", 4.10),
    HKD("HKD", "HK$", "🇭🇰", "Hong Kong", "Hong Kong Dollar (HK$)", 7.78),
    QAR("QAR", "QR", "🇶🇦", "Qatar", "Qatari Riyal (QR)", 3.64),
    KWD("KWD", "KD", "🇰🇼", "Kuwait", "Kuwaiti Dinar (KD)", 0.31)
}

object EmojiMatcher {
    fun suggestEmoji(title: String): String {
        val t = title.lowercase().trim()
        return when {
            t.contains("coffee") || t.contains("chai") || t.contains("tea") || t.contains("cafe") -> "☕"
            t.contains("fuel") || t.contains("petrol") || t.contains("diesel") || t.contains("gas") -> "⛽"
            t.contains("grocer") || t.contains("veg") || t.contains("milk") || t.contains("mart") -> "🛒"
            t.contains("food") || t.contains("dining") || t.contains("lunch") || t.contains("dinner") || t.contains("burger") || t.contains("biryani") -> "🍔"
            t.contains("pizza") -> "🍕"
            t.contains("electric") || t.contains("power") || t.contains("bill") || t.contains("util") -> "⚡"
            t.contains("metro") || t.contains("train") || t.contains("cab") || t.contains("uber") || t.contains("auto") || t.contains("bus") -> "🚕"
            t.contains("flight") || t.contains("travel") || t.contains("trip") -> "✈️"
            t.contains("rent") || t.contains("home") || t.contains("house") -> "🏠"
            t.contains("med") || t.contains("pharma") || t.contains("doctor") || t.contains("health") -> "💊"
            t.contains("gym") || t.contains("fit") || t.contains("sport") -> "🏋️"
            t.contains("movie") || t.contains("netflix") || t.contains("cinema") || t.contains("game") -> "🎬"
            t.contains("shop") || t.contains("cloth") || t.contains("amazon") || t.contains("flipkart") -> "🛍️"
            t.contains("phone") || t.contains("recharge") || t.contains("wifi") || t.contains("internet") -> "📱"
            t.contains("salary") || t.contains("bonus") || t.contains("pay") -> "💰"
            t.contains("gift") || t.contains("party") -> "🎁"
            t.contains("book") || t.contains("edu") || t.contains("school") || t.contains("fee") -> "📚"
            else -> "💳"
        }
    }

    val PRESET_EMOJIS = listOf(
        "☕", "⛽", "🛒", "🍔", "🍕", "⚡", "🚕", "✈️",
        "🏠", "💊", "🏋️", "🎬", "🛍️", "📱", "💰", "🎁", "📚", "💳"
    )
}
