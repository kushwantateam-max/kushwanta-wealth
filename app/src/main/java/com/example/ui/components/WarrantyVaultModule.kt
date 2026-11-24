package com.example.ui.components

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.domain.util.NotificationHelper
import com.example.domain.util.SoundEffectManager
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateRedNeon
import com.example.ui.theme.StateYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

// -----------------------------------------------------------------------------
// Data Models & Pure Expiry / 15-Day Alert Calculations
// -----------------------------------------------------------------------------

enum class WarrantySeverity {
    PROTECTED,
    CAUTION,
    ALERT_15_DAY,
    EXPIRED
}

data class WarrantyItem(
    val id: String = UUID.randomUUID().toString(),
    val productName: String,
    val purchaseDateMillis: Long,
    val warrantyMonths: Int,
    val expiryDateMillis: Long,
    val receiptImagePath: String? = null,
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class WarrantyCountdownInfo(
    val daysRemaining: Long,
    val severity: WarrantySeverity,
    val badgeLabel: String,
    val accentColorHex: Long
)

object WarrantyCalculator {

    private const val DAY_MS = 24L * 3600_000L
    private const val ALERT_DAYS_BEFORE_EXPIRY = 15L

    fun computeExpiryDateMillis(purchaseDateMillis: Long, warrantyMonths: Int): Long {
        val safeMonths = warrantyMonths.coerceIn(1, 240)
        val cal = Calendar.getInstance().apply {
            timeInMillis = purchaseDateMillis
            add(Calendar.MONTH, safeMonths)
        }
        return cal.timeInMillis
    }

    private fun startOfDayMillis(timeMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun computeCountdown(
        expiryDateMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): WarrantyCountdownInfo {
        val todayStart = startOfDayMillis(nowMillis)
        val expiryStart = startOfDayMillis(expiryDateMillis)
        val daysRemaining = (expiryStart - todayStart) / DAY_MS

        return when {
            daysRemaining < 0L -> WarrantyCountdownInfo(
                daysRemaining = daysRemaining,
                severity = WarrantySeverity.EXPIRED,
                badgeLabel = "EXPIRED ${-daysRemaining}D AGO",
                accentColorHex = 0xFFFF3366
            )
            daysRemaining == 0L -> WarrantyCountdownInfo(
                daysRemaining = 0L,
                severity = WarrantySeverity.EXPIRED,
                badgeLabel = "EXPIRES TODAY",
                accentColorHex = 0xFFFF3366
            )
            daysRemaining <= ALERT_DAYS_BEFORE_EXPIRY -> WarrantyCountdownInfo(
                daysRemaining = daysRemaining,
                severity = WarrantySeverity.ALERT_15_DAY,
                badgeLabel = "${daysRemaining}D LEFT • 15D ALERT",
                accentColorHex = 0xFFFF3366
            )
            daysRemaining <= 45L -> WarrantyCountdownInfo(
                daysRemaining = daysRemaining,
                severity = WarrantySeverity.CAUTION,
                badgeLabel = "${daysRemaining}D LEFT",
                accentColorHex = 0xFFF59E0B
            )
            else -> WarrantyCountdownInfo(
                daysRemaining = daysRemaining,
                severity = WarrantySeverity.PROTECTED,
                badgeLabel = "${daysRemaining}D LEFT",
                accentColorHex = 0xFF00FFCC
            )
        }
    }

    /**
     * Returns initial delay in milliseconds for WorkManager to fire exactly 15 days before expiry.
     * If expiry is already within 1..15 days, schedules a near-immediate reminder (5 seconds).
     * Returns null if warranty has already expired.
     */
    fun compute15DayAlertDelayMillis(
        expiryDateMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Long? {
        if (expiryDateMillis <= nowMillis) return null
        val triggerAtMillis = expiryDateMillis - (ALERT_DAYS_BEFORE_EXPIRY * DAY_MS)
        return if (triggerAtMillis > nowMillis) {
            triggerAtMillis - nowMillis
        } else {
            5_000L
        }
    }

    fun formatDate(timeMillis: Long): String {
        val df = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return df.format(Date(timeMillis))
    }

    fun formatDurationLabel(months: Int): String {
        return when {
            months < 12 -> "$months mo"
            months % 12 == 0 -> "${months / 12} yr"
            else -> "$months mo"
        }
    }
}

// -----------------------------------------------------------------------------
// WorkManager 15-Day Expiry Notification Worker & Scheduler
// -----------------------------------------------------------------------------

class WarrantyAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val warrantyId = inputData.getString(KEY_WARRANTY_ID).orEmpty()
            val productName = inputData.getString(KEY_PRODUCT_NAME).orEmpty().ifBlank { "Saved Product" }
            val expiryFormatted = inputData.getString(KEY_EXPIRY_DATE).orEmpty().ifBlank { "soon" }

            NotificationHelper.showNotification(
                context = applicationContext,
                title = "15-Day Warranty Alert: $productName",
                message = "Warranty expires on $expiryFormatted (15 days remaining). Check receipt & claim service before coverage ends.",
                isHighPriority = true,
                notificationId = (warrantyId.hashCode() and 0x7FFFFFFF).coerceAtLeast(1000),
                playInAppChime = true
            )
            Result.success()
        } catch (_: Throwable) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_WARRANTY_ID = "warranty_id"
        const val KEY_PRODUCT_NAME = "product_name"
        const val KEY_EXPIRY_DATE = "expiry_date"
    }
}

