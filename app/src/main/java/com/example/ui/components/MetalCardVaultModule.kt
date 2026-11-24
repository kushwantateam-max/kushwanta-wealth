package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CurrencyCode
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

enum class MetalCardFinish(
    val id: String,
    val label: String,
    val primaryHex: Long,
    val secondaryHex: Long,
    val accentHex: Long,
    val chipPrimaryHex: Long,
    val chipSecondaryHex: Long,
    val textPrimaryHex: Long
) {
    CYBER_OBSIDIAN(
        id = "cyber_obsidian",
        label = "Cyber Obsidian",
        primaryHex = 0xFF0B0C10,
        secondaryHex = 0xFF1A202C,
        accentHex = 0xFF00FFCC,
        chipPrimaryHex = 0xFFCBD5E1,
        chipSecondaryHex = 0xFF64748B,
        textPrimaryHex = 0xFFF8FAFC
    ),
    ELECTRIC_CYAN(
        id = "electric_cyan",
        label = "Electric Cyan",
        primaryHex = 0xFF031F24,
        secondaryHex = 0xFF084C52,
        accentHex = 0xFF00FFCC,
        chipPrimaryHex = 0xFF99F6E4,
        chipSecondaryHex = 0xFF0D9488,
        textPrimaryHex = 0xFFECFEFF
    ),
    ROYAL_GOLD(
        id = "royal_gold",
        label = "Royal Gold",
        primaryHex = 0xFF171206,
        secondaryHex = 0xFF3A2C0A,
        accentHex = 0xFFFFD700,
        chipPrimaryHex = 0xFFFDE68A,
        chipSecondaryHex = 0xFFB45309,
        textPrimaryHex = 0xFFFFFBEB
    ),
    PLATINUM_SILVER(
        id = "platinum_silver",
        label = "Platinum Silver",
        primaryHex = 0xFF1E293B,
        secondaryHex = 0xFF64748B,
        accentHex = 0xFFE2E8F0,
        chipPrimaryHex = 0xFFF1F5F9,
        chipSecondaryHex = 0xFF94A3B8,
        textPrimaryHex = 0xFFFFFFFF
    ),
    EMERALD(
        id = "emerald",
        label = "Emerald",
        primaryHex = 0xFF042218,
        secondaryHex = 0xFF065F46,
        accentHex = 0xFF10B981,
        chipPrimaryHex = 0xFFA7F3D0,
        chipSecondaryHex = 0xFF059669,
        textPrimaryHex = 0xFFECFDF5
    );

    companion object {
        fun fromId(id: String?): MetalCardFinish =
            entries.firstOrNull { it.id == id } ?: CYBER_OBSIDIAN

        fun defaultForAccount(account: AccountEntity, index: Int): MetalCardFinish = when {
            account.name.contains("Emerald", ignoreCase = true) ||
                account.cardColorHex.equals("#10B981", ignoreCase = true) ||
                account.cardColorHex.equals("#042218", ignoreCase = true) -> EMERALD
            account.name.contains("Black", ignoreCase = true) -> CYBER_OBSIDIAN
            account.name.contains("Platinum", ignoreCase = true) -> PLATINUM_SILVER
            account.type == AccountType.CREDIT_CARD -> ROYAL_GOLD
            account.name.contains("UPI", ignoreCase = true) ||
                account.name.contains("Digital", ignoreCase = true) ||
                account.name.contains("Forex", ignoreCase = true) -> ELECTRIC_CYAN
            account.isDefault -> CYBER_OBSIDIAN
            index % 5 == 1 -> ELECTRIC_CYAN
            index % 5 == 2 -> ROYAL_GOLD
            index % 5 == 3 -> PLATINUM_SILVER
            index % 5 == 4 -> EMERALD
            else -> CYBER_OBSIDIAN
        }
    }
}

data class StarterCardSpec(
    val title: String,
    val type: AccountType,
    val balanceUsd: Double,
    val monthlyLimitUsd: Double,
    val last4: String,
    val cardColorHex: String,
    val isDefault: Boolean,
    val finish: MetalCardFinish
)

data class CountryCardPreset(
    val id: String,
    val pillLabel: String,
    val cardTitle: String,
    val accountType: AccountType,
    val finish: MetalCardFinish,
    val cardColorHex: String,
    val defaultLimitUsd: Double,
    val last4: String
)

object CountryCardEngine {

    private val asianDigitalCurrencies = setOf(
        CurrencyCode.INR,
        CurrencyCode.SGD,
        CurrencyCode.JPY,
        CurrencyCode.CNY,
        CurrencyCode.KRW,
        CurrencyCode.IDR,
        CurrencyCode.THB,
        CurrencyCode.MYR,
        CurrencyCode.PHP,
        CurrencyCode.VND,
        CurrencyCode.PKR,
        CurrencyCode.BDT,
        CurrencyCode.NPR,
        CurrencyCode.LKR,
        CurrencyCode.AED,
        CurrencyCode.SAR,
        CurrencyCode.QAR,
        CurrencyCode.KWD,
        CurrencyCode.HKD
    )

    fun isIndiaRegion(currency: CurrencyCode): Boolean = currency == CurrencyCode.INR

    fun isAsiaOrMiddleEastRegion(currency: CurrencyCode): Boolean = currency in asianDigitalCurrencies

    fun regionRailSubtitle(currency: CurrencyCode, account: AccountEntity): String {
        val last4 = account.accountNumberLast4
        return when {
            last4.isNotEmpty() -> "${currency.flag} ${currency.code}  •  ••••  $last4"
            CategoryIconHelper.isUpiWallet(account) || account.name.contains("UPI", ignoreCase = true) ->
                "${currency.flag} ${currency.code}  •  INSTANT DIGITAL RAIL"
            account.name.contains("Forex", ignoreCase = true) ->
                "${currency.flag} MULTI-CURRENCY TRAVEL RAIL"
            else -> "${currency.flag} ${currency.countryName.uppercase()}  •  ${currency.code}"
        }
    }

