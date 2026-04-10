package com.example.sobesai.presentation.main.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.PinIconActive
import com.example.sobesai.presentation.theme.PinIconDefault
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_pin_icon_description

@Composable
fun SpecializationCard(
    specialization: Specialization,
    onPinClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimens.Padding.Small)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Elevation.Small),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                top = AppDimens.Padding.Tiny,
                bottom = AppDimens.Padding.Normal,
                start = AppDimens.Padding.Normal,
                end = AppDimens.Padding.Normal,
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = specialization.title,
                        style = AppTypography.titleSmall
                    )
                    Spacer(modifier = Modifier.size(AppDimens.SpacerHeight.Tiny))
                    if (specialization.imageUrl.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(AppDimens.IconSize.Normal)
                        )
                    } else {
                        AsyncImage(
                            model = specialization.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(AppDimens.IconSize.Normal)
                                .clip(MaterialTheme.shapes.small)
                        )
                    }
                }
                IconButton(onClick = { onPinClick() }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(Res.string.main_pin_icon_description),
                        tint = if (specialization.isPinned) PinIconActive else PinIconDefault
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraTiny))
            Text(
                text = specialization.description,
                style = AppTypography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpecializationCard() {
    SpecializationCard(
        specialization = Specialization(
            id = 1,
            title = "Android Developer",
            description = "Разработка мобильных приложений на Kotlin и Jetpack Compose.",
            isPinned = true,
            pinOrder = 1
        ),
        onPinClick = {},
        onItemClick = { },
        modifier = Modifier
    )
}
