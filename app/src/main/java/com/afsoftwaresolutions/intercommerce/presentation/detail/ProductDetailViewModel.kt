package com.afsoftwaresolutions.intercommerce.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.AddToCartUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.ObserveProductUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.RefreshProductUseCase
import com.afsoftwaresolutions.intercommerce.presentation.common.UiText
import com.afsoftwaresolutions.intercommerce.presentation.common.toUiText
import com.afsoftwaresolutions.intercommerce.presentation.navigation.ProductDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel private constructor(
    private val productId: Int,
    observeProductUseCase: ObserveProductUseCase,
    private val refreshProductUseCase: RefreshProductUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeProductUseCase: ObserveProductUseCase,
        refreshProductUseCase: RefreshProductUseCase,
        addToCartUseCase: AddToCartUseCase
    ) : this(
        productId = savedStateHandle.toRoute<ProductDetailRoute>().productId,
        observeProductUseCase = observeProductUseCase,
        refreshProductUseCase = refreshProductUseCase,
        addToCartUseCase = addToCartUseCase
    )

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductDetailUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailUiEvent> = _events.asSharedFlow()

    init {
        observeProductUseCase(productId)
            .let { productFlow ->
                viewModelScope.launch {
                    productFlow
                        .catch { throwable ->
                            if (throwable is CancellationException) throw throwable
                            handleUnexpectedFailure()
                        }
                        .collect { product ->
                            _uiState.update { current ->
                                current.copy(product = product)
                            }
                        }
                }
            }

        refreshProduct()
    }

    fun addToCart() {
        if (_uiState.value.isAddingToCart) {
            return
        }

        val currentProduct = _uiState.value.product ?: return

        _uiState.update { it.copy(isAddingToCart = true) }
        viewModelScope.launch {
            val result = addToCartUseCase(currentProduct)
            _uiState.update { it.copy(isAddingToCart = false) }

            when (result) {
                is CartOperationResult.Success -> {
                    _events.emit(
                        ProductDetailUiEvent.ProductAdded(
                            UiText.StringResource(R.string.message_product_added)
                        )
                    )
                }

                is CartOperationResult.Failure -> {
                    _events.emit(ProductDetailUiEvent.Error(result.error.toUiText()))
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        refreshProduct()
    }

    private fun refreshProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                when (val result = refreshProductUseCase(productId)) {
                    is DataResult.Success -> {
                        _uiState.update { it.copy(isRefreshing = false) }
                    }

                    is DataResult.Failure -> {
                        _uiState.update { current ->
                            current.copy(
                                isRefreshing = false,
                                error = result.error.toUiText()
                            )
                        }
                        if (_uiState.value.product == null) {
                            _events.emit(ProductDetailUiEvent.Error(result.error.toUiText()))
                        }
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Throwable) {
                handleUnexpectedFailure()
            }
        }
    }

    private suspend fun handleUnexpectedFailure() {
        val unknownError = UiText.StringResource(R.string.error_unknown)
        _uiState.update { current ->
            current.copy(
                isRefreshing = false,
                error = unknownError
            )
        }
        if (_uiState.value.product == null) {
            _events.emit(ProductDetailUiEvent.Error(unknownError))
        }
    }

    companion object {
        internal fun forTest(
            productId: Int,
            observeProductUseCase: ObserveProductUseCase,
            refreshProductUseCase: RefreshProductUseCase,
            addToCartUseCase: AddToCartUseCase
        ): ProductDetailViewModel {
            return ProductDetailViewModel(
                productId = productId,
                observeProductUseCase = observeProductUseCase,
                refreshProductUseCase = refreshProductUseCase,
                addToCartUseCase = addToCartUseCase
            )
        }
    }
}