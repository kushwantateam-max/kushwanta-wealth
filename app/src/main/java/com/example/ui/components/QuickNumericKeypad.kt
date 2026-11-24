package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DarkCanvasBg

@Composable
fun QuickNumericKeypad(
    currentExpression: String,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEvaluate: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkKeypad: Boolean = true,
    keyHeight: Dp = 46.dp,
    onKeyFeedback: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val keys = listOf(
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf(".", "0", "C", "+")
    )

    val keypadBg = if (isDarkKeypad) DarkCanvasBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val buttonBg = if (isDarkKeypad) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface
    val operatorBg = if (isDarkKeypad) Color(0xFF334155) else CobaltBlue.copy(alpha = 0.12f)
    val textPrimary = if (isDarkKeypad) Color.White else MaterialTheme.colorScheme.onSurface
    val operatorColor = if (isDarkKeypad) Color(0xFF38BDF8) else CobaltBlue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(keypadBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    val isOperator = key in listOf("÷", "×", "-", "+")
                    val isAction = key == "C"

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(keyHeight)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isOperator) operatorBg else if (isAction) Color(0xFFEF4444).copy(alpha = 0.15f) else buttonBg)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onKeyFeedback()
                                when (key) {
                                    "C" -> onClear()
                                    "=" -> onEvaluate()
                                    else -> onKeyClick(key)
                                }
                            }
                            .testTag("keypad_button_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = if (isOperator) operatorColor else if (isAction) Color(0xFFEF4444) else textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom action row: Backspace and Save Confirm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(buttonBg)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onKeyFeedback()
                        onBackspace()
                    }
                    .testTag("keypad_backspace"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = textPrimary
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("keypad_save_transaction_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CobaltBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Save Transaction",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