    fun getStarterCards(currency: CurrencyCode): List<StarterCardSpec> {
        return if (currency == CurrencyCode.INR) {
            listOf(
                StarterCardSpec(
                    title = "Primary Bank Debit",
                    type = AccountType.BANK,
                    balanceUsd = 520.0, // ~ ₹45,000
                    monthlyLimitUsd = 700.0,
                    last4 = "4821",
                    cardColorHex = "#0B0C10",
                    isDefault = true,
                    finish = MetalCardFinish.CYBER_OBSIDIAN
                ),
                StarterCardSpec(
                    title = "UPI / Local Digital",
                    type = AccountType.CASH,
                    balanceUsd = 145.0, // ~ ₹12,500
                    monthlyLimitUsd = 300.0,
                    last4 = "",
                    cardColorHex = "#00B894",
                    isDefault = false,
                    finish = MetalCardFinish.ELECTRIC_CYAN
                ),
                StarterCardSpec(
                    title = "Platinum Credit Card",
                    type = AccountType.CREDIT_CARD,
                    balanceUsd = 350.0, // ~ ₹30,000
                    monthlyLimitUsd = 1750.0,
                    last4 = "9014",
                    cardColorHex = "#475569",
                    isDefault = false,
                    finish = MetalCardFinish.PLATINUM_SILVER
                )
            )
        } else {
            val debitPrefix = when (currency) {
                CurrencyCode.GBP -> "UK Checking Debit Card"
                CurrencyCode.EUR -> "Euro Checking Debit Card"
                CurrencyCode.AED, CurrencyCode.SAR -> "${currency.countryName} Debit Card"
                CurrencyCode.JPY -> "Japan Bank Debit Card"
                CurrencyCode.CAD -> "Canada Checking Debit Card"
                CurrencyCode.AUD -> "Australia Everyday Debit"
                CurrencyCode.BRL -> "Brazil Checking Debit Card"
                CurrencyCode.SGD -> "Singapore Checking Debit"
                else -> "Checking Debit Card"
            }
            listOf(
                StarterCardSpec(
                    title = debitPrefix,
                    type = AccountType.BANK,
                    balanceUsd = 2400.0,
                    monthlyLimitUsd = 3000.0,
                    last4 = "5520",
                    cardColorHex = "#0B0C10",
                    isDefault = true,
                    finish = MetalCardFinish.CYBER_OBSIDIAN
                ),
                StarterCardSpec(
                    title = "Rewards Credit Card",
                    type = AccountType.CREDIT_CARD,
                    balanceUsd = 950.0,
                    monthlyLimitUsd = 4000.0,
                    last4 = "3391",
                    cardColorHex = "#3A2C0A",
                    isDefault = false,
                    finish = MetalCardFinish.ROYAL_GOLD
                ),
                StarterCardSpec(
                    title = "Black Metal Card",
                    type = AccountType.CREDIT_CARD,
                    balanceUsd = 5000.0,
                    monthlyLimitUsd = 10000.0,
                    last4 = "8800",
                    cardColorHex = "#1E293B",
                    isDefault = false,
                    finish = MetalCardFinish.PLATINUM_SILVER
                )
            )
        }
    }

    fun getQuickAddPills(currency: CurrencyCode): List<CountryCardPreset> {
        val base = mutableListOf<CountryCardPreset>()

        if (isAsiaOrMiddleEastRegion(currency)) {
            base += CountryCardPreset(
                id = "upi_local_digital",
                pillLabel = "UPI / Local Digital",
                cardTitle = if (currency == CurrencyCode.INR) "UPI / Local Digital" else "${currency.code} Instant Digital",
                accountType = AccountType.CASH,
                finish = MetalCardFinish.ELECTRIC_CYAN,
                cardColorHex = "#00B894",
                defaultLimitUsd = 500.0,
                last4 = ""
            )
        }

        base += CountryCardPreset(
            id = "black_card",
            pillLabel = "Black Card",
            cardTitle = "Black Metal Card",
            accountType = AccountType.CREDIT_CARD,
            finish = MetalCardFinish.CYBER_OBSIDIAN,
            cardColorHex = "#0B0C10",
            defaultLimitUsd = 10000.0,
            last4 = "8801"
        )
        base += CountryCardPreset(
            id = "credit_card",
            pillLabel = "Credit Card",
            cardTitle = "${currency.code} Rewards Credit",
            accountType = AccountType.CREDIT_CARD,
            finish = MetalCardFinish.ROYAL_GOLD,
            cardColorHex = "#3A2C0A",
            defaultLimitUsd = 5000.0,
            last4 = "4412"
        )
        base += CountryCardPreset(
            id = "debit_card",
            pillLabel = "Debit Card",
            cardTitle = "${currency.countryName} Bank Debit",
            accountType = AccountType.BANK,
            finish = MetalCardFinish.CYBER_OBSIDIAN,
            cardColorHex = "#1D4ED8",
            defaultLimitUsd = 2500.0,
            last4 = "6104"
        )
        base += CountryCardPreset(
            id = "platinum_card",
            pillLabel = "Platinum Card",
            cardTitle = "Platinum Reserve Card",
            accountType = AccountType.CREDIT_CARD,
            finish = MetalCardFinish.PLATINUM_SILVER,
            cardColorHex = "#475569",
            defaultLimitUsd = 7500.0,
            last4 = "7792"
        )
        base += CountryCardPreset(
            id = "forex_travel_card",
            pillLabel = "Forex Travel Card",
            cardTitle = "Forex Travel Card",
            accountType = AccountType.BANK,
            finish = MetalCardFinish.ELECTRIC_CYAN,
            cardColorHex = "#084C52",
            defaultLimitUsd = 3500.0,
            last4 = "2098"
        )
        base += CountryCardPreset(
            id = "prepaid_virtual",
            pillLabel = "Prepaid Virtual",
            cardTitle = "Prepaid Virtual Card",
            accountType = AccountType.CASH,
            finish = MetalCardFinish.ELECTRIC_CYAN,
            cardColorHex = "#0D9488",
            defaultLimitUsd = 1000.0,
            last4 = "1149"
        )
        base += CountryCardPreset(
            id = "business_corporate",
            pillLabel = "Business Corporate",
            cardTitle = "Business Corporate Card",
            accountType = AccountType.BANK,
            finish = MetalCardFinish.PLATINUM_SILVER,
            cardColorHex = "#334155",
            defaultLimitUsd = 15000.0,
            last4 = "9930"
        )

        return base
    }
}

