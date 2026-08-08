package com.afsoftwaresolutions.intercommerce.presentation.detail

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.Dimensions
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.model.Review
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import com.afsoftwaresolutions.intercommerce.presentation.common.asString
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.MoneyFormatter
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.PercentageFormatter
import com.afsoftwaresolutions.intercommerce.presentation.components.FullScreenError
import com.afsoftwaresolutions.intercommerce.presentation.components.FullScreenLoading
import com.afsoftwaresolutions.intercommerce.presentation.components.shimmer
import com.afsoftwaresolutions.intercommerce.presentation.navigation.cartButtonContentDescription
import com.afsoftwaresolutions.intercommerce.presentation.navigation.formatCartBadgeCount
import com.afsoftwaresolutions.intercommerce.ui.theme.InterCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    cartItemCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onRetryClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val product = uiState.product

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("product_detail_screen"),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.product_detail_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.testTag("detail_cart_button")
                    ) {
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AddToCartBar(
                product = product,
                isAddingToCart = uiState.isAddingToCart,
                onAddToCartClick = onAddToCartClick
            )
        }
    ) { innerPadding ->
        when {
            product == null && uiState.isRefreshing -> {
                FullScreenLoading(
                    modifier = Modifier
                        .padding(innerPadding)
                        .testTag("detail_loading")
                )
            }

            product == null && uiState.error != null -> {
                FullScreenError(
                    message = uiState.error,
                    onRetry = onRetryClick,
                    modifier = Modifier
                        .padding(innerPadding)
                        .testTag("detail_error")
                )
            }

            product != null -> {
                ProductDetailContent(
                    product = product,
                    uiState = uiState,
                    innerPadding = innerPadding,
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    uiState: ProductDetailUiState,
    innerPadding: PaddingValues,
    onRetryClick: () -> Unit
) {
    val imageUrls = remember(product.thumbnail, product.images) {
        prepareProductImageUrls(product.thumbnail, product.images)
    }
    val pageCount = if (imageUrls.isEmpty()) 1 else imageUrls.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (uiState.error != null) {
            RetryMessage(
                error = uiState.error,
                onRetryClick = onRetryClick
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .testTag("product_image_pager")
        ) { page ->
            val contentDescription = if (pageCount > 1) {
                stringResource(R.string.product_image_page_description, page + 1, pageCount)
            } else {
                stringResource(R.string.product_image_generic_description)
            }

            val imageUrl = imageUrls.getOrNull(page)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { this.contentDescription = contentDescription }
            ) {
                if (imageUrl == null) {
                    LocalPlaceholderImage(contentDescription)
                } else {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .shimmer()
                            )
                        },
                        error = {
                            LocalPlaceholderImage(contentDescription)
                        }
                    )
                }
            }
        }

        if (pageCount > 1) {
            Text(
                text = stringResource(R.string.product_image_page_description, pagerState.currentPage + 1, pageCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!product.brand.isNullOrBlank() || product.category.isNotBlank()) {
            Text(
                text = listOfNotNull(product.brand, product.category.ifBlank { null }).joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = MoneyFormatter.format(product.price),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (product.discountPercentage.basisPoints > 0) {
            Text(
                text = stringResource(
                    R.string.product_discount_description,
                    PercentageFormatter.format(product.discountPercentage)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = stringResource(
                R.string.product_rating_description,
                String.format(java.util.Locale.US, "%.1f", product.rating)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = if (product.stock == 0) {
                stringResource(R.string.no_stock_short)
            } else {
                stringResource(R.string.product_units_available, product.stock)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (product.stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

        Text(
            text = stringResource(R.string.description_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (hasMeaningfulDimensions(product.dimensions)) {
            Text(
                text = stringResource(R.string.dimensions_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${product.dimensions.width} x ${product.dimensions.height} x ${product.dimensions.depth}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (product.reviews.isNotEmpty()) {
            Text(
                text = stringResource(R.string.reviews_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            product.reviews.take(3).forEach { review ->
                Text(
                    text = "• ${review.comment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RetryMessage(
    error: UiText,
    onRetryClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = error.asString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onRetryClick) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun AddToCartBar(
    product: Product?,
    isAddingToCart: Boolean,
    onAddToCartClick: () -> Unit
) {
    val noStock = product?.stock == 0
    val enabled = product != null && !isAddingToCart && !noStock

    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onAddToCartClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
                .testTag("add_to_cart_button")
        ) {
            if (isAddingToCart) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            val textRes = when {
                isAddingToCart -> R.string.adding_to_cart
                noStock -> R.string.no_stock_short
                else -> R.string.add_to_cart
            }
            Text(text = stringResource(textRes))
        }
    }
}

@Composable
private fun LocalPlaceholderImage(contentDescription: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier.height(64.dp)
        )
        Text(
            text = stringResource(R.string.image_load_error),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

private fun hasMeaningfulDimensions(dimensions: Dimensions): Boolean {
    return dimensions.width > 0.0 || dimensions.height > 0.0 || dimensions.depth > 0.0
}

@Preview(name = "Detalle disponible claro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ProductDetailAvailableLightPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(product = previewProduct(stock = 9)),
            cartItemCount = 0,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Detalle disponible oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailAvailableDarkPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(product = previewProduct(stock = 9)),
            cartItemCount = 12,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Sin existencias", showBackground = true)
@Composable
private fun ProductDetailNoStockPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(product = previewProduct(stock = 0)),
            cartItemCount = 2,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Agregando", showBackground = true)
@Composable
private fun ProductDetailAddingPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(
                product = previewProduct(stock = 10),
                isAddingToCart = true
            ),
            cartItemCount = 99,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Error sin producto", showBackground = true)
@Composable
private fun ProductDetailErrorPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(
                product = null,
                error = UiText.StringResource(R.string.error_no_connection)
            ),
            cartItemCount = 0,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Carga inicial", showBackground = true)
@Composable
private fun ProductDetailLoadingPreview() {
    InterCommerceTheme {
        ProductDetailScreen(
            uiState = ProductDetailUiState(product = null, isRefreshing = true),
            cartItemCount = 0,
            onBackClick = {},
            onCartClick = {},
            onAddToCartClick = {},
            onRetryClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

private fun previewProduct(stock: Int): Product {
    return Product(
        id = 1,
        title = "Producto 1",
        description = "The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects.",
        category = "beauty",
        price = Money.ofCents(999),
        discountPercentage = Percentage.ofBasisPoints(1_048),
        rating = 4.3,
        stock = stock,
        tags = emptyList(),
        brand = "Essence",
        sku = "RCH45Q1A",
        weight = 2,
        dimensions = Dimensions(23.17, 14.43, 28.01),
        warrantyInformation = "",
        shippingInformation = "",
        availabilityStatus = "In Stock",
        reviews = listOf(
            Review(2, "Very unhappy with my purchase!", "2024-05-23", "John Doe", "john.doe@x.com"),
            Review(4, "Very satisfied!", "2024-05-23", "Jane Doe", "jane.doe@x.com")
        ),
        returnPolicy = "",
        minimumOrderQuantity = 24,
        images = listOf(
            "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp",
            "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/2.webp"
        ),
        thumbnail = "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp"
    )
}