package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository

class RemoveCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Int): CartOperationResult<Unit> {
        if (productId <= 0) {
            return CartOperationResult.Failure(CartError.ProductNotFound)
        }

        return when (val result = cartRepository.removeCartItem(productId)) {
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