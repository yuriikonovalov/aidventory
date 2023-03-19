package com.aidventory.feature.settings.presentation.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.domain.model.Theme
import com.aidventory.feature.settings.R

@Composable
internal fun ThemeScreen(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle(null)

    ThemeScreenContent(
        modifier = modifier.padding(horizontal = 16.dp),
        theme = theme,
        onThemeClick = viewModel::changeTheme
    )
}

@Composable
private fun ThemeScreenContent(
    theme: Theme?,
    onThemeClick: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ThemeItem(
                text = stringResource(R.string.theme_item_light),
                isSelected = theme == Theme.LIGHT,
                onClick = { onThemeClick(Theme.LIGHT) }
            )
        }
        item {
            ThemeItem(
                text = stringResource(R.string.theme_item_dark),
                isSelected = theme == Theme.DARK,
                onClick = { onThemeClick(Theme.DARK) }
            )
        }
        item {
            ThemeItem(
                text = stringResource(R.string.theme_item_follow_system),
                isSelected = theme == Theme.FOLLOW_SYSTEM,
                onClick = { onThemeClick(Theme.FOLLOW_SYSTEM) }
            )
        }
    }
}

@Composable
private fun ThemeItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(modifier = Modifier.weight(1f), text = text)
    }
}

@Preview
@Composable
private fun ThemeScreenContentPreview() {
    AidventoryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ThemeScreenContent(theme = Theme.DARK, onThemeClick = {})
        }
    }
}