package com.afsoftwaresolutions.intercommerce.presentation.catalog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.Dimensions
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.model.Review
import com.afsoftwaresolutions.intercommerce.presentation.catalog.components.ProductCard
import com.afsoftwaresolutions.intercommerce.presentation.catalog.components.ProductCardSkeleton
import com.afsoftwaresolutions.intercommerce.presentation.common.toPagingUiText
import com.afsoftwaresolutions.intercommerce.presentation.components.EmptyState
import com.afsoftwaresolutions.intercommerce.presentation.components.FullScreenError
import com.afsoftwaresolutions.intercommerce.presentation.components.FullScreenLoading
import com.afsoftwaresolutions.intercommerce.presentation.navigation.cartButtonContentDescription
import com.afsoftwaresolutions.intercommerce.presentation.navigation.formatCartBadgeCount
import com.afsoftwaresolutions.intercommerce.ui.theme.InterCommerceTheme
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.emptyList

private enum class CatalogViewMode {
    GRID,
    LIST
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    uiState: CatalogUiState,
    products: LazyPagingItems<Product>,
    cartItemCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val cachedDataMessage = stringResource(R.string.cached_data_message)
    var viewMode by rememberSaveable { mutableStateOf(CatalogViewMode.GRID) }

    val refreshState = products.loadState.refresh
    val appendState = products.loadState.append
    val hasPagedItems = products.itemCount > 0
    val refreshError = (refreshState as? LoadState.Error)?.error
    val appendError = (appendState as? LoadState.Error)?.error

