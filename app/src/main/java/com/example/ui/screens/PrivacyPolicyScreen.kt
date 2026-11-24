package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalGoldAccentColor
import com.example.ui.theme.LocalIsDarkTheme

private const val SUPPORT_EMAIL = "kushwanta.team@gmail.com"
private const val ONLINE_PRIVACY_POLICY_URL = "https://kushwantateam-max.github.io/kushwanta-privacy/"

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = LocalIsDarkTheme.current
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val screenBg = if (isDarkTheme) Color(0xFF0B0C10) else Color(0xFFE8EEF5)
    val cardBg = if (isDarkTheme) Color(0xFF131720) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) Color(0xFF00FFCC).copy(alpha = 0.16f) else Color(0xFFCBD5E1)
    val primaryText = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val mutedText = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)
    val accentColor = if (isDarkTheme) Color(0xFF00FFCC) else Color(0xFF00695C)
    val goldAccent = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFFB45309)
    val wordmarkBrush = remember(isDarkTheme) {
        Brush.linearGradient(
            if (isDarkTheme) listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
            else listOf(Color(0xFF00695C), Color(0xFF0F172A))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBg)
            .testTag("privacy_policy_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("privacy_policy_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Settings",
                    tint = primaryText
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryText
                    )
                )
                Text(
                    text = "Kushwanta Team • Legal & Data Safety",
                    fontSize = 11.sp,
                    color = mutedText
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("privacy_policy_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(DarkCanvasBg)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF00FFCC))),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.koshvanta_official_logo),
                                contentDescription = "Kushwanta Emblem",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "KUSHWANTA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                brush = wordmarkBrush,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Privacy Policy & App Usage Disclaimer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PolicyMetaPill("Kushwanta Team", accentColor)
                            PolicyMetaPill("Effective: Nov 20, 2026", accentColor)
                        }
                    }
                }
            }

            item {
                PolicySectionCard(
                    number = "SECTION 01",
                    title = "Our Privacy Commitment",
                    body = "Kushwanta is designed by the Kushwanta Team as a privacy-first personal finance and cognitive wellness application. This Privacy Policy explains what information is stored locally on your device, what limited information is processed if you choose to sign in, how third-party advertising partners operate, and how you can permanently delete your account and all data at any time.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    footerEmail = SUPPORT_EMAIL,
                    accentColor = accentColor,
                    onEmailClick = { openSupportEmail(context) }
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 02",
                    title = "Local-First Financial & Mind Arena Storage",
                    body = "Your financial records and game progress belong strictly to you. All personal finance data (transactions, accounts, budgets, savings goals, bill reminders, debts, metal card vault entries, warranties, quick presets, and calculator histories) and all 10 Mind Arena game scores, streaks, and XP remain strictly on your device. We never sell, rent, or upload your personal financial transactions or ledger records to external marketing servers.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    accentColor = accentColor
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 03",
                    title = "Optional Authentication (Firebase Auth)",
                    body = "You may use Kushwanta completely in Guest Mode without creating an account. If you optionally choose to sign in with Google Sign-In or Email & Password via Google Firebase Authentication, we collect and process only your Display Name, Email Address, and Profile Photo URL (when signing in with Google) to personalize your profile. Passwords are handled securely by Firebase Authentication and are never stored in plain text.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    accentColor = accentColor
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 04",
                    title = "Third-Party Advertising Partners",
                    body = "To keep Kushwanta accessible and support ongoing development, the app integrates third-party advertising networks, specifically Google AdMob and Unity Ads. These partners may use the Android Advertising ID (AAID) and standard device signals (such as device model, OS version, IP address, and ad interaction metrics) to serve and measure relevant ads. You can reset or limit ad personalization anytime in your Android device's Settings → Google → Ads.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    accentColor = accentColor
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 05",
                    title = "One-Tap Account & Data Deletion",
                    body = "You can permanently delete your linked account and wipe all local Room database tables, preferences, avatar files, and Mind Arena game progress anytime via Settings → Data & Reset Controls → Delete Account & All Data. You may also request deletion assistance by emailing us below. Using Sign Out in Profile only ends your active login session and preserves your local vault data.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    footerEmail = SUPPORT_EMAIL,
                    accentColor = accentColor,
                    onEmailClick = { openSupportEmail(context) }
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 06",
                    title = "Children's Privacy",
                    body = "Kushwanta is not directed to children under the age of 13 (or the minimum digital consent age in your jurisdiction). We do not knowingly collect personal information from children under 13. If you believe a child has provided personal information through the app, please email us immediately so we can promptly remove it.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    accentColor = accentColor
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 07",
                    title = "App Usage & Financial Disclaimer",
                    body = "Kushwanta is a personal finance tracking, budgeting, educational calculator, and cognitive entertainment tool. Kushwanta is not a registered bank, investment broker, lender, or certified financial, legal, or tax advisor. All calculators, projections, and insights are provided strictly for informational and educational purposes.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    accentColor = accentColor
                )
            }

            item {
                PolicySectionCard(
                    number = "SECTION 08",
                    title = "Policy Updates & Contact Us",
                    body = "We may update this Privacy Policy periodically to reflect new features or legal requirements. Publisher: Kushwanta Team • Effective Date: November 20, 2026.",
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    titleColor = primaryText,
                    bodyColor = mutedText,
                    goldAccent = goldAccent,
                    footerEmail = SUPPORT_EMAIL,
                    accentColor = accentColor,
                    onEmailClick = { openSupportEmail(context) }
                )
            }

            item {
                OutlinedButton(
                    onClick = { openOnlinePrivacyPolicy(context) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("privacy_policy_online_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Online Privacy Policy",
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Online Privacy Policy ↗",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            item {
                Text(
                    text = "© 2026 Kushwanta Team. All rights reserved.",
                    fontSize = 11.sp,
                    color = mutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 84.dp)
                )
            }
        }
    }
}

@Composable
private fun PolicyMetaPill(text: String, accentColor: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accentColor.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PolicySectionCard(
    number: String,
    title: String,
    body: String,
    cardBg: Color,
    cardBorder: Color,
    titleColor: Color,
    bodyColor: Color,
    goldAccent: Color,
    accentColor: Color,
    footerEmail: String? = null,
    onEmailClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = number,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = goldAccent
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = body,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = bodyColor
            )
            if (footerEmail != null && onEmailClick != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEmailClick() }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                        .testTag("privacy_policy_email_link")
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Support",
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = footerEmail,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}

private fun openSupportEmail(context: Context) {
    runCatching {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$SUPPORT_EMAIL")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

private fun openOnlinePrivacyPolicy(context: Context) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ONLINE_PRIVACY_POLICY_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
