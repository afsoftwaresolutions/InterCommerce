package com.afsoftwaresolutions.intercommerce.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ClearCartUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ObserveCartOverviewUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.RemoveCartItemUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.UpdateCartQuantityUseCase
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import com.afsoftwaresolutions.intercommerce.presentation.common.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCartOverviewUseCase: ObserveCartOverviewUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val isUpdatingState = MutableStateFlow(false)

    val uiState: StateFlow<CartUiState> = combine(
        observeCartOverviewUseCase(),
        isUpdatingState
    ) { overview, isUpdating ->
        CartUiState(
            overview = overview,
            isUpdating = isUpdating
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState()
    )

    private val _events = MutableSharedFlow<CartUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CartUiEvent> = _events.asSharedFlow()

    fun increaseQuantity(productId: Int) {
        val currentItem = uiState.value.overview.items.firstOrNull { it.productId == productId } ?: return
        updateQuantity(productId, currentItem.quantity + 1)
    }

    fun decreaseQuantity(productId: Int) {
        val currentItem = uiState.value.overview.items.firstOrNull { it.productId == productId } ?: return
        if (currentItem.quantity <= 1) {
            return
        }
        updateQuantity(productId, currentItem.quantity - 1)
    }

    fun removeItem(productId: Int) {
        performUpdate {
            when (val result = removeCartItemUseCase(productId)) {
                is CartOperationResult.Success -> {
                    _events.emit(CartUiEvent.ItemRemoved(UiText.StringResource(R.string.message_cart_item_removed)))
                }

                is CartOperationResult.Failure -> {
                    _events.emit(CartUiEvent.Error(result.error.toUiText()))
                }
            }
        }
    }

    fun clearCart() {
        performUpdate {
            when (val result = clearCartUseCase()) {
                is CartOperationResult.Success -> {
                    _events.emit(CartUiEvent.CartCleared(UiText.StringResource(R.string.message_cart_cleared)))
                }

                is CartOperationResult.Failure -> {
                    _events.emit(CartUiEvent.Error(result.error.toUiText()))
                }
            }
        }
    }

    private fun updateQuantity(productId: Int, quantity: Int) {
        performUpdate {
            when (val result = updateCartQuantityUseCase(productId, quantity)) {
                is CartOperationResult.Success -> Unit
                is CartOperationResult.Failure -> {
                    _events.emit(CartUiEvent.Error(result.error.toUiText()))
                }
            }
        }
    }

    private fun performUpdate(block: suspend () -> Unit) {
        if (isUpdatingState.value) {
            return
        }

        isUpdatingState.value = true
        viewModelScope.launch {
            try {
                block()
            } finally {
                isUpdatingState.value = false
            }
        }
    }
}