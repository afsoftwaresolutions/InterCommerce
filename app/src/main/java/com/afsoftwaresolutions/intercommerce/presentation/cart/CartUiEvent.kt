package com.afsoftwaresolutions.intercommerce.presentation.cart

import com.afsoftwaresolutions.intercommerce.presentation.common.UiText

sealed interface CartUiEvent {
    data class Error(val message: UiText) : CartUiEvent
    data class ItemRemoved(val message: UiText) : CartUiEvent
    data class CartCleared(val message: UiText) : CartUiEvent
}