object WarrantyAlertScheduler {

    private fun uniqueWorkName(warrantyId: String): String = "warranty_alert_$warrantyId"

    fun schedule15DayAlert(context: Context, item: WarrantyItem, nowMillis: Long = System.currentTimeMillis()): Boolean {
        return try {
            val delayMs = WarrantyCalculator.compute15DayAlertDelayMillis(item.expiryDateMillis, nowMillis)
            if (delayMs == null) {
                cancelAlert(context, item.id)
                return false
            }

            val input = workDataOf(
                WarrantyAlertWorker.KEY_WARRANTY_ID to item.id,
                WarrantyAlertWorker.KEY_PRODUCT_NAME to item.productName,
                WarrantyAlertWorker.KEY_EXPIRY_DATE to WarrantyCalculator.formatDate(item.expiryDateMillis)
            )

            val request = OneTimeWorkRequestBuilder<WarrantyAlertWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(input)
                .addTag("warranty_15d_alert")
                .build()

            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(uniqueWorkName(item.id), ExistingWorkPolicy.REPLACE, request)
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun cancelAlert(context: Context, warrantyId: String) {
        try {
            WorkManager.getInstance(context.applicationContext)
                .cancelUniqueWork(uniqueWorkName(warrantyId))
        } catch (_: Throwable) {
        }
    }
}

// -----------------------------------------------------------------------------
// Local Offline Persistence & Photo Receipt Copy Store
// -----------------------------------------------------------------------------

object WarrantyVaultStore {

    private const val PREF_NAME = "koshvanta_warranty_vault_prefs"
    private const val KEY_WARRANTIES_JSON = "warranties_json"

    fun loadAll(context: Context): List<WarrantyItem> = runCatching {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_WARRANTIES_JSON, null).orEmpty()
        if (raw.isBlank()) return emptyList()

        val arr = JSONArray(raw)
        val result = ArrayList<WarrantyItem>(arr.length())
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val name = obj.optString("productName", "").trim()
            if (name.isEmpty()) continue
            result.add(
                WarrantyItem(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    productName = name,
                    purchaseDateMillis = obj.optLong("purchaseDateMillis", System.currentTimeMillis()),
                    warrantyMonths = obj.optInt("warrantyMonths", 12),
                    expiryDateMillis = obj.optLong("expiryDateMillis", System.currentTimeMillis()),
                    receiptImagePath = obj.optString("receiptImagePath", "").takeIf { it.isNotBlank() },
                    notes = obj.optString("notes", ""),
                    createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                )
            )
        }
        result.sortedBy { it.expiryDateMillis }
    }.getOrDefault(emptyList())

    fun saveAll(context: Context, items: List<WarrantyItem>) {
        runCatching {
            val arr = JSONArray()
            items.forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("productName", item.productName)
                    put("purchaseDateMillis", item.purchaseDateMillis)
                    put("warrantyMonths", item.warrantyMonths)
                    put("expiryDateMillis", item.expiryDateMillis)
                    put("receiptImagePath", item.receiptImagePath.orEmpty())
                    put("notes", item.notes)
                    put("createdAtMillis", item.createdAtMillis)
                }
                arr.put(obj)
            }
            val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_WARRANTIES_JSON, arr.toString()).apply()
        }
    }

    fun upsertItem(context: Context, item: WarrantyItem): List<WarrantyItem> {
        val existing = loadAll(context).filterNot { it.id == item.id }
        val updated = (existing + item).sortedBy { it.expiryDateMillis }
        saveAll(context, updated)
        return updated
    }

    fun deleteItem(context: Context, warrantyId: String): List<WarrantyItem> {
        val all = loadAll(context)
        val victim = all.firstOrNull { it.id == warrantyId }
        deleteReceiptFile(victim?.receiptImagePath)
        val remaining = all.filterNot { it.id == warrantyId }
        saveAll(context, remaining)
        return remaining
    }

    suspend fun copyReceiptImageLocally(
        context: Context,
        sourceUri: Uri,
        warrantyId: String
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.applicationContext.filesDir, "warranty_receipts")
            if (!dir.exists()) dir.mkdirs()
            val outFile = File(dir, "$warrantyId.jpg")
            context.applicationContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (outFile.exists() && outFile.length() > 0L) outFile.absolutePath else null
        }.getOrNull()
    }

    fun deleteReceiptFile(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching {
            val f = File(path)
            if (f.exists()) f.delete()
        }
    }
}

