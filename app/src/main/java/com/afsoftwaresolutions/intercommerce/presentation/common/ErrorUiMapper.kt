package com.afsoftwaresolutions.intercommerce.presentation.common

import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.DataError

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.NoConnection -> UiText.StringResource(R.string.error_no_connection)
        DataError.Timeout -> UiText.StringResource(R.string.error_timeout)
        DataError.NotFound -> UiText.StringResource(R.string.error_not_found)
        is DataError.Http -> UiText.StringResource(R.string.error_http)
        is DataError.Server -> UiText.StringResource(R.string.error_server)
        DataError.Serialization -> UiText.StringResource(R.string.error_serialization)
        DataError.Database -> UiText.StringResource(R.string.error_database)
        DataError.Unknown -> UiText.StringResource(R.string.error_unknown)
    }
}

fun CartError.toUiText(): UiText {
    return when (this) {
        CartError.OutOfStock -> UiText.StringResource(R.string.error_out_of_stock)
        CartError.InvalidQuantity -> UiText.StringResource(R.string.error_invalid_quantity)
        CartError.QuantityExceedsStock -> UiText.StringResource(R.string.error_quantity_exceeds_stock)
        CartError.ProductNotFound -> UiText.StringResource(R.string.error_product_not_found)
        is CartError.Data -> error.toUiText()
    }
}