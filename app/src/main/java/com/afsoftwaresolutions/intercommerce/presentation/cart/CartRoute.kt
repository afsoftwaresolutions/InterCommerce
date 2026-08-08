package com.afsoftwaresolutions.intercommerce.presentation.cart

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartRoute(
    onBackClick: () -> Unit,
    onContinueShopping: () -> Unit,
    onProductClick: (Int) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var transientError by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(viewModel.events, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is CartUiEvent.Error -> {
                        transientError = event.message
                        snackbarHostState.showSnackbar(event.message.resolve(context))
                    }

                    is CartUiEvent.ItemRemoved -> {
                        transientError = null
                        snackbarHostState.showSnackbar(event.message.resolve(context))
                    }

                    is CartUiEvent.CartCleared -> {
                        transientError = null
                        snackbarHostState.showSnackbar(event.message.resolve(context))
                    }
                }
            }
        }
    }

    CartScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinueShopping = onContinueShopping,
        onProductClick = onProductClick,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onDecreaseQuantity = viewModel::decreaseQuantity,
        onRemoveItem = viewModel::removeItem,
        onClearCart = viewModel::clearCart,
        onRetry = { transientError = null },
        snackbarHostState = snackbarHostState,
        transientError = transientError
    )
}

private fun UiText.resolve(context: android.content.Context): String {
    return when (this) {
        is UiText.StringResource -> context.getString(resourceId)
    }
}