package com.example.sobesai.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.dancingscript_bold

val DancingScriptFontFamily: FontFamily
    @Composable
    get() = FontFamily(Font(Res.font.dancingscript_bold))

val AppTypography: Typography
    @Composable
    get() = Typography(
        //Заголовки
        displayLarge = TextStyle(
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = DancingScriptFontFamily,
            letterSpacing = 1.sp,
            lineHeight = 68.sp,
            brush = Brush.linearGradient(colors = TitleGradient)
        ),
        displaySmall = TextStyle(
            fontSize = 48.sp,
            fontFamily = DancingScriptFontFamily,
            letterSpacing = 1.sp,
            lineHeight = 52.sp,
            brush = Brush.linearGradient(colors = TitleGradient)
        ),
        headlineLarge = TextStyle(
            fontFamily = DancingScriptFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            brush = Brush.linearGradient(colors = TitleGradient)
        ),
        headlineMedium = TextStyle(
            fontFamily = DancingScriptFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            brush = Brush.linearGradient(colors = TitleGradient)
        ),
        titleLarge = TextStyle(
            fontSize = 32.sp
        ),
        titleMedium = TextStyle(
            fontSize = 24.sp,

            ),
        titleSmall = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        ),
        // Текст
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),

        // Кнопки
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )