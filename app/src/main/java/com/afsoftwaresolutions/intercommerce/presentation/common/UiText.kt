package com.afsoftwaresolutions.intercommerce.presentation.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class StringResource(
        @StringRes val resourceId: Int
    ) : UiText
}

@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.StringResource -> stringResource(resourceId)
    }
}