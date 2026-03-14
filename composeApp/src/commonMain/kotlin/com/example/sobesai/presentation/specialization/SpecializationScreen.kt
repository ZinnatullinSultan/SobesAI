package com.example.sobesai.presentation.Specialization

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.Border
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.specialization_description
import sobesai.composeapp.generated.resources.specialization_difficulty_junior
import sobesai.composeapp.generated.resources.specialization_difficulty_middle
import sobesai.composeapp.generated.resources.specialization_difficulty_senior
import sobesai.composeapp.generated.resources.specialization_start_interview

@Composable
fun SpecializationScreen(
    id: Long,
    onBackClick: () -> Unit,
    onStartInterview: (Long, DifficultyLevel) -> Unit,
    viewModel: SpecializationViewModel = koinViewModel(parameters = { parametersOf(id) })
) {
    val state by viewModel.state.collectAsState()

    SpecializationContent(
        state = state,
        onBackClick = onBackClick,
        onLevelSelected = { viewModel.onLevelSelected(it) },
        onStartInterview = onStartInterview
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializationContent(
    state: SpecializationUiState,
    onBackClick: () -> Unit,
    onLevelSelected: (DifficultyLevel) -> Unit,
    onStartInterview: (Long, DifficultyLevel) -> Unit
) {
    val specialization = state.specialization ?: return

    Scaffold(
        topBar = {
            SpecializationTopBar(onBackClick = onBackClick)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.Padding.Large),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))

            Text(
                text = specialization.title,
                style = AppTypography.headlineLarge
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))

            Text(
                text = stringResource(
                    Res.string.specialization_description,
                    specialization.title
                ),
                style = AppTypography.labelSmall
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraLarge))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Components.ArrangementSpaceSmall)
            ) {
                DifficultyCard(
                    level = DifficultyLevel.Junior,
                    isSelected = state.selectedLevel == DifficultyLevel.Junior,
                    onClick = { onLevelSelected(DifficultyLevel.Junior) },
                    modifier = Modifier.weight(1f)
                )
                DifficultyCard(
                    level = DifficultyLevel.Middle,
                    isSelected = state.selectedLevel == DifficultyLevel.Middle,
                    onClick = { onLevelSelected(DifficultyLevel.Middle) },
                    modifier = Modifier.weight(1f)
                )
                DifficultyCard(
                    level = DifficultyLevel.Senior,
                    isSelected = state.selectedLevel == DifficultyLevel.Senior,
                    onClick = { onLevelSelected(DifficultyLevel.Senior) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AppButton(
                text = stringResource(Res.string.specialization_start_interview),
                onClick = { onStartInterview(specialization.id, state.selectedLevel) },
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraLarge))
        }
    }
}

@Composable
fun DifficultyCard(
    level: DifficultyLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Border else Color.Transparent
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.Components.DifficultyCardHeight)
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
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = when (level) {
//                        DifficultyLevel.Junior -> "Базовые вопросы, JVM"
//                        DifficultyLevel.Middle -> "Архитектура, Kotlin, Тесты"
//                        DifficultyLevel.Senior -> "Системный дизайн, High-Load"
//                    },
//                    color = Color.Gray,
//                    fontSize = 10.sp,
//                    textAlign = TextAlign.Center,
//                    lineHeight = 12.sp
//                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializationTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(Res.string.app_title),
                style = AppTypography.headlineLarge,

                )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .padding(AppDimens.Padding.Normal)
                    .size(AppDimens.IconSize.Large)
                    .clip(CircleShape)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        )
    )
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
        onStartInterview = { _, _ -> }
    )
}
