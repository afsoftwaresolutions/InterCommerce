package com.afsoftwaresolutions.intercommerce.presentation.cart

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.CartOverview
import com.afsoftwaresolutions.intercommerce.domain.model.CartTotals
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.presentation.cart.components.CartItemCard
import com.afsoftwaresolutions.intercommerce.presentation.cart.components.CartSummary
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import com.afsoftwaresolutions.intercommerce.presentation.components.FullScreenError
import com.afsoftwaresolutions.intercommerce.ui.theme.InterCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onContinueShopping: () -> Unit,
    onProductClick: (Int) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearCart: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    transientError: UiText? = null
) {
    val items = uiState.overview.items
    val isEmpty = items.isEmpty()
    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen"),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.cart_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("cart_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                },
                actions = {
                    if (!isEmpty) {
                        TextButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.testTag("clear_cart_button")
                        ) {
                            Text(text = stringResource(R.string.clear_cart_action))
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            transientError != null && isEmpty -> {
                FullScreenError(
                    message = transientError,
                    onRetry = onRetry,
                    modifier = Modifier
                        .padding(innerPadding)
                        .testTag("cart_error")
                )
            }

            isEmpty && uiState.isUpdating -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("cart_loading"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            isEmpty -> {
                EmptyCartState(
                    onContinueShopping = onContinueShopping,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                CartContent(
                    items = items,
                    totals = uiState.overview.totals,
                    isUpdating = uiState.isUpdating,
                    onProductClick = onProductClick,
                    onIncreaseQuantity = onIncreaseQuantity,
                    onDecreaseQuantity = onDecreaseQuantity,
                    onRemoveItem = onRemoveItem,
                    innerPadding = innerPadding
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            modifier = Modifier.testTag("clear_cart_dialog"),
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = stringResource(R.string.clear_cart_dialog_title)) },
            text = { Text(text = stringResource(R.string.clear_cart_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearCart()
                    },
                    modifier = Modifier.testTag("confirm_clear_cart")
                ) {
                    Text(text = stringResource(R.string.clear_cart_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog = false },
                    modifier = Modifier.testTag("cancel_clear_cart")
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun EmptyCartState(
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("empty_cart"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.cart_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = stringResource(R.string.cart_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        Button(
            onClick = onContinueShopping,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.continue_shopping))
        }
    }
}

@Composable
private fun CartContent(
    items: List<CartItem>,
    totals: CartTotals,
    isUpdating: Boolean,
    onProductClick: (Int) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .testTag("cart_items_list"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = items,
            key = { it.productId }
        ) { item ->
            CartItemCard(
                productId = item.productId,
                title = item.title,
                thumbnail = item.thumbnail,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                stock = item.availableStock,
                itemSubtotal = null,
                isUpdating = isUpdating,
                onProductClick = onProductClick,
                onIncreaseClick = onIncreaseQuantity,
                onDecreaseClick = onDecreaseQuantity,
                onRemoveClick = onRemoveItem
            )
        }

        item {
            CartSummary(
                totals = totals,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Carrito vacio claro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun EmptyCartLightPreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = CartUiState(),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Carrito vacio oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyCartDarkPreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = CartUiState(),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Carrito con productos claro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun CartItemsLightPreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = previewCartState(),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Carrito con productos oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartItemsDarkPreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = previewCartState(),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Cantidad uno", showBackground = true)
@Composable
private fun CartQuantityOnePreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = previewCartState(quantity = 1, stock = 5),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Cantidad al stock", showBackground = true)
@Composable
private fun CartQuantityAtStockPreview() {
    InterCommerceTheme {
        CartScreen(
            uiState = previewCartState(quantity = 3, stock = 3),
            onBackClick = {},
            onContinueShopping = {},
            onProductClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {},
            onClearCart = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Dialogo vaciar visible", showBackground = true)
@Composable
private fun ClearDialogPreview() {
    var show by remember { mutableStateOf(true) }
    InterCommerceTheme {
        if (show) {
            AlertDialog(
                modifier = Modifier.testTag("clear_cart_dialog"),
                onDismissRequest = { show = false },
                title = { Text(text = stringResource(R.string.clear_cart_dialog_title)) },
                text = { Text(text = stringResource(R.string.clear_cart_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { show = false }) {
                        Text(text = stringResource(R.string.clear_cart_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { show = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

private fun previewCartState(
    quantity: Int = 2,
    stock: Int = 5
): CartUiState {
    val items = listOf(
        CartItem(
            productId = 1,
            title = "Producto 1",
            thumbnail = "",
            unitPrice = Money.ofCents(999),
            discountPercentage = Percentage.ofBasisPoints(3_000),
            quantity = quantity,
            availableStock = stock
        ),
        CartItem(
            productId = 2,
            title = "Producto 2",
            thumbnail = "",
            unitPrice = Money.ofCents(1999),
            discountPercentage = Percentage.ZERO,
            quantity = 1,
            availableStock = 10
        )
    )

    return CartUiState(
        overview = CartOverview(
            items = items,
            totals = CartTotals(
                subtotal = Money.ofCents(3997),
                discount = Money.ofCents(200),
                subtotalAfterDiscount = Money.ofCents(3797),
                tax = Money.ofCents(721),
                total = Money.ofCents(4518),
                totalUnits = quantity + 1
            )
        ),
        isUpdating = false
    )
}