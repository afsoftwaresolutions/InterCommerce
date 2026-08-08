package com.afsoftwaresolutions.intercommerce.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import com.afsoftwaresolutions.intercommerce.presentation.common.asString

@Composable
fun FullScreenError(
    message: UiText,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullScreenErrorContent(
        messageText = message.asString(),
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun FullScreenError(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullScreenErrorContent(
        messageText = stringResource(messageRes),
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
private fun FullScreenErrorContent(
    messageText: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("full_screen_error")
            .semantics { stateDescription = messageText },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = messageText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("full_screen_error_message")
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag("full_screen_error_retry")
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}