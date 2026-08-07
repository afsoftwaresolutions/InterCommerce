package com.afsoftwaresolutions.intercommerce.domain.error

sealed interface CartError {
    data object OutOfStock : CartError
    data object InvalidQuantity : CartError
    data object QuantityExceedsStock : CartError
    data object ProductNotFound : CartError
    data class Data(val error: DataError) : CartError
}