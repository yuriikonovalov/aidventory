package com.aidventory.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.layout.DisplayFeature
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.ListDetailPane
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.feature.settings.R
import com.aidventory.feature.settings.SettingCategory
import com.aidventory.feature.settings.presentation.about.AboutScreen
import com.aidventory.feature.settings.presentation.data.DataScreen
import com.aidventory.feature.settings.presentation.supplyuses.SupplyUsesScreen
import com.aidventory.feature.settings.presentation.theme.ThemeScreen
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy

@Composable
internal fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    val showListAndDetail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val onNavigationClick = state.getOnNavigationClickAction(
        showListAndDetail = showListAndDetail,
        closeDetails = viewModel::closeDetails,
        navigateUp = navController::navigateUp
    )
    SettingsScreenContent(
        modifier = Modifier.fillMaxSize(),
        displayFeatures = displayFeatures,
        showListAndDetail = showListAndDetail,
        isDetailOpen = state.isDetailOpen,
        onNavigationClick = onNavigationClick,
        selectedSettingCategory = state.selectedSettingCategory,
        setIsDetailOpen = viewModel::setIsDetailOpen,
        onSettingCategoryClick = viewModel::selectSettingCategory
    )
}

private fun SettingsUiState.getOnNavigationClickAction(
    showListAndDetail: Boolean,
    closeDetails: () -> Unit,
    navigateUp: () -> Unit
): () -> Unit {
    return if (isDetailOpen && !showListAndDetail) {
        closeDetails
    } else {
        navigateUp
    }
}

@Composable
private fun SettingsScreenContent(
    displayFeatures: List<DisplayFeature>,
    showListAndDetail: Boolean,
    isDetailOpen: Boolean,
    selectedSettingCategory: SettingCategory?,
    setIsDetailOpen: (Boolean) -> Unit,
    onSettingCategoryClick: (SettingCategory) -> Unit,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            isDetailOpen = isDetailOpen,
            showListAndDetail = showListAndDetail,
            onNavigationClick = onNavigationClick,
            selectedSettingCategory = selectedSettingCategory
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListDetailPane(
            isDetailOpen = isDetailOpen,
            setIsDetailOpen = setIsDetailOpen,
            showListAndDetail = showListAndDetail,
            detailKey = selectedSettingCategory,
            list = {
                SettingsPane(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    selectedSettingCategory = selectedSettingCategory,
                    onItemClick = onSettingCategoryClick
                )
            },
            detail = {
                DetailPane(
                    selectedSettingCategory = selectedSettingCategory
                )
            },
            twoPaneStrategy = HorizontalTwoPaneStrategy(splitFraction = 1f / 2f),
            displayFeatures = displayFeatures
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingTopAppBar(
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    selectedSettingCategory: SettingCategory?,
    onNavigationClick: () -> Unit,
    modifier: Modifier
) {

    val title = if (isDetailOpen && !showListAndDetail) {
        stringResource(selectedSettingCategory!!.textStringRes)
    } else {
        stringResource(R.string.settings)
    }

    AidventoryTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = title,
        navigationIcon = AidventoryIcons.ArrowBack.imageVector,
        onNavigationClick = onNavigationClick
    )
}

@Composable
private fun SettingsPane(
    selectedSettingCategory: SettingCategory?,
    onItemClick: (SettingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = SettingCategory.values(),
            key = { it.name }
        ) { settingCategory ->
            val icon = when (settingCategory) {
                SettingCategory.DATA -> AidventoryIcons.Storage.imageVector
                SettingCategory.SUPPLY_USES -> AidventoryIcons.Category.imageVector
                SettingCategory.THEME -> AidventoryIcons.Palette.imageVector
                SettingCategory.ABOUT -> AidventoryIcons.Info.imageVector
            }
            SettingListItem(
                isSelected = selectedSettingCategory == settingCategory,
                text = stringResource(settingCategory.textStringRes),
                description = stringResource(settingCategory.descriptionStringRes),
                icon = icon,
                onItemClick = { onItemClick(settingCategory) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingListItem(
    isSelected: Boolean,
    text: String,
    description: String,
    icon: ImageVector,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onItemClick,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}

@Composable
private fun DetailPane(
    selectedSettingCategory: SettingCategory?,
    modifier: Modifier = Modifier,
) {
    when (selectedSettingCategory) {
        SettingCategory.DATA -> DataScreen()
        SettingCategory.SUPPLY_USES -> SupplyUsesScreen()
        SettingCategory.THEME -> ThemeScreen(modifier = modifier)
        SettingCategory.ABOUT -> AboutScreen()
        null -> EmptyDetailPane()
    }
}

@Composable
private fun EmptyDetailPane(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(top = 16.dp),
        text = stringResource(R.string.settings_empty_detail_pane_text)
    )
}