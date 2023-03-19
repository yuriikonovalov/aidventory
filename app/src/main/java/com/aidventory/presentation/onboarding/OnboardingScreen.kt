package com.aidventory.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aidventory.R

@Composable
fun OnboardingScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val widthFraction = rememberWidthFraction(widthSizeClass)
    val padding = rememberOnboardingPadding(widthSizeClass)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OnboardingScreenContent(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight()
                .safeContentPadding()
                .padding(padding),
            state = viewModel.uiState,
            onNextClick = viewModel::next,
            onGetStartedClick = viewModel::getStarted
        )
    }
}

@Composable
private fun rememberWidthFraction(widthSizeClass: WindowWidthSizeClass) = remember(widthSizeClass) {
    if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.5f else 1f
}

@Composable
private fun rememberOnboardingPadding(widthSizeClass: WindowWidthSizeClass) =
    remember(widthSizeClass) {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
            WindowWidthSizeClass.Medium -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            else -> PaddingValues(horizontal = 48.dp, vertical = 16.dp)
        }
    }

@Composable
private fun OnboardingScreenContent(
    state: OnboardingUiState,
    onNextClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = state) { state ->
                when (state) {
                    OnboardingUiState.ManageSuppliesFeature -> {
                        OnboardingPage(
                            imageResId = R.drawable.il_first_aid_kit,
                            title = stringResource(R.string.onboarding_track_your_supplies_title),
                            message = stringResource(R.string.onboarding_track_your_supplies_text)
                        )
                    }

                    OnboardingUiState.ScannerFeature -> {
                        OnboardingPage(
                            imageResId = R.drawable.il_qr_code,
                            title = stringResource(R.string.onboarding_scanner_title),
                            message = stringResource(R.string.onboarding_scanner_text)
                        )
                    }

                    OnboardingUiState.NotificationFeature -> {
                        OnboardingPage(
                            imageResId = R.drawable.il_notification,
                            title = stringResource(R.string.onboarding_expiry_notification_title),
                            message = stringResource(R.string.onboarding_expiry_notification_text)
                        )
                    }
                }

            }
        }

        OnboardingPageNavigation(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            onNextClick = onNextClick,
            onGetStartedClick = onGetStartedClick
        )
    }
}

@Composable
private fun OnboardingPageNavigation(
    state: OnboardingUiState,
    onNextClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nextButtonAction =
        if (state is OnboardingUiState.NotificationFeature) onGetStartedClick else onNextClick

    val nextButtonText =
        if (state is OnboardingUiState.NotificationFeature) {
            stringResource(R.string.onboarding_button_get_started)
        } else {
            stringResource(R.string.onboarding_button_next)
        }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageIndicator(state = state)
        TextButton(onClick = nextButtonAction) {
            Text(text = nextButtonText)
        }
    }
}

@Composable
private fun PageIndicator(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        PageIndicatorDot(
            isActive = state is OnboardingUiState.ManageSuppliesFeature,
            colorActive = Color(0xFF92E3A9)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PageIndicatorDot(
            isActive = state is OnboardingUiState.ScannerFeature,
            colorActive = Color(0xFF407BFF)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PageIndicatorDot(
            isActive = state is OnboardingUiState.NotificationFeature,
            colorActive = Color(0xFFFFC100)
        )
    }
}

@Composable
private fun PageIndicatorDot(
    isActive: Boolean,
    colorActive: Color,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isActive) {
            colorActive
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        }
    )
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun OnboardingPage(
    imageResId: Int,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .sizeIn(maxWidth = 200.dp)
                .aspectRatio(1f),
            painter = painterResource(imageResId),
            contentDescription = title
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, textAlign = TextAlign.Center)
    }
}