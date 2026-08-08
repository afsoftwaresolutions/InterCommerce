package com.afsoftwaresolutions.intercommerce.presentation.detail

import com.afsoftwaresolutions.intercommerce.presentation.common.UiText

sealed interface ProductDetailUiEvent {
    data class ProductAdded(val message: UiText) : ProductDetailUiEvent
    data class Error(val message: UiText) : ProductDetailUiEvent
}