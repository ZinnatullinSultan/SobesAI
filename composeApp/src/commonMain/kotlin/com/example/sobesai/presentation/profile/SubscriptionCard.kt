package com.example.sobesai.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.sobesai.domain.model.SubscriptionStatus
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.subscription_current_plan
import sobesai.composeapp.generated.resources.subscription_free
import sobesai.composeapp.generated.resources.subscription_interviews_unlimited
import sobesai.composeapp.generated.resources.subscription_interviews_used
import sobesai.composeapp.generated.resources.subscription_premium
import sobesai.composeapp.generated.resources.subscription_upgrade_button

@Composable
internal fun SubscriptionCard(
    subscriptionStatus: SubscriptionStatus,
    onPurchasePremium: () -> Unit,
    isUpgrading: Boolean
) {
    Card(
        modifier = Modifier
            .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.Padding.Normal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlanHeader(subscriptionStatus)
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
            
            if (subscriptionStatus.isPremium) {
                PremiumContent()
            } else {
                FreeUserContent(
                    subscriptionStatus = subscriptionStatus,
                    onPurchasePremium = onPurchasePremium,
                    isUpgrading = isUpgrading
                )
            }
        }
    }
}

@Composable
private fun PlanHeader(subscriptionStatus: SubscriptionStatus) {
    Text(
        text = stringResource(Res.string.subscription_current_plan),
        style = AppTypography.labelMedium
    )
    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
    
    Text(
        text = if (subscriptionStatus.isPremium) {
            stringResource(Res.string.subscription_premium)
        } else {
            stringResource(Res.string.subscription_free)
        },
        style = AppTypography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = if (subscriptionStatus.isPremium) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
}

@Composable
private fun PremiumContent() {
    Text(
        text = stringResource(Res.string.subscription_interviews_unlimited),
        style = AppTypography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun FreeUserContent(
    subscriptionStatus: SubscriptionStatus,
    onPurchasePremium: () -> Unit,
    isUpgrading: Boolean
) {
    Text(
        text = stringResource(
            Res.string.subscription_interviews_used,
            subscriptionStatus.interviewsUsed,
            subscriptionStatus.interviewsLimit
        ),
        style = AppTypography.bodyMedium
    )
    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
    
    LinearProgressIndicator(
        progress = { 
            subscriptionStatus.interviewsUsed.toFloat() / 
            subscriptionStatus.interviewsLimit.toFloat() 
        },
        modifier = Modifier.fillMaxWidth(),
        color = if (subscriptionStatus.interviewsUsed >= subscriptionStatus.interviewsLimit) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))

    AppButton(
        text = stringResource(Res.string.subscription_upgrade_button),
        onClick = onPurchasePremium,
        isLoading = isUpgrading
    )
}
