package com.aidventory.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.feature.home.presentation.home.HomeScreenContent
import com.aidventory.feature.home.presentation.home.HomeUiState
import com.aidventory.feature.home.util.HomeScreenTestTags
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun initialState_shouldShowProgressIndicator() {

        val state = mutableStateOf(HomeUiState())
        rule.setContent {
            AidventoryTheme {
                HomeScreenContent(
                    windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp)),
                    state = state.value,
                    onSettingClick = {},
                    onSearchClick = {},
                    onSuppliesClick = {},
                    onContainersClick = {},
                    onAddSupplyClick = {},
                    onAddContainerClick = {}
                )
            }
        }
        rule.onAllNodesWithTag(HomeScreenTestTags.CIRCULAR_PROGRESS_INDICATOR)
            .assertCountEquals(2)

        // Update state
        state.value = state.value.copy(isSuppliesLoading = false, isContainersLoading = false)
        rule.onAllNodesWithTag(HomeScreenTestTags.CIRCULAR_PROGRESS_INDICATOR)
            .assertCountEquals(0)
        rule.onNodeWithText(rule.activity.getString(
//            R.string.home_supplies_empty_text
        1
        ))
            .assertExists()
    }
}