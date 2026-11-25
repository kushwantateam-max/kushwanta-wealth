package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalGoldAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.viewmodel.KoshvantaViewModel

@Composable
fun WelcomeAuthScreen(
    viewModel: KoshvantaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val accentColor = LocalAccentColor.current
    val goldAccent = LocalGoldAccentColor.current

    var isEmailExpanded by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isBusy by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isFeedbackError by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "welcome_aura")
    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_rotation"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF07090E),
                DarkCanvasBg,
                Color(0xFF0E141B)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF4F7FA),
                Color(0xFFFFFFFF),
                Color(0xFFEFF5F4)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("welcome_auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Brand Hero Centerpiece with Animated Aura
            Box(
                modifier = Modifier.size(144.dp),
                contentAlignment = Alignment.Center
            ) {
                val auraColors = if (isDark) {
                    listOf(NeonMint, ElectricCyan, RoyalGold, NeonMint)
                } else {
                    listOf(accentColor, goldAccent, accentColor.copy(alpha = 0.35f), accentColor)
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                (if (isDark) NeonMint else accentColor).copy(alpha = pulseAlpha * 0.45f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.52f
                    )
                    rotate(degrees = auraRotation) {
                        drawArc(
                            brush = Brush.sweepGradient(auraColors),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                            size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx()),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(DarkCanvasBg),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.koshvanta_official_logo),
                        contentDescription = "Kushwanta Official Emblem",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val welcomeWordmarkBrush = remember(isDark) {
                Brush.linearGradient(
                    if (isDark) {
                        listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                    } else {
                        listOf(Color(0xFF00796B), Color(0xFF0F172A))
                    }
                )
            }
            Text(
                text = "KUSHWANTA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = welcomeWordmarkBrush,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.8.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (isDark) Color(0xFFFFD700).copy(alpha = 0.12f) else Color(0xFF00796B).copy(alpha = 0.10f),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFFFFD700).copy(alpha = 0.35f) else Color(0xFF00796B).copy(alpha = 0.28f)
                )
            ) {
                Text(
                    text = "WEALTH OS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.2.sp,
                    color = if (isDark) Color(0xFFFFD700) else Color(0xFF00796B),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Global Wealth Vault • Smart Ledger & Mind Arena",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // 2. Option 1 — Continue with Google (Custom Canvas Multi-Color 'G')
            Surface(
                onClick = {
                    if (!isBusy) {
                        isBusy = true
                        feedbackMessage = null
                        viewModel.signInWithGoogle(context) { ok, errMsg ->
                            isBusy = false
                            if (!ok && !errMsg.isNullOrBlank()) {
                                isFeedbackError = true
                                feedbackMessage = errMsg
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color.White else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline
                ),
                shadowElevation = if (isDark) 0.dp else 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("welcome_google_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isBusy && !isEmailExpanded) {
                        CircularProgressIndicator(
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Signing in with Google…",
                            color = Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        GoogleGLogo(modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!isEmailExpanded && !feedbackMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = (if (isFeedbackError) StateRed else StateGreen).copy(alpha = 0.14f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = feedbackMessage!!,
                        color = if (isFeedbackError) StateRed else StateGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Option 2 — Continue with Email (Expandable Glassmorphic Card)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isEmailExpanded = !isEmailExpanded
                                feedbackMessage = null
                            }
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                            .testTag("welcome_email_toggle_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEmailExpanded) "Hide Email Sign In" else "Continue with Email",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    AnimatedVisibility(
                        visible = isEmailExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                            // Smooth Sign In / Create Account Switcher
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        isSignUpMode = false
                                        feedbackMessage = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (!isSignUpMode) accentColor else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("welcome_mode_signin")
                                ) {
                                    Text(
                                        text = "Sign In",
                                        color = if (!isSignUpMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                                Surface(
                                    onClick = {
                                        isSignUpMode = true
                                        feedbackMessage = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSignUpMode) accentColor else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("welcome_mode_signup")
                                ) {
                                    Text(
                                        text = "Create Account",
                                        color = if (isSignUpMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isSignUpMode) {
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = {
                                        fullName = it
                                        feedbackMessage = null
                                    },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("welcome_fullname_input")
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    feedbackMessage = null
                                },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("welcome_email_input")
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    feedbackMessage = null
                                },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        modifier = Modifier.testTag("welcome_password_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("welcome_password_input")
                            )

                            if (!isSignUpMode) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (!isBusy) {
                                                isBusy = true
                                                viewModel.sendPasswordReset(context, email) { ok, msg ->
                                                    isBusy = false
                                                    isFeedbackError = !ok
                                                    feedbackMessage = msg
                                                }
                                            }
                                        },
                                        modifier = Modifier.testTag("welcome_forgot_password_button")
                                    ) {
                                        Text(
                                            text = "Forgot Password?",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = accentColor
                                        )
                                    }
                                }
                            }

                            if (!feedbackMessage.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = (if (isFeedbackError) StateRed else StateGreen).copy(alpha = 0.14f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = feedbackMessage!!,
                                        color = if (isFeedbackError) StateRed else StateGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (!isBusy) {
                                        isBusy = true
                                        feedbackMessage = null
                                        if (isSignUpMode) {
                                            viewModel.createAccountWithEmail(
                                                context = context,
                                                fullName = fullName,
                                                email = email,
                                                password = password
                                            ) { ok, msg ->
                                                isBusy = false
                                                if (!ok) {
                                                    isFeedbackError = true
                                                    feedbackMessage = msg
                                                }
                                            }
                                        } else {
                                            viewModel.signInWithEmail(
                                                context = context,
                                                email = email,
                                                password = password
                                            ) { ok, msg ->
                                                isBusy = false
                                                if (!ok) {
                                                    isFeedbackError = true
                                                    feedbackMessage = msg
                                                }
                                            }
                                        }
                                    }
                                },
                                enabled = !isBusy,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("welcome_email_submit_button")
                            ) {
                                if (isBusy) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = if (isSignUpMode) "Create Account" else "Sign In",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Option 3 — Continue as Guest / Explore Vault
            OutlinedButton(
                onClick = { viewModel.continueAsGuest() },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("welcome_guest_button")
            ) {
                Text(
                    text = "Continue as Guest / Explore Vault",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "You can link Google or Email anytime later in Profile Hub.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GoogleGLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.20f
        val inset = strokeWidth / 2f
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(inset, inset)

        // Red top-left arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -145f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Yellow left-bottom arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 145f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 35f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Blue right arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -5f,
            sweepAngle = 45f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Blue horizontal crossbar of 'G'
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.52f, size.height * 0.5f),
            end = Offset(size.width - inset * 0.4f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Butt
        )
    }
}
