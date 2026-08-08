package com.afsoftwaresolutions.intercommerce.presentation.catalog

import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText

data class CatalogUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Product> = emptyList(),
    val searchError: UiText? = null,
    val isSearchActive: Boolean = false
)