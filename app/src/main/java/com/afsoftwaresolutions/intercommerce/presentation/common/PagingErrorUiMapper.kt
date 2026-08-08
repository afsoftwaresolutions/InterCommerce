package com.afsoftwaresolutions.intercommerce.presentation.common

import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.data.paging.PagingDataException

fun Throwable.toPagingUiText(): UiText {
    return when (this) {
        is PagingDataException -> error.toUiText()
        else -> UiText.StringResource(R.string.error_unknown)
    }
}