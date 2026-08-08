package com.afsoftwaresolutions.intercommerce.presentation.catalog

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun CatalogRoute(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val products = viewModel.products.collectAsLazyPagingItems()

    CatalogScreen(
        uiState = uiState,
        products = products,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onClearSearch = viewModel::clearSearch,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        cartItemCount = cartItemCount,
        onRetry = products::retry
    )
}