// -----------------------------------------------------------------------------
// 1. Exact-Size Dark Neon Glassmorphic Card for Tools Hub Grid
// -----------------------------------------------------------------------------

@Composable
fun WarrantyVaultHubCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val items = remember { mutableStateOf(WarrantyVaultStore.loadAll(context)) }
    val urgentCount = remember(items.value) {
        val now = System.currentTimeMillis()
        items.value.count {
            val sev = WarrantyCalculator.computeCountdown(it.expiryDateMillis, now).severity
            sev == WarrantySeverity.ALERT_15_DAY || sev == WarrantySeverity.EXPIRED
        }
    }
    val hasAlert = urgentCount > 0
    val highlightColor = if (hasAlert) StateRedNeon else NeonMint

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = DarkCanvasBg,
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(highlightColor.copy(alpha = 0.78f), NeonMint.copy(alpha = 0.35f))
            )
        ),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)
            .testTag("warranty_vault_hub_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkSurfaceElevated, DarkCanvasBg)
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(highlightColor.copy(alpha = 0.16f))
                        .border(1.dp, highlightColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Product Warranty & Guarantee Vault",
                        tint = highlightColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = highlightColor.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = when {
                            hasAlert -> "$urgentCount Alert"
                            items.value.isNotEmpty() -> "${items.value.size} Vault"
                            else -> "15D Alert"
                        },
                        color = highlightColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "Warranty Vault",
                    color = DarkTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (items.value.isEmpty()) {
                        "Receipts, expiry timer & 15-day alert"
                    } else {
                        val next = items.value.first()
                        val cd = WarrantyCalculator.computeCountdown(next.expiryDateMillis)
                        "${next.productName}: ${cd.badgeLabel}"
                    },
                    color = DarkTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Full Interactive Product Warranty & Guarantee Vault Tool Screen
// -----------------------------------------------------------------------------

@Composable
fun ToolWarrantyVault() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var warranties by remember { mutableStateOf(WarrantyVaultStore.loadAll(context)) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var productNameInput by remember { mutableStateOf("") }
    var purchaseDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedMonths by remember { mutableIntStateOf(12) }
    var customMonthsText by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var pendingReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }
    var statusBanner by remember { mutableStateOf<String?>(null) }
    var fullScreenReceiptPath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(60_000L)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingReceiptUri = uri
        }
    }

    val effectiveMonths = customMonthsText.toIntOrNull()?.takeIf { it in 1..240 } ?: selectedMonths
    val previewExpiryMillis = remember(purchaseDateMillis, effectiveMonths) {
        WarrantyCalculator.computeExpiryDateMillis(purchaseDateMillis, effectiveMonths)
    }
    val previewCountdown = remember(previewExpiryMillis, nowMillis) {
        WarrantyCalculator.computeCountdown(previewExpiryMillis, nowMillis)
    }

    val expiringSoonCount = warranties.count {
        val sev = WarrantyCalculator.computeCountdown(it.expiryDateMillis, nowMillis).severity
        sev == WarrantySeverity.ALERT_15_DAY || sev == WarrantySeverity.EXPIRED
    }

    val openDatePicker: () -> Unit = {
        val cal = Calendar.getInstance().apply { timeInMillis = purchaseDateMillis }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 12, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                purchaseDateMillis = picked.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("warranty_vault_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Dark Neon Overview Banner
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = DarkCanvasBg,
                border = BorderStroke(
                    1.2.dp,
                    if (expiringSoonCount > 0) StateRedNeon.copy(alpha = 0.7f) else NeonMint.copy(alpha = 0.55f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(DarkSurfaceElevated, DarkCanvasBg)))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "GUARANTEE & RECEIPT SHIELD",
                            color = NeonMint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${warranties.size} Protected Item(s)",
                            color = DarkTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (expiringSoonCount > 0) {
                                "$expiringSoonCount item(s) within 15-day alert or expired"
                            } else {
                                "WorkManager auto-notifies 15 days before expiry"
                            },
                            color = if (expiringSoonCount > 0) StateRedNeon else DarkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (expiringSoonCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background((if (expiringSoonCount > 0) StateRedNeon else NeonMint).copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Warranty Vault",
                            tint = if (expiringSoonCount > 0) StateRedNeon else NeonMint,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // 2. Add New Product Warranty Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = DarkCanvasBg,
                border = BorderStroke(1.1.dp, NeonMint.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Register Product Warranty",
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    OutlinedTextField(
                        value = productNameInput,
                        onValueChange = {
                            productNameInput = it
                            formError = null
                        },
                        label = { Text("Product / Appliance Name") },
                        placeholder = { Text("e.g., MacBook Pro, Sony TV, Refrigerator") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = NeonMint.copy(alpha = 0.35f),
                            focusedLabelColor = NeonMint,
                            unfocusedLabelColor = DarkTextSecondary,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary,
                            cursorColor = NeonMint
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("warranty_product_input")
                    )

                    // Purchase Date Selector Row + Quick Presets
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Purchase Date",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = openDatePicker,
                                border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("warranty_purchase_date_button")
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = NeonMint, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = WarrantyCalculator.formatDate(purchaseDateMillis),
                                    color = DarkTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Today" to 0,
                                "-3 Mo" to -3,
                                "-6 Mo" to -6,
                                "-11 Mo" to -11,
                                "-1 Yr" to -12
                            ).forEach { (label, offsetMonths) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.25f)),
                                    modifier = Modifier.clickable {
                                        val c = Calendar.getInstance()
                                        if (offsetMonths != 0) c.add(Calendar.MONTH, offsetMonths)
                                        purchaseDateMillis = c.timeInMillis
                                    }
                                ) {
                                    Text(
                                        text = label,
                                        color = DarkTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Warranty Duration Chips + Custom Months
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Warranty / Guarantee Duration",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(6 to "6 Mo", 12 to "1 Yr", 24 to "2 Yr", 36 to "3 Yr", 60 to "5 Yr").forEach { (months, label) ->
                                val selected = customMonthsText.isBlank() && selectedMonths == months
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        selectedMonths = months
                                        customMonthsText = ""
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonMint.copy(alpha = 0.18f),
                                        selectedLabelColor = NeonMint,
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = DarkTextSecondary
                                    ),
                                    modifier = Modifier.testTag("warranty_duration_${months}m")
                                )
                            }
                        }
                        OutlinedTextField(
                            value = customMonthsText,
                            onValueChange = { raw ->
                                if (raw.isEmpty() || (raw.all { it.isDigit() } && raw.length <= 3)) {
                                    customMonthsText = raw
                                }
                            },
                            label = { Text("Or Custom Months (1–240)") },
                            placeholder = { Text("Optional custom duration in months") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMint,
                                unfocusedBorderColor = DarkTextSecondary.copy(alpha = 0.35f),
                                focusedLabelColor = NeonMint,
                                unfocusedLabelColor = DarkTextSecondary,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Optional Notes & Receipt Photo Picker (Zero-Permission Photo Picker)
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Store / Serial Note (Optional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = DarkTextSecondary.copy(alpha = 0.35f),
                            focusedLabelColor = NeonMint,
                            unfocusedLabelColor = DarkTextSecondary,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("warranty_pick_receipt_button")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = NeonMint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (pendingReceiptUri != null) "Receipt Attached (Change)" else "Attach Receipt Photo",
                                color = DarkTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (pendingReceiptUri != null) {
                            IconButton(
                                onClick = { pendingReceiptUri = null },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove Receipt", tint = StateRedNeon)
                            }
                        }
                    }

                    // Live Expiry Preview Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, Color(previewCountdown.accentColorHex).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Expires: ${WarrantyCalculator.formatDate(previewExpiryMillis)}",
                                color = DarkTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = previewCountdown.badgeLabel,
                                color = Color(previewCountdown.accentColorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (formError != null) {
                        Text(
                            text = formError.orEmpty(),
                            color = StateRedNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (statusBanner != null) {
                        Text(
                            text = statusBanner.orEmpty(),
                            color = NeonMint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val cleanName = productNameInput.trim()
                            if (cleanName.isBlank()) {
                                SoundEffectManager.playErrorTone()
                                formError = "Enter a product name first."
                                return@Button
                            }
                            val newId = UUID.randomUUID().toString()
                            val chosenUri = pendingReceiptUri
                            scope.launch {
                                val localPhotoPath = if (chosenUri != null) {
                                    WarrantyVaultStore.copyReceiptImageLocally(context, chosenUri, newId)
                                } else null

                                val expiryMillis = WarrantyCalculator.computeExpiryDateMillis(purchaseDateMillis, effectiveMonths)
                                val newItem = WarrantyItem(
                                    id = newId,
                                    productName = cleanName,
                                    purchaseDateMillis = purchaseDateMillis,
                                    warrantyMonths = effectiveMonths,
                                    expiryDateMillis = expiryMillis,
                                    receiptImagePath = localPhotoPath,
                                    notes = notesInput.trim()
                                )
                                warranties = WarrantyVaultStore.upsertItem(context, newItem)
                                val armed = WarrantyAlertScheduler.schedule15DayAlert(context, newItem)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                SoundEffectManager.playSuccessChime()

                                productNameInput = ""
                                notesInput = ""
                                pendingReceiptUri = null
                                formError = null
                                statusBanner = if (armed) {
                                    "Saved '$cleanName' • 15-Day Expiry Alert armed via WorkManager!"
                                } else {
                                    "Saved '$cleanName' (already expired — no future alert needed)."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonMint,
                            contentColor = DarkCanvasBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("warranty_save_button")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Warranty & Arm 15-Day Alert",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 3. Saved Warranties Section Header
        item {
            Text(
                text = "Saved Product Warranties (${warranties.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (warranties.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkCanvasBg,
                    border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = NeonMint, modifier = Modifier.size(28.dp))
                        Text(
                            text = "No warranties saved yet",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Add electronics, appliances or vehicles above to store receipts and receive an automatic 15-day alert before expiry.",
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(warranties, key = { it.id }) { item ->
                val countdown = WarrantyCalculator.computeCountdown(item.expiryDateMillis, nowMillis)
                val badgeColor = Color(countdown.accentColorHex)

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkCanvasBg,
                    border = BorderStroke(1.1.dp, badgeColor.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("warranty_item_card_${item.id}")
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = item.productName,
                                    color = DarkTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Bought ${WarrantyCalculator.formatDate(item.purchaseDateMillis)} (${WarrantyCalculator.formatDurationLabel(item.warrantyMonths)})",
                                    color = DarkTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Expires ${WarrantyCalculator.formatDate(item.expiryDateMillis)}",
                                    color = DarkTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (item.notes.isNotBlank()) {
                                    Text(
                                        text = item.notes,
                                        color = DarkTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    WarrantyAlertScheduler.cancelAlert(context, item.id)
                                    warranties = WarrantyVaultStore.deleteItem(context, item.id)
                                    SoundEffectManager.playDeleteSound()
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Warranty", tint = StateRedNeon)
                            }
                        }

                        // Color-Coded Live Countdown Badge + Receipt Thumbnail / 15-Day Alert Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = badgeColor.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f)),
                                modifier = Modifier.testTag("warranty_countdown_badge_${item.id}")
                            ) {
                                Text(
                                    text = countdown.badgeLabel,
                                    color = badgeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            if (!item.receiptImagePath.isNullOrBlank()) {
                                ReceiptThumbnailPill(
                                    imagePath = item.receiptImagePath,
                                    onClick = { fullScreenReceiptPath = item.receiptImagePath }
                                )
                            } else if (countdown.severity != WarrantySeverity.EXPIRED) {
                                Text(
                                    text = "15d Alert Armed",
                                    color = NeonMint,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    if (fullScreenReceiptPath != null) {
        ReceiptPreviewDialog(
            imagePath = fullScreenReceiptPath!!,
            onDismiss = { fullScreenReceiptPath = null }
        )
    }
}

@Composable
private fun ReceiptThumbnailPill(
    imagePath: String,
    onClick: () -> Unit
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, imagePath) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
                BitmapFactory.decodeFile(imagePath, opts)
            }.getOrNull()
        }
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.45f)),
        modifier = Modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Receipt Photo Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = NeonMint, modifier = Modifier.size(16.dp))
            }
            Text("View Receipt", color = NeonMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReceiptPreviewDialog(
    imagePath: String,
    onDismiss: () -> Unit
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, imagePath) {
        value = withContext(Dispatchers.IO) {
            runCatching { BitmapFactory.decodeFile(imagePath) }.getOrNull()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkCanvasBg,
            border = BorderStroke(1.2.dp, NeonMint),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Warranty Receipt", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Receipt", tint = DarkTextPrimary)
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Warranty Receipt Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Text("Receipt preview currently unavailable.", color = DarkTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
