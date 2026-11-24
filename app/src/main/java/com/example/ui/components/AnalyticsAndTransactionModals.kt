package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// -------------------------------------------------------------
// 1. Transaction Detail / Edit / Split / Refund / Receipt Photo Dialog
// -------------------------------------------------------------
@Composable
fun TransactionDetailEditDialog(
    viewModel: KoshvantaViewModel,
    item: TransactionWithDetails,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit
) {
    val tx = item.transaction
    var amountText by remember {
        mutableStateOf(String.format(Locale.US, "%.2f", viewModel.fromUsd(tx.amount)))
    }
    var noteText by remember { mutableStateOf(tx.note) }
    var selectedCatId by remember { mutableStateOf(tx.categoryId) }
    var receiptUri by remember { mutableStateOf(tx.receiptUri) }
    var splitNote by remember { mutableStateOf(tx.splitDetails) }
    var showSplitEditor by remember { mutableStateOf(false) }
    val accent = LocalAccentColor.current

    // Zero-permission Android Photo Picker (Play Store Compliant!)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            receiptUri = uri.toString()
        }
    }

    val df = remember { SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Transaction Details", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                IconButton(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        onDismiss()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StateRed)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${tx.type.name} • ${item.account?.name ?: "Account"} • ${df.format(Date(tx.timestamp))}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note / Merchant") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.filter { it.isExpense == (tx.type == TransactionType.EXPENSE) }.forEach { cat ->
                        val sel = cat.id == selectedCatId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) accent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCatId = cat.id }
                        ) {
                            Text(
                                text = cat.name,
                                color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Receipt Photo Picker + Split + Refund actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (receiptUri != null) "Receipt ✓" else "Receipt", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showSplitEditor = !showSplitEditor },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.CallSplit, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Split", fontSize = 11.sp)
                    }
                }

                if (showSplitEditor || splitNote.isNotEmpty()) {
                    OutlinedTextField(
                        value = splitNote,
                        onValueChange = { splitNote = it },
                        label = { Text("Split Breakdown (e.g. 50% Food / 50% Groceries)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (tx.type == TransactionType.EXPENSE) {
                    OutlinedButton(
                        onClick = {
                            viewModel.markTransactionAsRefund(tx)
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StateGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark as Refund / Reversal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0.0) {
                        viewModel.updateTransactionDetails(
                            oldTx = tx,
                            newDisplayAmount = amt,
                            newNote = noteText,
                            newCategoryId = selectedCatId,
                            receiptUri = receiptUri,
                            splitDetails = splitNote
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------
// 2. Reports & Analytics Bottom Sheet (Bar Chart + PDF/CSV Export)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAnalyticsSheet(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var periodFilter by remember { mutableStateOf("Monthly") } // Daily, Weekly, Monthly, Yearly
    val accent = LocalAccentColor.current

    val cutoffMs = remember(periodFilter) {
        val now = System.currentTimeMillis()
        when (periodFilter) {
            "Daily" -> now - 24L * 3600 * 1000
            "Weekly" -> now - 7L * 24 * 3600 * 1000
            "Monthly" -> now - 30L * 24 * 3600 * 1000
            else -> now - 365L * 24 * 3600 * 1000
        }
    }

    val filtered = remember(transactions, cutoffMs) {
        transactions.filter { it.transaction.timestamp >= cutoffMs }
    }
    val periodIncome = filtered.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
    val periodExpense = filtered.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }
    val highestCategory = remember(filtered) {
        filtered.filter { it.transaction.type == TransactionType.EXPENSE && it.category != null }
            .groupBy { it.category!!.name }
            .mapValues { e -> e.value.sumOf { it.transaction.amount } }
            .maxByOrNull { it.value }
    }

    // 7-day Income vs Expense bar pairs
    val barSeries = remember(transactions) {
        val cal = Calendar.getInstance()
        (6 downTo 0).map { offset ->
            val dayCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val start = (dayCal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.timeInMillis
            val end = start + 24L * 3600 * 1000
            val dayTxs = transactions.filter { it.transaction.timestamp in start until end }
            val inc = dayTxs.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
            val exp = dayTxs.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }
            val label = SimpleDateFormat("EEE", Locale.US).format(Date(start))
            Triple(label, inc, exp)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reports & Statement Studio", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
            }

            // Period Selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { p ->
                    val sel = p == periodFilter
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (sel) accent else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).clickable { periodFilter = p }
                    ) {
                        Text(
                            text = p,
                            color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }
                }
            }

            // Summary Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = StateGreen.copy(alpha = 0.12f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("$periodFilter Income", fontSize = 11.sp, color = StateGreen, fontWeight = FontWeight.Bold)
                        Text(viewModel.formatAmount(periodIncome), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StateGreen)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = StateRed.copy(alpha = 0.12f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("$periodFilter Expense", fontSize = 11.sp, color = StateRed, fontWeight = FontWeight.Bold)
                        Text(viewModel.formatAmount(periodExpense), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StateRed)
                    }
                }
            }

            // Income vs Expense Bar Graph (Canvas)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("7-Day Income vs Expense", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("● Income", fontSize = 10.sp, color = StateGreen, fontWeight = FontWeight.Bold)
                            Text("● Expense", fontSize = 10.sp, color = StateRed, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val maxVal = barSeries.maxOfOrNull { maxOf(it.second, it.third) }?.coerceAtLeast(1.0) ?: 1.0
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        val groupW = size.width / barSeries.size
                        val barW = (groupW * 0.28f).coerceAtLeast(8f)
                        barSeries.forEachIndexed { idx, (_, inc, exp) ->
                            val xCenter = groupW * idx + groupW / 2f
                            val incH = ((inc / maxVal).toFloat() * size.height).coerceAtLeast(4f)
                            val expH = ((exp / maxVal).toFloat() * size.height).coerceAtLeast(4f)
                            drawRoundRect(
                                color = StateGreen,
                                topLeft = Offset(xCenter - barW - 2f, size.height - incH),
                                size = Size(barW, incH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                            drawRoundRect(
                                color = StateRed,
                                topLeft = Offset(xCenter + 2f, size.height - expH),
                                size = Size(barW, expH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        barSeries.forEach { (day, _, _) ->
                            Text(text = day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (highestCategory != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StateYellow.copy(alpha = 0.14f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Highest Spending Detection: ${highestCategory.key} (${viewModel.formatAmount(highestCategory.value)})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Export PDF & CSV Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.exportPdfReport(context, accounts, filtered) },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("export_pdf_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export PDF", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }
                Button(
                    onClick = { viewModel.exportCsvReport(context, filtered) },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("export_csv_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Custom Category Creator Dialog
// -------------------------------------------------------------
@Composable
fun CustomCategoryDialog(
    viewModel: KoshvantaViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    val colors = listOf("#06B6D4", "#EC4899", "#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#EF4444")
    var selectedColor by remember { mutableStateOf(colors.first()) }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Category", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name (e.g. Pets, Crypto, Gym)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isExpense) StateRed else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).clickable { isExpense = true }
                    ) {
                        Text(
                            "Expense",
                            color = if (isExpense) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isExpense) StateGreen else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).clickable { isExpense = false }
                    ) {
                        Text(
                            "Income",
                            color = if (!isExpense) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Text("Badge Color:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(if (hex == selectedColor) 32.dp else 26.dp)
                                .clip(CircleShape)
                                .background(c)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addCustomCategory(name, "payments", selectedColor, isExpense)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Add Category", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------
// 4. Add Bill Reminder Dialog
// -------------------------------------------------------------
@Composable
fun AddBillDialog(
    viewModel: KoshvantaViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var daysText by remember { mutableStateOf("5") }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Bill Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill Title (e.g. Electricity, Netflix)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    label = { Text("Due in Days (e.g. 5)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    val days = daysText.toIntOrNull() ?: 5
                    if (title.isNotBlank() && amt > 0.0) {
                        viewModel.addBillReminder(title, amt, days)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Save Reminder", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
