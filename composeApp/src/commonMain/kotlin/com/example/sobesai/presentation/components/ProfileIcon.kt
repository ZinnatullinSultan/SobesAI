package com.example.sobesai.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.presentation.theme.AppDimens
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_profile_icon_description

@Composable
fun ProfileIcon(
    onProfileClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onProfileClick ?: {},
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(Res.string.main_profile_icon_description),
            modifier = modifier.size(AppDimens.IconSize.ExtraLarge)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileIcon() {
    ProfileIcon(
        onProfileClick = {}
    )
}
