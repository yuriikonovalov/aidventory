package com.aidventory.feature.home.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.feature.containers.navigation.navigateToAddContainerDialog
import com.aidventory.feature.containers.navigation.navigateToAddContainerScreen
import com.aidventory.feature.containers.navigation.navigateToContainersGraph
import com.aidventory.feature.home.R
import com.aidventory.feature.home.navigation.navigateToSearchScreen
import com.aidventory.feature.home.util.HomeScreenTestTags
import com.aidventory.feature.settings.navigation.navigateToSettingsGraph
import com.aidventory.feature.supplies.navigation.navigateToAddSupplyScreen
import com.aidventory.feature.supplies.navigation.navigateToSuppliesGraph

@Composable
internal fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val onAddContainerClick = remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass.getOnAddClickAction(
            navController::navigateToAddContainerScreen, navController::navigateToAddContainerDialog
        )
    }

    HomeScreenContent(
        windowSizeClass = windowSizeClass,
        state = state,
        modifier = Modifier,
        onSettingClick = navController::navigateToSettingsGraph,
        onSearchClick = navController::navigateToSearchScreen,
        onSuppliesClick = navController::navigateToSuppliesGraph,
        onContainersClick = navController::navigateToContainersGraph,
        onAddSupplyClick = navController::navigateToAddSupplyScreen,
        onAddContainerClick = onAddContainerClick
    )
}

private fun WindowWidthSizeClass.getOnAddClickAction(
    onNavigateToAddContainerScreen: () -> Unit,
    onNavigateToAddContainerDialog: () -> Unit,
): () -> Unit {
    // If the current screen's width class is Compact (phone), then we open a full screen
    // for adding a container. When the screen width is larger than Compact, then we open
    // a dialog for adding a container.
    //
    // The decision is made based on the fact that there's only one input field on the screen
    // and a button so it won't be user-friendly to open a full screen for large devices.
    return if (this == WindowWidthSizeClass.Compact) {
        onNavigateToAddContainerScreen
    } else {
        onNavigateToAddContainerDialog
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    windowSizeClass: WindowSizeClass,
    state: HomeUiState,
    onSettingClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSuppliesClick: () -> Unit,
    onContainersClick: () -> Unit,
    onAddSupplyClick: () -> Unit,
    onAddContainerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopAppBar(
            windowSizeClass = windowSizeClass,
            title = stringResource(R.string.top_app_bar_title),
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingClick
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {

            Row(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                HomeSection(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_supplies_title),
                    emptyText = stringResource(R.string.home_supplies_empty_text),
                    isLoading = state.isSuppliesLoading,
                    sectionItems = state.supplies.map { it.name },
                    addButtonText = stringResource(R.string.home_supplies_add_text),
                    onClick = onSuppliesClick,
                    onAddButtonClick = onAddSupplyClick
                )

                Spacer(modifier = Modifier.width(32.dp))

                HomeSection(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_containers_title),
                    emptyText = stringResource(R.string.home_containers_empty_text),
                    isLoading = state.isContainersLoading,
                    sectionItems = state.containers.map { it.name },
                    addButtonText = stringResource(R.string.home_add_text),
                    onClick = onContainersClick,
                    onAddButtonClick = onAddContainerClick
                )
            }
        } else {

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                HomeSection(
                    title = stringResource(R.string.home_supplies_title),
                    emptyText = stringResource(R.string.home_supplies_empty_text),
                    isLoading = state.isSuppliesLoading,
                    sectionItems = state.supplies.map { it.name },
                    addButtonText = stringResource(R.string.home_supplies_add_text),
                    onClick = onSuppliesClick,
                    onAddButtonClick = onAddSupplyClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                HomeSection(
                    title = stringResource(R.string.home_containers_title),
                    emptyText = stringResource(R.string.home_containers_empty_text),
                    isLoading = state.isContainersLoading,
                    sectionItems = state.containers.map { it.name },
                    addButtonText = stringResource(R.string.home_add_text),
                    onClick = onContainersClick,
                    onAddButtonClick = onAddContainerClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSection(
    title: String,
    emptyText: String,
    isLoading: Boolean,
    sectionItems: List<String>,
    addButtonText: String,
    onClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listItems = sectionItems.take(3)
    val plusMore = sectionItems.size - 3

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = isLoading) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(24.dp)
                            .testTag(HomeScreenTestTags.CIRCULAR_PROGRESS_INDICATOR)
                    )
                } else {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClick
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (listItems.isEmpty()) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 32.dp),
                                    text = emptyText,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                listItems.forEachIndexed { index, item ->
                                    val lastItem = index == listItems.lastIndex
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        text = item,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!lastItem) {
                                        Divider(modifier = Modifier.fillMaxWidth())
                                    }

                                }
                                if (plusMore > 0) {
                                    Divider(modifier = Modifier.fillMaxWidth())
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        text = stringResource(R.string.home_more_text, plusMore)
                                    )
                                }
                            }

                        }
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onAddButtonClick
        ) {
            Icon(imageVector = AidventoryIcons.Add.imageVector, contentDescription = null)
            Text(text = addButtonText, maxLines = 1)
        }
    }
}