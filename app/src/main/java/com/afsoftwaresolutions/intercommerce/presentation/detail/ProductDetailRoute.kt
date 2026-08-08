package com.afsoftwaresolutions.intercommerce.presentation.detail

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductDetailRoute(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.events, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is ProductDetailUiEvent.ProductAdded -> {
                        snackbarHostState.showSnackbar(event.message.resolve(context))
                        view.performHapticFeedback(productAddedHapticConstant(Build.VERSION.SDK_INT))
                    }

                    is ProductDetailUiEvent.Error -> {
                        snackbarHostState.showSnackbar(event.message.resolve(context))
                    }
                }
            }
        }
    }

    ProductDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        cartItemCount = cartItemCount,
        onAddToCartClick = viewModel::addToCart,
        onRetryClick = viewModel::retry,
        snackbarHostState = snackbarHostState
    )
}

private fun UiText.resolve(context: android.content.Context): String {
    return when (this) {
        is UiText.StringResource -> context.getString(resourceId)
    }
}

internal fun productAddedHapticConstant(sdkInt: Int): Int {
    return if (sdkInt >= 30) {
        HapticFeedbackConstants.CONFIRM
    } else {
        HapticFeedbackConstants.VIRTUAL_KEY
    }
}