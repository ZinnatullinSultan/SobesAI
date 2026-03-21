package com.example.sobesai.presentation.specialization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.components.AppTopBar
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.Border
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.specialization_description
import sobesai.composeapp.generated.resources.specialization_difficulty_junior
import sobesai.composeapp.generated.resources.specialization_difficulty_middle
import sobesai.composeapp.generated.resources.specialization_difficulty_senior
import sobesai.composeapp.generated.resources.specialization_start_interview


@Composable
fun SpecializationScreen(
    id: Long,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStartInterview: (Long, DifficultyLevel) -> Unit,
    viewModel: SpecializationViewModel = koinViewModel(parameters = { parametersOf(id) })
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    SpecializationContent(
        state = state,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        modifier = Modifier.verticalScroll(scrollState),
        onLevelSelected = { viewModel.onLevelSelected(it) },
        onStartInterview = onStartInterview
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecializationContent(
    state: SpecializationUiState,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLevelSelected: (DifficultyLevel) -> Unit,
    onStartInterview: (Long, DifficultyLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    val specialization = state.specialization ?: return

    Scaffold(
        topBar = {
            AppTopBar(
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onClearClick = null
            )
        },
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()

        ) {
            val isLandscape = maxWidth > maxHeight

            val horizontalPadding =
                if (isLandscape) AppDimens.Padding.Normal else AppDimens.Padding.Large
            val horizontalAlignment =
                if (isLandscape) Alignment.CenterHorizontally else Alignment.Start
            val topSpacerHeight =
                if (isLandscape) AppDimens.SpacerHeight.Tiny else AppDimens.SpacerHeight.Normal
            val bottomSpacerHeight =
                if (isLandscape) AppDimens.SpacerHeight.Normal else AppDimens.SpacerHeight.ExtraLarge
            val sectionSpacerHeight =
                if (isLandscape) AppDimens.SpacerHeight.Small else AppDimens.SpacerHeight.ExtraLarge
            val cardHeight = if (isLandscape) 80.dp else AppDimens.Components.DifficultyCardHeight
            val textStyle =
                if (isLandscape) AppTypography.headlineMedium else AppTypography.headlineLarge

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = horizontalAlignment
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                Text(
                    text = specialization.title,
                    style = textStyle
                )

                Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))

                Text(
                    text = stringResource(
                        Res.string.specialization_description,
                        specialization.title
                    ),
                    style = AppTypography.labelSmall
                )

                Spacer(modifier = Modifier.height(sectionSpacerHeight))

                Row(
                    modifier = Modifier.widthIn(max = AppDimens.Components.TextFieldMaxWidth)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Components.ArrangementSpaceSmall)
                ) {
                    DifficultyLevel.entries.forEach { level ->
                        DifficultyCard(
                            level = level,
                            isSelected = state.selectedLevel == level,
                            onClick = { onLevelSelected(level) },
                            cardHeight = cardHeight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (!isLandscape) {
                    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
                } else {
                    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))
                }

                AppButton(
                    text = stringResource(Res.string.specialization_start_interview),
                    onClick = { onStartInterview(specialization.id, state.selectedLevel) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(bottomSpacerHeight))
            }
        }
    }
}

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
                    repeat(
                        when (level) {
                            DifficultyLevel.Junior -> 1
                            DifficultyLevel.Middle -> 2
                            DifficultyLevel.Senior -> 3
                        }
                    ) {
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
            text = when (level) {
                DifficultyLevel.Junior -> stringResource(Res.string.specialization_difficulty_junior)
                DifficultyLevel.Middle -> stringResource(Res.string.specialization_difficulty_middle)
                DifficultyLevel.Senior -> stringResource(Res.string.specialization_difficulty_senior)
            },
            style = AppTypography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewSpecializationScreen() {
    SpecializationContent(
        state = SpecializationUiState(
            specialization = Specialization(1, "Android Developer", "Description"),
            selectedLevel = DifficultyLevel.Middle
        ),
        onBackClick = {},
        onLevelSelected = {},
        onProfileClick = {},
        onStartInterview = { _, _ -> }
    )
}
