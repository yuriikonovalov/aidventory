package com.aidventory.feature.settings.presentation.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aidventory.feature.settings.R

@Composable
internal fun AboutScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.about_version))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.about_attribution))
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Online illustrations by Storyset: https://storyset.com/online"
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Technology illustrations by Storyset: https://storyset.com/technology"
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Medical illustrations by Storyset: https://storyset.com/medical"
        )
    }
}