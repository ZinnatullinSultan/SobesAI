package com.example.sobesai.presentation.specialization.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.Border
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.specialization_difficulty_junior
import sobesai.composeapp.generated.resources.specialization_difficulty_middle
import sobesai.composeapp.generated.resources.specialization_difficulty_senior

@Composable
fun DifficultyCard(
    level: DifficultyLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    cardHeight: Dp = AppDimens.Components.DifficultyCardHeight,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Border else Color.Transparent
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = if (isSelected) BorderStroke(
                AppDimens.Components.BorderStroke,
                borderColor
            ) else null,
            shape = RoundedCornerShape(AppDimens.CornerShape.Small)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimens.Padding.Small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    repeat(level.starCount) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(AppDimens.IconSize.Small)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
                Text(
                    level.name,
                    style = AppTypography.labelLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
        Text(
            text = stringResource(level.labelRes),
            style = AppTypography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

enum class DifficultyLevel(
    val starCount: Int,
    val labelRes: StringResource
) {
    Junior(1, Res.string.specialization_difficulty_junior),
    Middle(2, Res.string.specialization_difficulty_middle),
    Senior(3, Res.string.specialization_difficulty_senior)
}