data class AccountVaultTelemetry(
    val accountId: Long,
    val inflowUsd: Double,
    val outflowUsd: Double,
    val limitUsd: Double,
    val utilizationRatio: Float,
    val limitLabel: String,
    val statusBadge: String
)

object MetalCardVaultStore {
    private const val PREFS_NAME = "koshvanta_metal_vault_prefs"
    private const val KEY_FINISH_PREFIX = "finish_acc_"
    private const val KEY_CURRENCY_PREFIX = "currency_acc_"

    fun getFinish(context: Context, account: AccountEntity, index: Int): MetalCardFinish {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = runCatching { prefs.getString("$KEY_FINISH_PREFIX${account.id}", null) }.getOrNull()
        return if (saved != null) {
            MetalCardFinish.fromId(saved)
        } else {
            MetalCardFinish.defaultForAccount(account, index)
        }
    }

    fun saveFinish(context: Context, accountId: Long, finish: MetalCardFinish) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("$KEY_FINISH_PREFIX$accountId", finish.id).commit()
    }

    fun getCardCurrencyOverride(context: Context, accountId: Long): CurrencyCode? {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = runCatching { prefs.getString("$KEY_CURRENCY_PREFIX$accountId", null) }.getOrNull()
        return code?.let { c -> CurrencyCode.entries.firstOrNull { it.code == c } }
    }

    fun saveCardCurrencyOverride(context: Context, accountId: Long, currency: CurrencyCode?) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            if (currency == null) {
                remove("$KEY_CURRENCY_PREFIX$accountId")
            } else {
                putString("$KEY_CURRENCY_PREFIX$accountId", currency.code)
            }
        }.commit()
    }
}

object MetalCardVaultCalculator {

    fun filterTransactionsForAccount(
        accountId: Long,
        transactions: List<TransactionWithDetails>
    ): List<TransactionWithDetails> = transactions.filter { item ->
        item.transaction.accountId == accountId || item.transaction.toAccountId == accountId
    }

    fun computeAccountTelemetry(
        account: AccountEntity,
        accountTransactions: List<TransactionWithDetails>
    ): AccountVaultTelemetry {
        val inflowUsd = accountTransactions.sumOf { item ->
            val tx = item.transaction
            when {
                tx.type == TransactionType.INCOME && tx.accountId == account.id -> tx.amount
                tx.type == TransactionType.TRANSFER && tx.toAccountId == account.id -> tx.amount
                else -> 0.0
            }
        }
        val outflowUsd = accountTransactions.sumOf { item ->
            val tx = item.transaction
            when {
                tx.type == TransactionType.EXPENSE && tx.accountId == account.id -> tx.amount
                tx.type == TransactionType.TRANSFER && tx.accountId == account.id -> tx.amount
                else -> 0.0
            }
        }

        val hasExplicitLimit = account.creditLimit > 0.0
        val effectiveLimitUsd = if (hasExplicitLimit) {
            account.creditLimit
        } else {
            maxOf(account.balance.coerceAtLeast(0.0) + outflowUsd, 500.0)
        }
        val ratio = (outflowUsd / effectiveLimitUsd).toFloat().coerceIn(0f, 1f)
        val limitLabel = when {
            account.type == AccountType.CREDIT_CARD && hasExplicitLimit -> "Credit Limit Pulse"
            hasExplicitLimit -> "Monthly Limit"
            else -> "Spend Capacity"
        }
        val statusBadge = when {
            account.isDefault -> "PRIMARY VAULT"
            CategoryIconHelper.isUpiWallet(account) || account.name.contains("UPI", ignoreCase = true) -> "DIGITAL RAIL"
            account.type == AccountType.CREDIT_CARD -> "METAL CREDIT"
            account.name.contains("Forex", ignoreCase = true) -> "GLOBAL FOREX"
            else -> "${account.type.name} LEDGER"
        }

        return AccountVaultTelemetry(
            accountId = account.id,
            inflowUsd = inflowUsd,
            outflowUsd = outflowUsd,
            limitUsd = effectiveLimitUsd,
            utilizationRatio = ratio,
            limitLabel = limitLabel,
            statusBadge = statusBadge
        )
    }

    fun computeTiltAngles(
        touchX: Float,
        touchY: Float,
        width: Float,
        height: Float
    ): Pair<Float, Float> {
        if (width <= 1f || height <= 1f) return 0f to 0f
        val normX = ((touchX / width) - 0.5f).coerceIn(-0.5f, 0.5f) * 2f
        val normY = ((touchY / height) - 0.5f).coerceIn(-0.5f, 0.5f) * 2f
        val rotY = (normX * 14f).coerceIn(-14f, 14f)
        val rotX = (-normY * 10f).coerceIn(-10f, 10f)
        return rotX to rotY
    }

    fun formatUsdInCurrency(usdAmount: Double, currency: CurrencyCode, customSymbol: String = ""): String {
        val converted = usdAmount * currency.exchangeRateToUsd
        val sym = customSymbol.ifBlank { currency.symbol }
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "$sym${formatter.format(converted)}"
    }
}

