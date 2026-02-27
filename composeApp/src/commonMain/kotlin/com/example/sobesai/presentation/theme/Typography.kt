package com.example.sobesai.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.dancingscript_bold

val DancingScriptFontFamily: FontFamily
    @Composable
    get() = FontFamily(Font(Res.font.dancingscript_bold))