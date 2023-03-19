package com.aidventory.feature.supplies.presentation.addsupply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.feature.supplies.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddSupplyTopAppBar(
    stepOrdinal: Int,
    totalSteps: Int,
    title: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = AidventoryIcons.ArrowBack.imageVector,
                        contentDescription = stringResource(R.string.top_app_bar_action_navigate_up)
                    )
                }
            },
            title = {
                StepTitle(step = stepOrdinal + 1, totalSteps = totalSteps, title = title)
            }
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = stepOrdinal.toFloat() / totalSteps.toFloat(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddSupplyExpandedTopAppBar(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {}
) {
    AidventoryTopAppBar(
        modifier = modifier,
        navigationIcon = AidventoryIcons.ArrowBack.imageVector,
        onNavigationClick = onNavigationClick
    )
}

@Composable
private fun StepTitle(
    step: Int,
    totalSteps: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_supply_top_app_bar_step, step, totalSteps),
            style = MaterialTheme.typography.labelSmall
        )
        Text(text = title)
    }
}

@Preview(device = Devices.PHONE)
@Composable
private fun AddSupplyTopAppBarPreview() {
    AidventoryTheme {
        AddSupplyTopAppBar(
            stepOrdinal = 3,
            totalSteps = 4,
            title = "Container",
            onNavigationClick = {}
        )
    }
}