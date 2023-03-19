package com.aidventory.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aidventory.core.domain.model.Theme
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.presentation.onboarding.OnboardingScreen
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // Collect the ui state and assign it the compose mutable state.
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    uiState = it
                }
            }
        }

        // Keep the splash screen shown to the user until the ui state is loaded.
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        // Turn off the decor fitting system windows.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val darkTheme = shouldUseDarkTheme(uiState)
            val shouldHideOnboarding = rememberShouldHideOnboarding(uiState)
            val expiredBadgeValue = rememberExpiredBadgeValue(uiState)

            // Update the dark content of the system bars to match the theme.
            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !darkTheme
                onDispose { }
            }

            AidventoryTheme(darkTheme = darkTheme) {
                val windowSizeClass = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)

                // set portrait mode only for mobile
                requestedOrientation =
                    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }

                Surface(color = MaterialTheme.colorScheme.background) {
                    if (shouldHideOnboarding) {
                        AidventoryApp(
                            windowSizeClass = windowSizeClass,
                            displayFeatures = displayFeatures,
                            expiredBadgeValue = expiredBadgeValue
                        )
                    } else {
                        OnboardingScreen(widthSizeClass = windowSizeClass.widthSizeClass)
                    }
                }
            }
        }
    }

    @Composable
    private fun rememberShouldHideOnboarding(state: MainActivityUiState) = remember(state) {
        state is MainActivityUiState.Success && state.userPreferences.shouldHideOnboarding
    }

    @Composable
    private fun rememberExpiredBadgeValue(state: MainActivityUiState) = remember(state) {
        if (state is MainActivityUiState.Success) state.expiredBadgeValue else 0
    }

    @Composable
    private fun shouldUseDarkTheme(uiState: MainActivityUiState) = when (uiState) {
        MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success -> when (uiState.userPreferences.theme) {
            Theme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            Theme.LIGHT -> false
            Theme.DARK -> true
        }
    }
}