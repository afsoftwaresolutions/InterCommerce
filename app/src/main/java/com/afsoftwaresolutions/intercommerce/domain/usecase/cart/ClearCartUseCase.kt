package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.toCartOperationUnitResult
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository

class ClearCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): CartOperationResult<Unit> {
        return cartRepository.clearCart().toCartOperationUnitResult()
    }
}
