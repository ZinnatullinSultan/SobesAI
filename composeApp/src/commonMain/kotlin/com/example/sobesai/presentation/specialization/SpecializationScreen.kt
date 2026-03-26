package com.example.sobesai.presentation.specialization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.components.AppTopBar
import com.example.sobesai.presentation.specialization.components.DifficultyCard
import com.example.sobesai.presentation.specialization.components.DifficultyLevel
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.specialization_description
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
        onLevelSelected = { viewModel.onLevelSelected(it) },
        onStartInterview = onStartInterview,
        modifier = Modifier.verticalScroll(scrollState)
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
                    modifier = Modifier
                        .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
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

@Preview
@Composable
fun PreviewSpecializationScreen() {
    SpecializationContent(
        state = SpecializationUiState(
            specialization = Specialization(1, "Android Developer", "Description"),
            selectedLevel = DifficultyLevel.Middle
        ),
        onBackClick = {},
        onProfileClick = {},
        onLevelSelected = {},
        onStartInterview = { _, _ -> }
    )
}