    LaunchedEffect(refreshError, hasPagedItems, uiState.isSearchActive) {
        if (!uiState.isSearchActive && refreshError != null && hasPagedItems) {
            snackbarHostState.showSnackbar(cachedDataMessage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.catalog_title)) },
                actions = {
                    val toggleContentDescription = if (viewMode == CatalogViewMode.GRID) {
                        stringResource(R.string.switch_to_list_view)
                    } else {
                        stringResource(R.string.switch_to_grid_view)
                    }
                    val toggleLabel = if (viewMode == CatalogViewMode.GRID) {
                        stringResource(R.string.view_mode_list_short)
                    } else {
                        stringResource(R.string.view_mode_grid_short)
                    }

                    IconButton(
                        modifier = Modifier
                            .semantics {
                                contentDescription = toggleContentDescription
                            }
                            .testTag("catalog_view_toggle"),
                        onClick = {
                            viewMode = if (viewMode == CatalogViewMode.GRID) {
                                CatalogViewMode.LIST
                            } else {
                                CatalogViewMode.GRID
                            }
                        }
                    ) {
                        Text(text = toggleLabel)
                    }
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                val badgeText = formatCartBadgeCount(cartItemCount)
                                if (badgeText.isNotEmpty()) {
                                    Badge { Text(text = badgeText) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = cartButtonContentDescription(cartItemCount)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        CatalogLayout(
            innerPadding = innerPadding,
            query = uiState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onClearSearch = onClearSearch
        ) {
            if (uiState.isSearchActive) {
                SearchContent(
                    uiState = uiState,
                    onProductClick = onProductClick,
                    onRetrySearch = { onSearchQueryChange(uiState.searchQuery) },
                    gridState = gridState,
                    viewMode = viewMode
                )
            } else {
                PagedCatalogContent(
                    products = products,
                    refreshState = refreshState,
                    appendState = appendState,
                    appendError = appendError,
                    hasPagedItems = hasPagedItems,
                    onProductClick = onProductClick,
                    onRetry = onRetry,
                    gridState = gridState,
                    viewMode = viewMode
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    uiState: CatalogUiState,
    onProductClick: (Int) -> Unit,
    onRetrySearch: () -> Unit,
    gridState: LazyGridState,
    viewMode: CatalogViewMode
) {
    val searchResults = uiState.searchResults
    when {
        uiState.isSearching -> {
            SkeletonGrid(
                gridState = gridState,
                count = 6,
                viewMode = viewMode
            )
        }

        uiState.searchError != null && searchResults.isEmpty() -> {
            FullScreenError(message = uiState.searchError, onRetry = onRetrySearch)
        }

        searchResults.isEmpty() -> {
            EmptyState(
                titleRes = R.string.search_empty_title,
                messageRes = R.string.search_empty_message
            )
        }

        else -> {
            if (viewMode == CatalogViewMode.GRID) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("catalog_search_grid"),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { it.id },
                        contentType = { "product" }
                    ) { product ->
                        ProductCard(
                            productId = product.id,
                            title = product.title,
                            thumbnail = product.thumbnail,
                            price = product.price,
                            discount = product.discountPercentage,
                            rating = product.rating,
                            stock = product.stock,
                            onClick = onProductClick
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("catalog_search_list"),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = searchResults.size,
                        key = { index -> searchResults[index].id },
                        contentType = { "product" }
                    ) { index ->
                        val product = searchResults[index]
                        ProductCard(
                            productId = product.id,
                            title = product.title,
                            thumbnail = product.thumbnail,
                            price = product.price,
                            discount = product.discountPercentage,
                            rating = product.rating,
                            stock = product.stock,
                            onClick = onProductClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PagedCatalogContent(
    products: LazyPagingItems<Product>,
    refreshState: LoadState,
    appendState: LoadState,
    appendError: Throwable?,
    hasPagedItems: Boolean,
    onProductClick: (Int) -> Unit,
    onRetry: () -> Unit,
    gridState: LazyGridState,
    viewMode: CatalogViewMode
) {
    if (refreshState is LoadState.Loading && !hasPagedItems) {
        FullScreenLoading()
        return
    }

    if (refreshState is LoadState.Error && !hasPagedItems) {
        FullScreenError(message = refreshState.error.toPagingUiText(), onRetry = onRetry)
        return
    }

    if (refreshState is LoadState.NotLoading && products.itemCount == 0) {
        EmptyState(
            titleRes = R.string.catalog_empty_title,
            messageRes = R.string.catalog_empty_message
        )
        return
    }

    if (viewMode == CatalogViewMode.GRID) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("catalog_paged_grid"),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = products.itemCount,
                key = { index -> products[index]?.id ?: "loading_$index" },
                contentType = { "product" }
            ) { index ->
                val product = products[index]
                if (product != null) {
                    ProductCard(
                        productId = product.id,
                        title = product.title,
                        thumbnail = product.thumbnail,
                        price = product.price,
                        discount = product.discountPercentage,
                        rating = product.rating,
                        stock = product.stock,
                        onClick = onProductClick
                    )
                } else {
                    ProductCardSkeleton()
                }
            }

            if (appendState is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .testTag("catalog_append_loading"),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (appendError != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FullScreenError(
                        message = appendError.toPagingUiText(),
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("catalog_paged_list"),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = products.itemCount,
            key = { index -> products[index]?.id ?: "loading_$index" },
            contentType = { "product" }
        ) { index ->
            val product = products[index]
            if (product != null) {
                ProductCard(
                    productId = product.id,
                    title = product.title,
                    thumbnail = product.thumbnail,
                    price = product.price,
                    discount = product.discountPercentage,
                    rating = product.rating,
                    stock = product.stock,
                    onClick = onProductClick
                )
            } else {
                ProductCardSkeleton()
            }
        }

        if (appendState is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .testTag("catalog_append_loading"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (appendError != null) {
            item {
                FullScreenError(
                    message = appendError.toPagingUiText(),
                    onRetry = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun CatalogLayout(
    innerPadding: PaddingValues,
    query: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("catalog_search_field"),
            placeholder = { Text(stringResource(R.string.search_products_hint)) },
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search_description)
                        )
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SkeletonGrid(
    gridState: LazyGridState,
    count: Int,
    viewMode: CatalogViewMode
) {
    if (viewMode == CatalogViewMode.GRID) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("catalog_skeleton_grid"),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count) {
                ProductCardSkeleton()
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("catalog_skeleton_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count) {
            ProductCardSkeleton()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardSkeletonPreview() {
    InterCommerceTheme {
        ProductCardSkeleton()
    }
}

@Preview(name = "Catalog Search Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun CatalogScreenSearchPreview() {
    val previewItems = listOf(
        Product(
            id = 1,
            title = "Essence Mascara Lash Princess",
            description = "Mascara",
            category = "beauty",
            price = Money.ofCents(999),
            discountPercentage = Percentage.ofBasisPoints(1_000),
            rating = 4.4,
            stock = 8,
            tags = emptyList(),
            brand = "Essence",
            sku = "SKU1",
            weight = 1,
            dimensions = Dimensions(1.0, 1.0, 1.0),
            warrantyInformation = "",
            shippingInformation = "",
            availabilityStatus = "In Stock",
            reviews = listOf(Review(5, "Great", "2026-01-01", "A", "a@x.com")),
            returnPolicy = "",
            minimumOrderQuantity = 1,
            images = emptyList(),
            thumbnail = ""
        )
    )

    val lazyItems = flowOf(PagingData.from(previewItems)).collectAsLazyPagingItems()

    InterCommerceTheme {
        CatalogScreen(
            uiState = CatalogUiState(
                searchQuery = "Mascara",
                isSearchActive = true,
                isSearching = false,
                searchResults = previewItems
            ),
            products = lazyItems,
            cartItemCount = 0,
            onSearchQueryChange = {},
            onClearSearch = {},
            onProductClick = {},
            onCartClick = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Catalog Search Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CatalogScreenSearchDarkPreview() {
    val previewItems = listOf(
        Product(
            id = 2,
            title = "Eyeshadow Palette with Mirror",
            description = "Palette",
            category = "beauty",
            price = Money.ofCents(1999),
            discountPercentage = Percentage.ZERO,
            rating = 4.0,
            stock = 0,
            tags = emptyList(),
            brand = "Essence",
            sku = "SKU2",
            weight = 1,
            dimensions = Dimensions(1.0, 1.0, 1.0),
            warrantyInformation = "",
            shippingInformation = "",
            availabilityStatus = "Out of Stock",
            reviews = listOf(Review(5, "Great", "2026-01-01", "A", "a@x.com")),
            returnPolicy = "",
            minimumOrderQuantity = 1,
            images = emptyList(),
            thumbnail = ""
        )
    )

    val lazyItems = flowOf(PagingData.from(previewItems)).collectAsLazyPagingItems()

    InterCommerceTheme {
        CatalogScreen(
            uiState = CatalogUiState(
                searchQuery = "Palette",
                isSearchActive = true,
                isSearching = false,
                searchResults = previewItems
            ),
            products = lazyItems,
            cartItemCount = 120,
            onSearchQueryChange = {},
            onClearSearch = {},
            onProductClick = {},
            onCartClick = {},
            onRetry = {}
        )
    }
}