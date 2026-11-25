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

    // --- Kushwanta Elite Hub Localization (EN / HI) ---
    fun eliteHubTitle(lang: String) =
        if (lang == "HI") "कुशवंता एलीट हब" else "Kushwanta Elite Hub"
    fun eliteHubEntryTitle(lang: String) =
        if (lang == "HI") "एलीट हब कम्युनिटी" else "Elite Hub Community"
    fun eliteHubEntrySubtitle(lang: String) =
        if (lang == "HI") "आधिकारिक व्हाट्सएप चैनल और वीआईपी कम्युनिटी से जुड़ें" else "Join the Official Channel & VIP Member Community"
    fun eliteHubOfficialBadge(lang: String) =
        if (lang == "HI") "आधिकारिक" else "Official"
    fun eliteHubBackContentDesc(lang: String) =
        if (lang == "HI") "पिछली स्क्रीन पर वापस जाएं" else "Navigate back"
    fun eliteHubHeroHeadline(lang: String) =
        if (lang == "HI") "असाधारण से आगे। साथ मिलकर निर्मित।" else "BEYOND ORDINARY. BUILT TOGETHER."
    fun eliteHubHeroSubtitle(lang: String) =
        if (lang == "HI") "आपकी कम्युनिटी। आपका इकोसिस्टम। आपका भविष्य।" else "Your Community. Your Ecosystem. Your Future."
    fun eliteHubHeroDescription(lang: String) =
        if (lang == "HI") {
            "कुशवंता के आधिकारिक कम्युनिटी गेटवे में आपका स्वागत है — जहाँ स्मार्ट वेल्थ ओएस और माइंड एरिना के सदस्य अपडेट, विचार और नए फीचर्स से जुड़ते हैं।"
        } else {
            "Welcome to Kushwanta's official community gateway — connecting Smart Wealth OS and Mind Arena members with verified updates, releases, and shared progress."
        }

    fun eliteHubPrimaryBadge(lang: String) =
        if (lang == "HI") "आधिकारिक ब्रॉडकास्ट" else "OFFICIAL BROADCAST"
    fun eliteHubPrimaryTitle(lang: String) =
        if (lang == "HI") "आधिकारिक व्हाट्सएप चैनल से जुड़ें" else "JOIN OFFICIAL WHATSAPP CHANNEL"
    fun eliteHubPrimaryDesc(lang: String) =
        if (lang == "HI") {
            "नए फीचर रोलआउट, स्मार्ट वेल्थ टूल्स के अपडेट, माइंड एरिना घोषणाएं और आधिकारिक समाचार सीधे पाएं।"
        } else {
            "Receive verified product announcements, new calculator releases, Mind Arena updates, and official ecosystem news."
        }
    fun eliteHubPrimaryCta(lang: String) =
        if (lang == "HI") "आधिकारिक चैनल से जुड़ें" else "JOIN OFFICIAL CHANNEL"

    fun eliteHubSecondaryBadge(lang: String) =
        if (lang == "HI") "सदस्य चर्चा समूह" else "MEMBER DISCUSSION"
    fun eliteHubSecondaryTitle(lang: String) =
        if (lang == "HI") "कुशवंता वीआईपी कम्युनिटी से जुड़ें" else "JOIN KUSHWANTA VIP COMMUNITY"
    fun eliteHubSecondaryDesc(lang: String) =
        if (lang == "HI") {
            "अन्य कुशवंता सदस्यों से जुड़ें, फाइनेंशियल टूल्स व माइंड एरिना पर फीडबैक साझा करें और कम्युनिटी चर्चा में भाग लें।"
        } else {
            "Connect with fellow Kushwanta members, exchange thoughts on wealth utilities and Mind Arena games, and share product feedback."
        }
    fun eliteHubSecondaryCta(lang: String) =
        if (lang == "HI") "वीआईपी कम्युनिटी से जुड़ें" else "JOIN VIP COMMUNITY"

    fun eliteHubBenefitsHeader(lang: String) =
        if (lang == "HI") "एलीट लाभ" else "ELITE BENEFITS"
    fun eliteHubBenefitGameTitle(lang: String) =
        if (lang == "HI") "अर्ली गेम एक्सेस" else "EARLY GAME ACCESS"
    fun eliteHubBenefitGameDesc(lang: String) =
        if (lang == "HI") {
            "माइंड एरिना के नए ब्रेन गेम्स, चैलेंज राउंड्स और गेमप्ले अपडेट्स की जानकारी सबसे पहले पाएं।"
        } else {
            "Stay informed on upcoming Mind Arena brain games, fresh challenge modes, and gameplay enhancements."
        }
    fun eliteHubBenefitWealthTitle(lang: String) =
        if (lang == "HI") "वेल्थ इनसाइट्स" else "WEALTH INSIGHTS"
    fun eliteHubBenefitWealthDesc(lang: String) =
        if (lang == "HI") {
            "व्यक्तिगत वित्त कैलकुलेटर, बजटिंग आदतों और स्मार्ट मनी टूल्स के व्यावहारिक शैक्षिक नोट्स पढ़ें।"
        } else {
            "Explore practical educational notes on budgeting workflows, calculators, and personal finance discipline."
        }
    fun eliteHubBenefitUpdatesTitle(lang: String) =
        if (lang == "HI") "एक्सक्लूसिव अपडेट्स" else "EXCLUSIVE UPDATES"
    fun eliteHubBenefitUpdatesDesc(lang: String) =
        if (lang == "HI") {
            "ऐप के नए वर्जन, सुरक्षा सुधार और कम्युनिटी पहलों की सीधी सूचना प्राप्त करें।"
        } else {
            "Follow major app releases, reliability improvements, and official Kushwanta ecosystem announcements."
        }

    fun eliteHubPillarsHeader(lang: String) =
        if (lang == "HI") "इकोसिस्टम स्तंभ" else "ECOSYSTEM PILLARS"
    fun eliteHubPillarWealthTitle(lang: String) =
        if (lang == "HI") "स्मार्ट वेल्थ ओएस" else "SMART WEALTH OS"
    fun eliteHubPillarWealthDesc(lang: String) =
        if (lang == "HI") "120+ पर्सनल फाइनेंस कैलकुलेटर, बजट और विश्लेषण टूल्स" else "120+ personal finance calculators, trackers, and analytics utilities"
    fun eliteHubPillarWealthAction(lang: String) =
        if (lang == "HI") "टूल्स खोलें" else "Open Tools"
    fun eliteHubPillarArenaTitle(lang: String) =
        if (lang == "HI") "माइंड एरिना" else "MIND ARENA"
    fun eliteHubPillarArenaDesc(lang: String) =
        if (lang == "HI") "फोकस और रणनीति निखारने के लिए 10 ब्रेन गेम्स" else "10 brain & strategy games built for focus and daily mastery"
    fun eliteHubPillarArenaAction(lang: String) =
        if (lang == "HI") "एरिना खोलें" else "Open Arena"

    fun eliteHubTrustHeader(lang: String) =
        if (lang == "HI") "कम्युनिटी विश्वास और पारदर्शिता" else "COMMUNITY TRUST & TRANSPARENCY"
    fun eliteHubTrustOfficial(lang: String) =
        if (lang == "HI") "आधिकारिक संवाद — केवल ऐप में दिए गए सत्यापित HTTPS लिंक का ही उपयोग करें।" else "Official communication — only use verified HTTPS links published inside Kushwanta."
    fun eliteHubTrustRespect(lang: String) =
        if (lang == "HI") "सम्मानजनक सहभागिता — सभी सदस्यों के लिए रचनात्मक और स्पैम-मुक्त वातावरण बनाए रखें।" else "Respectful participation — keep discussions constructive, courteous, and free from spam."
    fun eliteHubTrustPrivacy(lang: String) =
        if (lang == "HI") "गोपनीयता सुरक्षा — कुशवंता कभी भी आपका पिन, पासवर्ड, ओटीपी या बैंक विवरण नहीं मांगता।" else "Privacy awareness — Kushwanta never asks for your PIN, password, OTP, or financial credentials."
    fun eliteHubTrustEducation(lang: String) =
        if (lang == "HI") "जिम्मेदार वित्तीय शिक्षा — साझा की गई जानकारी केवल शैक्षिक उद्देश्य के लिए है, निवेश सलाह नहीं।" else "Responsible financial education — content is purely educational, not personalized investment advice."
    fun eliteHubTrustGuidelines(lang: String) =
        if (lang == "HI") "कम्युनिटी दिशानिर्देश — बिना किसी सदस्यता या अतिरिक्त शुल्क के पारदर्शी कम्युनिटी अनुभव।" else "Community guidelines — transparent, free participation with zero hidden fees or subscriptions."

    fun eliteHubUrlErrorFallback(lang: String) =
        if (lang == "HI") {
            "लिंक नहीं खुल सका। कृपया सुनिश्चित करें कि व्हाट्सएप या वेब ब्राउज़र इंस्टॉल है।"
        } else {
            "Unable to open link. Please ensure WhatsApp or a web browser is installed."
        }
}
