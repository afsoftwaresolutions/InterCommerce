package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.first

class UpdateCartQuantityUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Int, quantity: Int): CartOperationResult<Unit> {
        if (quantity <= 0) {
            return CartOperationResult.Failure(CartError.InvalidQuantity)
        }

        val currentItem = cartRepository.observeCartItems().first().firstOrNull { it.productId == productId }
            ?: return CartOperationResult.Failure(CartError.ProductNotFound)

        if (quantity > currentItem.availableStock) {
            return CartOperationResult.Failure(CartError.QuantityExceedsStock)
        }

        return when (val result = cartRepository.updateQuantity(productId, quantity)) {
            is DataResult.Success -> CartOperationResult.Success(Unit)
            is DataResult.Failure -> {
                if (result.error == DataError.NotFound) {
                    CartOperationResult.Failure(CartError.ProductNotFound)
                } else {
                    CartOperationResult.Failure(CartError.Data(result.error))
                }
            }
        }
    }
}