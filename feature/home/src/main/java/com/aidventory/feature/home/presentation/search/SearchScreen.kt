package com.aidventory.feature.home.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.Supply
import com.aidventory.feature.home.R
import com.aidventory.feature.supplies.navigation.navigateToSuppliesScreen

@Composable
internal fun SearchScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreenContent(
        widthSizeClass = windowSizeClass.widthSizeClass,
        state = state,
        onQueryChanged = viewModel::inputQuery,
        onSupplyClick = { barcode ->
            navController.navigateToSuppliesScreen(barcode)
        },
        onNavigationClick = navController::navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenContent(
    widthSizeClass: WindowWidthSizeClass,
    state: SearchUiState,
    onQueryChanged: (query: String) -> Unit,
    onSupplyClick: (barcode: String) -> Unit,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AidventoryTopAppBar(
            onNavigationClick = onNavigationClick,
            navigationIcon = AidventoryIcons.ArrowBack.imageVector
        )
        Column(
            modifier = Modifier.fillMaxWidth(
                if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.5f else 1f
            )
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                query = state.query,
                onQueryChanged = onQueryChanged
            )

            if (state.query.isNotBlank()) {
                SearchResultMessage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    numberOfSupplies = state.supplies.size
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Supplies(
                modifier = Modifier.fillMaxWidth(),
                supplies = state.supplies,
                onSupplyClick = onSupplyClick
            )
        }
    }
}

@Composable
private fun Supplies(
    supplies: List<Supply>,
    onSupplyClick: (barcode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = supplies,
            key = { _, item -> item.barcode }
        ) { index, item ->
            val applyNavigationBarsPadding = index == supplies.lastIndex && supplies.size > 1
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSupplyClick(item.barcode) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .then(if (applyNavigationBarsPadding) Modifier.navigationBarsPadding() else Modifier),
                text = item.name
            )
        }
    }
}

@Composable
private fun SearchResultMessage(
    numberOfSupplies: Int,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = pluralStringResource(
            R.plurals.search_found_supplies_text,
            numberOfSupplies,
            numberOfSupplies
        ),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (query: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        maxLines = 1,
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
            Text(text = stringResource(R.string.search_text_field_placeholder))
        },
        leadingIcon = {
            Icon(
                imageVector = AidventoryIcons.Search.imageVector,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { onQueryChanged("") }) {
                Icon(
                    imageVector = AidventoryIcons.Close.imageVector,
                    contentDescription = stringResource(R.string.search_clear_input)
                )
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}