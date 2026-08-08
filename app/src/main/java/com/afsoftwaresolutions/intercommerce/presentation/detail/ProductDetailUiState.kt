package com.afsoftwaresolutions.intercommerce.presentation.detail

import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText

data class ProductDetailUiState(
    val product: Product? = null,
    val isRefreshing: Boolean = false,
    val isAddingToCart: Boolean = false,
    val error: UiText? = null
)