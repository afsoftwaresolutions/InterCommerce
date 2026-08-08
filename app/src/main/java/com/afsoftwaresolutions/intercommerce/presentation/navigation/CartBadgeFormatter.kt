package com.afsoftwaresolutions.intercommerce.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.afsoftwaresolutions.intercommerce.R

internal fun formatCartBadgeCount(count: Int): String {
    return when {
        count <= 0 -> ""
        count > 99 -> "99+"
        else -> count.toString()
    }
}

@Composable
internal fun cartButtonContentDescription(count: Int): String {
    return when {
        count <= 0 -> stringResource(R.string.cart_open_empty_description)
        count == 1 -> stringResource(R.string.cart_open_one_description)
        else -> pluralStringResource(R.plurals.cart_open_many_description, count, count)
    }
}