@Composable
fun MetalCardVaultSection(
    accounts: List<AccountEntity>,
    pagerState: PagerState,
    currentCurrency: CurrencyCode,
    customCurrencySymbol: String,
    allTransactions: List<TransactionWithDetails>,
    accountFinishes: Map<Long, MetalCardFinish>,
    accountCurrencies: Map<Long, CurrencyCode?>,
    addedBannerMessage: String?,
    onSelectFinish: (Long, MetalCardFinish) -> Unit,
    onQuickAddPreset: (CountryCardPreset) -> Unit,
    onOpenCountryPicker: () -> Unit,
    onOpenCustomCardDialog: () -> Unit,
    onOpenManageCardDialog: (AccountEntity) -> Unit,
    onDeleteAccountClick: (AccountEntity) -> Unit,
    onAutoPreloadCountryCards: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddPills = remember(currentCurrency) { CountryCardEngine.getQuickAddPills(currentCurrency) }
    val selectedAccount = accounts.getOrNull(pagerState.currentPage.coerceIn(0, (accounts.size - 1).coerceAtLeast(0)))
    val selectedFinish = if (selectedAccount != null) {
        accountFinishes[selectedAccount.id]
            ?: MetalCardFinish.defaultForAccount(selectedAccount, pagerState.currentPage.coerceAtLeast(0))
    } else {
        MetalCardFinish.CYBER_OBSIDIAN
    }
    val selectedAccent by animateColorAsState(
        targetValue = Color(selectedFinish.accentHex),
        animationSpec = tween(320),
        label = "vault_selected_accent"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("metal_card_vault_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Vault Header: Breathing Pulse + Country Selector Pill + Page Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                BreathingStatusDot(color = selectedAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3D CARD VAULT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFF0B0C10).copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.48f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .testTag("vault_country_picker_pill")
                        .clickable { onOpenCountryPicker() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentCurrency.flag} ${currentCurrency.code} (${customCurrencySymbol.ifBlank { currentCurrency.symbol }})",
                            color = Color(0xFF00FFCC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change Country & Currency",
                            tint = Color(0xFF00FFCC),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val totalSlots = accounts.size + 1
                for (idx in 0 until totalSlots) {
                    val active = idx == pagerState.currentPage
                    val isPlusSlot = idx == accounts.size
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (active) 16.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    active && isPlusSlot -> Color(0xFF00FFCC)
                                    active -> selectedAccent
                                    isPlusSlot -> Color(0xFF00FFCC).copy(alpha = 0.4f)
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                }
                            )
                    )
                }
            }
        }

        // Empty-state helper banner when user wiped all cards cleanly
        if (accounts.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0B0C10).copy(alpha = 0.92f),
                border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vault empty • Pre-load 3 ${currentCurrency.countryName} starter cards or mint below",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF00FFCC).copy(alpha = 0.18f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("vault_preload_starter_btn")
                            .clickable { onAutoPreloadCountryCards() }
                    ) {
                        Text(
                            text = "Pre-Load 3",
                            color = Color(0xFF00FFCC),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Snap-Scrolling HorizontalPager (User Cards + End-of-Scroll "+ Add Custom Card" Frosted Slot)
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 14.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("metal_card_pager")
        ) { page ->
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            val absOffset = abs(pageOffset).coerceIn(0f, 1f)
            val cardScale = 1f - (absOffset * 0.08f)
            val cardAlpha = 1f - (absOffset * 0.35f)
            val carouselTiltY = (pageOffset * -10f).coerceIn(-12f, 12f)

            if (page == accounts.size) {
                AddCustomMetalCardSlot(
                    currentCurrency = currentCurrency,
                    scale = cardScale,
                    alpha = cardAlpha,
                    onClick = onOpenCustomCardDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("metal_vault_end_add_card_slot")
                )
            } else {
                val account = accounts[page]
                val cardCurrency = accountCurrencies[account.id] ?: currentCurrency
                val customSym = if (cardCurrency == currentCurrency) customCurrencySymbol else ""
                val finish = accountFinishes[account.id]
                    ?: MetalCardFinish.defaultForAccount(account, page)
                val cardTxs = remember(account.id, allTransactions) {
                    MetalCardVaultCalculator.filterTransactionsForAccount(account.id, allTransactions)
                }
                val telemetry = remember(account, cardTxs) {
                    MetalCardVaultCalculator.computeAccountTelemetry(account, cardTxs)
                }

                FuturisticMetalCard(
                    account = account,
                    cardCurrency = cardCurrency,
                    finish = finish,
                    telemetry = telemetry,
                    formattedBalance = MetalCardVaultCalculator.formatUsdInCurrency(account.balance, cardCurrency, customSym),
                    formattedSpend = MetalCardVaultCalculator.formatUsdInCurrency(telemetry.outflowUsd, cardCurrency, customSym),
                    formattedLimit = MetalCardVaultCalculator.formatUsdInCurrency(telemetry.limitUsd, cardCurrency, customSym),
                    carouselScale = cardScale,
                    carouselAlpha = cardAlpha,
                    carouselTiltY = carouselTiltY,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("metal_card_item_${account.id}")
                )
            }
        }

        // Glowing Confirmation Banner ("✨ Card Added & Saved")
        AnimatedVisibility(
            visible = !addedBannerMessage.isNullOrBlank(),
            enter = expandVertically(tween(240)) + fadeIn(tween(220)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(180))
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF00FFCC).copy(alpha = 0.16f),
                border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_added_confirmation_banner")
            ) {
                Text(
                    text = addedBannerMessage.orEmpty(),
                    color = Color(0xFF00FFCC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // Country-Tailored Quick-Add Frosted Glass Pills ([Card Name +])
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${currentCurrency.flag} ${currentCurrency.countryName} Quick-Add Cards",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .testTag("country_quick_add_pills_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickAddPills.forEach { preset ->
                    val accent = Color(preset.finish.accentHex)
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFF0B0C10).copy(alpha = 0.86f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.48f)),
                        modifier = Modifier
                            .heightIn(min = 38.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .testTag("quick_add_pill_${preset.id}")
                            .clickable { onQuickAddPreset(preset) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${preset.pillLabel} +",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Directly Below Selected 3D Card: Odometer Balance, Monthly Limit Bar, Edit/Manage & 1-Tap Delete
        if (selectedAccount != null) {
            val activeCardCurrency = accountCurrencies[selectedAccount.id] ?: currentCurrency
            val activeSym = if (activeCardCurrency == currentCurrency) customCurrencySymbol else ""
            val activeTxs = remember(selectedAccount.id, allTransactions) {
                MetalCardVaultCalculator.filterTransactionsForAccount(selectedAccount.id, allTransactions)
            }
            val activeTelemetry = remember(selectedAccount, activeTxs) {
                MetalCardVaultCalculator.computeAccountTelemetry(selectedAccount, activeTxs)
            }
            val limitProgress by animateFloatAsState(
                targetValue = activeTelemetry.utilizationRatio,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
                label = "panel_limit_progress"
            )

            // 5 Switchable Luxury Metallic Finishes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetalCardFinish.entries.forEach { finishOption ->
                    val isSelected = selectedFinish == finishOption
                    val finishAccent = Color(finishOption.accentHex)
                    val shortFinishName = finishOption.label.substringAfter(" ")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(finishOption.primaryHex) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(
                            width = if (isSelected) 1.3.dp else 1.dp,
                            color = if (isSelected) finishAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)
                        ),
                        modifier = Modifier
                            .heightIn(min = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("finish_pill_${finishOption.id}")
                            .clickable { onSelectFinish(selectedAccount.id, finishOption) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(finishAccent)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = shortFinishName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) finishAccent else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selected_card_telemetry_panel_${selectedAccount.id}"),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = Color(0xFF0B0C10).copy(alpha = 0.95f),
                borderColor = selectedAccent.copy(alpha = 0.36f),
                borderWidth = 1.dp,
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedAccount.name} • ${activeCardCurrency.flag} ${activeCardCurrency.code}",
                                color = Color.White.copy(alpha = 0.72f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            OdometerBalanceText(
                                text = MetalCardVaultCalculator.formatUsdInCurrency(selectedAccount.balance, activeCardCurrency, activeSym),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = selectedAccent.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, selectedAccent.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .heightIn(min = 38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .testTag("vault_edit_manage_button")
                                    .clickable { onOpenManageCardDialog(selectedAccount) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit or Manage Card", tint = selectedAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit / Manage", color = selectedAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFF3366).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFFFF3366).copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .heightIn(min = 38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .testTag("vault_delete_selected_card")
                                    .clickable { onDeleteAccountClick(selectedAccount) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = Color(0xFFFF3366), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Delete", color = Color(0xFFFF3366), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Monthly Limit Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = activeTelemetry.limitLabel,
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${MetalCardVaultCalculator.formatUsdInCurrency(activeTelemetry.outflowUsd, activeCardCurrency, activeSym)} / ${MetalCardVaultCalculator.formatUsdInCurrency(activeTelemetry.limitUsd, activeCardCurrency, activeSym)}",
                                color = selectedAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(limitProgress.coerceAtLeast(0.03f))
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(selectedAccent.copy(alpha = 0.7f), selectedAccent, Color.White)
                                        )
                                    )
                            )
                        }
                    }

                    // Inflow / Outflow / Activity Telemetry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Inflow: +${MetalCardVaultCalculator.formatUsdInCurrency(activeTelemetry.inflowUsd, activeCardCurrency, activeSym)}",
                            color = Color(0xFF00FFCC),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Outflow: -${MetalCardVaultCalculator.formatUsdInCurrency(activeTelemetry.outflowUsd, activeCardCurrency, activeSym)}",
                            color = Color(0xFFFF3366),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeTxs.size} logs",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomMetalCardSlot(
    currentCurrency: CurrencyCode,
    scale: Float,
    alpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cyan = Color(0xFF00FFCC)
    Box(
        modifier = modifier
            .height(216.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0B0C10).copy(alpha = 0.94f), Color(0xFF0F2027).copy(alpha = 0.92f))
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(cyan, cyan.copy(alpha = 0.35f), Color.White.copy(alpha = 0.4f))),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = cyan.copy(alpha = 0.16f),
                border = BorderStroke(1.5.dp, cyan),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Card",
                        tint = cyan,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Text(
                text = "+ Add Custom Card",
                color = cyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Custom bank name, ${currentCurrency.code} / any currency & monthly spending limit",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun CountryCurrencyPickerDialog(
    currentCurrency: CurrencyCode,
    onSelect: (CurrencyCode) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) {
            CurrencyCode.entries
        } else {
            val q = query.trim()
            CurrencyCode.entries.filter {
                it.countryName.contains(q, ignoreCase = true) ||
                    it.code.contains(q, ignoreCase = true) ||
                    it.displayName.contains(q, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF00FFCC), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Country & Currency", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search India, USA, UK, UAE, Europe, Japan...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.code }) { code ->
                        val selected = code == currentCurrency
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFF00FFCC).copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.dp,
                                if (selected) Color(0xFF00FFCC) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("country_option_${code.code}")
                                .clickable {
                                    onSelect(code)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${code.flag}  ${code.countryName} (${code.code})",
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = code.symbol,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selected) Color(0xFF00FFCC) else MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddCustomMetalCardDialog(
    defaultCurrency: CurrencyCode,
    onCreateCard: (
        name: String,
        type: AccountType,
        currency: CurrencyCode,
        balanceInSelectedCurrency: Double,
        monthlyLimitInSelectedCurrency: Double,
        last4: String,
        finish: MetalCardFinish
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK) }
    var selectedCurrency by remember { mutableStateOf(defaultCurrency) }
    var balanceText by remember { mutableStateOf("1000") }
    var limitText by remember { mutableStateOf("5000") }
    var last4 by remember { mutableStateOf("") }
    var selectedFinish by remember { mutableStateOf(MetalCardFinish.CYBER_OBSIDIAN) }

    val quickCurrencies = remember(defaultCurrency) {
        (listOf(defaultCurrency, CurrencyCode.INR, CurrencyCode.USD, CurrencyCode.GBP, CurrencyCode.EUR, CurrencyCode.AED, CurrencyCode.JPY, CurrencyCode.SGD, CurrencyCode.CAD, CurrencyCode.AUD, CurrencyCode.BRL, CurrencyCode.SAR))
            .distinct()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mint Custom 3D Metal Card", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bank / Card Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_card_name_input")
                )

                Text("Card Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AccountType.entries.forEach { t ->
                        val sel = t == selectedType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(0xFF00FFCC).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedType = t }
                        ) {
                            Text(
                                text = t.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text("Card Currency", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickCurrencies.forEach { c ->
                        val sel = c == selectedCurrency
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(0xFF00FFCC).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCurrency = c }
                        ) {
                            Text(
                                text = "${c.flag} ${c.code} (${c.symbol})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Starting Balance (${selectedCurrency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Spending Limit (${selectedCurrency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_card_limit_input")
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) last4 = it },
                    label = { Text("Last 4 Digits (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Luxury Metallic Finish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MetalCardFinish.entries.forEach { f ->
                        val sel = f == selectedFinish
                        val accent = Color(f.accentHex)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(f.primaryHex) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (sel) accent else Color.Transparent),
                            modifier = Modifier.clickable { selectedFinish = f }
                        ) {
                            Text(
                                text = f.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sel) accent else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanName = name.trim().ifEmpty { "${selectedCurrency.countryName} Custom Card" }
                    val bal = balanceText.toDoubleOrNull() ?: 0.0
                    val lim = (limitText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                    onCreateCard(cleanName, selectedType, selectedCurrency, bal, lim, last4, selectedFinish)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFCC),
                    contentColor = Color(0xFF0B0C10)
                ),
                modifier = Modifier.testTag("confirm_mint_custom_card")
            ) {
                Text("Mint Card", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditManageMetalCardDialog(
    account: AccountEntity,
    currentFinish: MetalCardFinish,
    currentCardCurrency: CurrencyCode,
    onSave: (
        account: AccountEntity,
        newName: String,
        newType: AccountType,
        newCurrency: CurrencyCode,
        newBalanceInCurrency: Double,
        newMonthlyLimitInCurrency: Double,
        newLast4: String,
        newFinish: MetalCardFinish,
        setPrimary: Boolean
    ) -> Unit,
    onDelete: (AccountEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(account.name) }
    var selectedType by remember { mutableStateOf(account.type) }
    var selectedCurrency by remember { mutableStateOf(currentCardCurrency) }
    var balanceText by remember {
        mutableStateOf(String.format(Locale.US, "%.2f", account.balance * currentCardCurrency.exchangeRateToUsd))
    }
    var limitText by remember {
        val defaultLim = if (account.creditLimit > 0) account.creditLimit else 2500.0
        mutableStateOf(String.format(Locale.US, "%.2f", defaultLim * currentCardCurrency.exchangeRateToUsd))
    }
    var last4 by remember { mutableStateOf(account.accountNumberLast4) }
    var selectedFinish by remember { mutableStateOf(currentFinish) }
    var setAsPrimary by remember { mutableStateOf(account.isDefault) }

    val quickCurrencies = remember(currentCardCurrency) {
        (listOf(currentCardCurrency, CurrencyCode.INR, CurrencyCode.USD, CurrencyCode.GBP, CurrencyCode.EUR, CurrencyCode.AED, CurrencyCode.JPY, CurrencyCode.SGD, CurrencyCode.CAD, CurrencyCode.AUD, CurrencyCode.BRL))
            .distinct()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit / Manage Card", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Card Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AccountType.entries.forEach { t ->
                        val sel = t == selectedType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(0xFF00FFCC).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedType = t }
                        ) {
                            Text(t.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                        }
                    }
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickCurrencies.forEach { c ->
                        val sel = c == selectedCurrency
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(0xFF00FFCC).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCurrency = c }
                        ) {
                            Text("${c.flag} ${c.code}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                        }
                    }
                }
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Balance (${selectedCurrency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Limit (${selectedCurrency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) last4 = it },
                    label = { Text("Last 4 Digits") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MetalCardFinish.entries.forEach { f ->
                        val sel = f == selectedFinish
                        val accent = Color(f.accentHex)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Color(f.primaryHex) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (sel) accent else Color.Transparent),
                            modifier = Modifier.clickable { selectedFinish = f }
                        ) {
                            Text(
                                text = f.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sel) accent else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                OutlinedButton(
                    onClick = { setAsPrimary = !setAsPrimary },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (setAsPrimary) "★ Primary Card Enabled" else "Set as Primary Card",
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = {
                        onDelete(account)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete This Card (Undo Available)", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanName = name.trim().ifEmpty { account.name }
                    val bal = balanceText.toDoubleOrNull() ?: (account.balance * selectedCurrency.exchangeRateToUsd)
                    val lim = (limitText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                    onSave(account, cleanName, selectedType, selectedCurrency, bal, lim, last4, selectedFinish, setAsPrimary)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC), contentColor = Color(0xFF0B0C10))
            ) {
                Text("Save Changes", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FuturisticMetalCard(
    account: AccountEntity,
    cardCurrency: CurrencyCode,
    finish: MetalCardFinish,
    telemetry: AccountVaultTelemetry,
    formattedBalance: String,
    formattedSpend: String,
    formattedLimit: String,
    carouselScale: Float,
    carouselAlpha: Float,
    carouselTiltY: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    var cardSize by remember { mutableStateOf(IntSize(1, 1)) }
    var targetTiltX by remember { mutableFloatStateOf(0f) }
    var targetTiltY by remember { mutableFloatStateOf(0f) }

    val animatedTiltX by animateFloatAsState(
        targetValue = targetTiltX,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "card_tilt_x"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = (targetTiltY + carouselTiltY).coerceIn(-18f, 18f),
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "card_tilt_y"
    )

    val primaryColor by animateColorAsState(Color(finish.primaryHex), tween(360), label = "finish_p")
    val secondaryColor by animateColorAsState(Color(finish.secondaryHex), tween(360), label = "finish_s")
    val accentColor by animateColorAsState(Color(finish.accentHex), tween(360), label = "finish_a")
    val chipPrimary by animateColorAsState(Color(finish.chipPrimaryHex), tween(360), label = "chip_p")
    val chipSecondary by animateColorAsState(Color(finish.chipSecondaryHex), tween(360), label = "chip_s")
    val textPrimary by animateColorAsState(Color(finish.textPrimaryHex), tween(360), label = "text_p")

    val infiniteTransition = rememberInfiniteTransition(label = "metal_card_fx")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -0.55f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser_shimmer"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_pulse"
    )
    val animatedUtilization by animateFloatAsState(
        targetValue = telemetry.utilizationRatio,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
        label = "limit_utilization"
    )

    Box(
        modifier = modifier
            .height(216.dp)
            .onSizeChanged { cardSize = it }
            .graphicsLayer {
                scaleX = carouselScale
                scaleY = carouselScale
                alpha = carouselAlpha
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                cameraDistance = 14f * density
            }
            .pointerInput(account.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val pointer = event.changes.firstOrNull()
                        if (pointer != null && pointer.pressed) {
                            val (rx, ry) = MetalCardVaultCalculator.computeTiltAngles(
                                touchX = pointer.position.x,
                                touchY = pointer.position.y,
                                width = cardSize.width.toFloat(),
                                height = cardSize.height.toFloat()
                            )
                            targetTiltX = rx
                            targetTiltY = ry
                        } else {
                            targetTiltX = 0f
                            targetTiltY = 0f
                        }
                    }
                }
            }
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.28f),
                        accentColor.copy(alpha = 0.45f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor, primaryColor),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            )

            val lineStep = h / 18f
            for (i in 1..17) {
                val y = i * lineStep
                drawLine(
                    color = Color.White.copy(alpha = if (i % 3 == 0) 0.035f else 0.015f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
            }

            drawCircle(
                color = accentColor.copy(alpha = 0.09f),
                radius = h * 0.62f,
                center = Offset(w * 0.92f, h * 0.14f),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.05f),
                radius = h * 0.88f,
                center = Offset(w * 0.92f, h * 0.14f),
                style = Stroke(width = 1.5f)
            )

            val sweepStartX = (shimmerProgress * (w + h)) - h * 0.5f
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.07f),
                        accentColor.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    start = Offset(sweepStartX, 0f),
                    end = Offset(sweepStartX + w * 0.42f, h)
                )
            )

            val chipLeft = 22.dp.toPx()
            val chipTop = 58.dp.toPx()
            val chipW = 42.dp.toPx()
            val chipH = 30.dp.toPx()
            drawHolographicEmvChip(
                topLeft = Offset(chipLeft, chipTop),
                chipSize = Size(chipW, chipH),
                primary = chipPrimary,
                secondary = chipSecondary,
                holoAccent = accentColor,
                shimmerPhase = shimmerProgress
            )

            val nfcCenter = Offset(chipLeft + chipW + 16.dp.toPx(), chipTop + chipH * 0.5f)
            drawContactlessWaves(
                origin = nfcCenter,
                color = accentColor.copy(alpha = 0.85f)
            )

            val arcDiameter = 46.dp.toPx()
            val arcTopLeft = Offset(w - arcDiameter - 20.dp.toPx(), 18.dp.toPx())
            drawArc(
                color = Color.White.copy(alpha = 0.14f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = Size(arcDiameter, arcDiameter),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(accentColor, Color.White, accentColor),
                    center = Offset(
                        arcTopLeft.x + arcDiameter / 2f,
                        arcTopLeft.y + arcDiameter / 2f
                    )
                ),
                startAngle = 135f,
                sweepAngle = 270f * animatedUtilization.coerceAtLeast(0.06f),
                useCenter = false,
                topLeft = arcTopLeft,
                size = Size(arcDiameter, arcDiameter),
                style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = accentColor.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = telemetry.statusBadge,
                                color = accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (account.isDefault) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Primary Account",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(animatedUtilization * 100).toInt()}%",
                        color = textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = account.name,
                        color = textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = CountryCardEngine.regionRailSubtitle(cardCurrency, account),
                        color = textPrimary.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "LIVE VAULT BALANCE",
                            color = textPrimary.copy(alpha = 0.58f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                        OdometerBalanceText(
                            text = formattedBalance,
                            color = textPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = telemetry.limitLabel.uppercase(),
                            color = textPrimary.copy(alpha = 0.58f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "$formattedSpend / $formattedLimit",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedUtilization.coerceAtLeast(0.04f))
                            .height(5.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.7f),
                                        accentColor,
                                        Color.White
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun OdometerBalanceText(
    text: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        text.forEachIndexed { index, ch ->
            AnimatedContent(
                targetState = ch,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(220)) { it / 2 } + fadeIn(tween(180)))
                        .togetherWith(slideOutVertically(animationSpec = tween(200)) { -it / 2 } + fadeOut(tween(150)))
                },
                label = "odometer_char_$index"
            ) { targetChar ->
                Text(
                    text = targetChar.toString(),
                    color = color,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun BreathingStatusDot(color: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "breathing_dot")
    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(modifier = modifier.size(14.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .clip(CircleShape)
                .background(color.copy(alpha = glowAlpha))
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

private fun DrawScope.drawHolographicEmvChip(
    topLeft: Offset,
    chipSize: Size,
    primary: Color,
    secondary: Color,
    holoAccent: Color,
    shimmerPhase: Float
) {
    val radius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(primary, secondary, holoAccent.copy(alpha = 0.55f), primary),
            start = topLeft,
            end = Offset(
                topLeft.x + chipSize.width * (0.6f + shimmerPhase * 0.4f),
                topLeft.y + chipSize.height
            )
        ),
        topLeft = topLeft,
        size = chipSize,
        cornerRadius = radius
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.35f),
        topLeft = topLeft,
        size = chipSize,
        cornerRadius = radius,
        style = Stroke(width = 1.dp.toPx())
    )

    val lineColor = Color(0xFF0B0C10).copy(alpha = 0.48f)
    val left = topLeft.x
    val top = topLeft.y
    val w = chipSize.width
    val h = chipSize.height

    drawLine(lineColor, Offset(left + w * 0.33f, top), Offset(left + w * 0.33f, top + h), strokeWidth = 1.dp.toPx())
    drawLine(lineColor, Offset(left + w * 0.67f, top), Offset(left + w * 0.67f, top + h), strokeWidth = 1.dp.toPx())
    drawLine(lineColor, Offset(left, top + h * 0.34f), Offset(left + w * 0.33f, top + h * 0.34f), strokeWidth = 1.dp.toPx())
    drawLine(lineColor, Offset(left, top + h * 0.66f), Offset(left + w * 0.33f, top + h * 0.66f), strokeWidth = 1.dp.toPx())
    drawLine(lineColor, Offset(left + w * 0.67f, top + h * 0.34f), Offset(left + w, top + h * 0.34f), strokeWidth = 1.dp.toPx())
    drawLine(lineColor, Offset(left + w * 0.67f, top + h * 0.66f), Offset(left + w, top + h * 0.66f), strokeWidth = 1.dp.toPx())
    drawRoundRect(
        color = lineColor,
        topLeft = Offset(left + w * 0.36f, top + h * 0.24f),
        size = Size(w * 0.28f, h * 0.52f),
        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
}

@Composable
fun Mini3DNeonMetalCard(
    account: AccountEntity,
    cardCurrency: CurrencyCode,
    finish: MetalCardFinish,
    formattedBalance: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    var cardSize by remember { mutableStateOf(IntSize(1, 1)) }
    var targetTiltX by remember { mutableFloatStateOf(0f) }
    var targetTiltY by remember { mutableFloatStateOf(0f) }
    var isPressed by remember { mutableStateOf(false) }

    val animatedTiltX by animateFloatAsState(
        targetValue = targetTiltX,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMediumLow),
        label = "mini_card_tilt_x"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = targetTiltY,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMediumLow),
        label = "mini_card_tilt_y"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "mini_card_press_scale"
    )

    val primaryColor by animateColorAsState(Color(finish.primaryHex), tween(320), label = "mini_p")
    val secondaryColor by animateColorAsState(Color(finish.secondaryHex), tween(320), label = "mini_s")
    val accentColor by animateColorAsState(Color(finish.accentHex), tween(320), label = "mini_a")
    val chipPrimary by animateColorAsState(Color(finish.chipPrimaryHex), tween(320), label = "mini_cp")
    val chipSecondary by animateColorAsState(Color(finish.chipSecondaryHex), tween(320), label = "mini_cs")
    val textPrimary by animateColorAsState(Color(finish.textPrimaryHex), tween(320), label = "mini_tp")

    val infiniteTransition = rememberInfiniteTransition(label = "mini_metal_card_fx")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -0.55f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mini_shimmer"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_pulse"
    )

    val shortBadge = remember(account) {
        when {
            account.isDefault -> "PRIMARY"
            CategoryIconHelper.isUpiWallet(account) || account.name.contains("UPI", ignoreCase = true) -> "DIGITAL"
            account.type == AccountType.CREDIT_CARD -> "CREDIT"
            else -> account.type.name
        }
    }
    val subTag = remember(account, cardCurrency) {
        if (account.accountNumberLast4.isNotEmpty()) {
            "•••• ${account.accountNumberLast4}"
        } else {
            "${cardCurrency.flag} ${cardCurrency.code}"
        }
    }

    Box(
        modifier = modifier
            .width(198.dp)
            .height(126.dp)
            .onSizeChanged { cardSize = it }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                cameraDistance = 12f * density
            }
            .pointerInput(account.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val pointer = event.changes.firstOrNull()
                        if (pointer != null && pointer.pressed) {
                            isPressed = true
                            val (rx, ry) = MetalCardVaultCalculator.computeTiltAngles(
                                touchX = pointer.position.x,
                                touchY = pointer.position.y,
                                width = cardSize.width.toFloat(),
                                height = cardSize.height.toFloat()
                            )
                            targetTiltX = (rx * 0.75f).coerceIn(-8f, 8f)
                            targetTiltY = (ry * 0.75f).coerceIn(-10f, 10f)
                        } else {
                            isPressed = false
                            targetTiltX = 0f
                            targetTiltY = 0f
                        }
                    }
                }
            }
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.25f),
                        accentColor.copy(alpha = 0.45f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .testTag("dashboard_mini_metal_card_${account.id}")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor, primaryColor),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            )

            val lineStep = h / 12f
            for (i in 1..11) {
                val y = i * lineStep
                drawLine(
                    color = Color.White.copy(alpha = if (i % 3 == 0) 0.04f else 0.015f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
            }

            drawCircle(
                color = accentColor.copy(alpha = 0.1f),
                radius = h * 0.58f,
                center = Offset(w * 0.92f, h * 0.16f),
                style = Stroke(width = 1.3f)
            )

            val sweepStartX = (shimmerProgress * (w + h)) - h * 0.5f
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.06f),
                        accentColor.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    start = Offset(sweepStartX, 0f),
                    end = Offset(sweepStartX + w * 0.42f, h)
                )
            )

            val chipLeft = 14.dp.toPx()
            val chipTop = 40.dp.toPx()
            val chipW = 28.dp.toPx()
            val chipH = 20.dp.toPx()
            drawHolographicEmvChip(
                topLeft = Offset(chipLeft, chipTop),
                chipSize = Size(chipW, chipH),
                primary = chipPrimary,
                secondary = chipSecondary,
                holoAccent = accentColor,
                shimmerPhase = shimmerProgress
            )

            val nfcCenter = Offset(chipLeft + chipW + 10.dp.toPx(), chipTop + chipH * 0.5f)
            drawContactlessWaves(
                origin = nfcCenter,
                color = accentColor.copy(alpha = 0.85f),
                waveRadiiDp = listOf(3.8f, 6.2f, 8.6f, 11f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 13.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.48f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = shortBadge,
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (account.isDefault) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Primary Card",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = subTag,
                        color = textPrimary.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = account.name,
                    color = textPrimary.copy(alpha = 0.88f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                OdometerBalanceText(
                    text = formattedBalance,
                    color = textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

private fun DrawScope.drawContactlessWaves(
    origin: Offset,
    color: Color,
    waveRadiiDp: List<Float> = listOf(6f, 10f, 14f, 18f)
) {
    rotate(degrees = 0f, pivot = origin) {
        val radii = waveRadiiDp.map { it.dp.toPx() }
        radii.forEachIndexed { idx, r ->
            drawArc(
                color = color.copy(alpha = (0.9f - idx * 0.15f).coerceIn(0.35f, 0.95f)),
                startAngle = -42f,
                sweepAngle = 84f,
                useCenter = false,
                topLeft = Offset(origin.x - r, origin.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
