package com.example.data.prefs

object AppStrings {
    fun home(lang: String) = if (lang == "HI") "होम" else "Home"
    fun budgets(lang: String) = if (lang == "HI") "बजट" else "Budgets"
    fun accounts(lang: String) = if (lang == "HI") "खाते" else "Accounts"
    fun tools(lang: String) = if (lang == "HI") "टूल्स" else "Tools"
    fun settings(lang: String) = if (lang == "HI") "सेटिंग्स" else "Settings"
    fun profile(lang: String) = if (lang == "HI") "प्रोफ़ाइल" else "Profile"

    fun totalNetWorth(lang: String) = if (lang == "HI") "कुल संपत्ति (Net Worth)" else "Total Net Worth"
    fun spendable(lang: String) = if (lang == "HI") "खर्च योग्य" else "Spendable"
    fun income(lang: String) = if (lang == "HI") "आय" else "Income"
    fun expense(lang: String) = if (lang == "HI") "खर्च" else "Expense"
    fun transfer(lang: String) = if (lang == "HI") "ट्रांसफर" else "Transfer"
    fun addExpense(lang: String) = if (lang == "HI") "खर्च जोड़ें" else "Add Expense"
    fun quickPresets(lang: String) = if (lang == "HI") "त्वरित 1-टैप प्रीसेट" else "Quick 1-Tap Presets"
    fun connectedAccounts(lang: String) = if (lang == "HI") "जुड़े हुए खाते" else "Connected Accounts"
    fun recentTransactions(lang: String) = if (lang == "HI") "हालिया लेनदेन" else "Recent Transactions"
    fun dailyStreakTitle(lang: String, days: Int) =
        if (lang == "HI") "$days दिन की लगातार लॉगिन स्ट्रीक 🔥" else "$days-Day Login Streak 🔥"
    fun achievementLocker(lang: String) =
        if (lang == "HI") "अचीवमेंट लॉकर" else "Achievement Locker"
    fun smartMonthlyBudgets(lang: String) =
        if (lang == "HI") "स्मार्ट मासिक बजट" else "Smart Monthly Budgets"
    fun walletsAndBanks(lang: String) =
        if (lang == "HI") "वॉलेट और बैंक खाते" else "Wallets & Bank Accounts"
    fun financialToolsSuite(lang: String) =
        if (lang == "HI") "फाइनेंशियल टूल्स सूट" else "Financial Tools Suite"
    fun preferencesAndSystem(lang: String) =
        if (lang == "HI") "सेटिंग्स और सुरक्षा" else "Preferences & System"
}
