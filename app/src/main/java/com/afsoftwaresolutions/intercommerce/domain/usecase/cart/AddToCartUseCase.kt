package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.toCartOperationUnitResult
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AddToCartUseCase(
    private val cartRepository: CartRepository
) {
    private val mutex = Mutex()

    suspend operator fun invoke(product: Product): CartOperationResult<Unit> {
        if (product.stock <= 0) {
            return CartOperationResult.Failure(CartError.OutOfStock)
        }

        return try {
            mutex.withLock {
                val currentItems = cartRepository.observeCartItems().first()
                val existingItem = currentItems.firstOrNull { it.productId == product.id }
                val newQuantity = (existingItem?.quantity ?: 0) + 1

                if (newQuantity > product.stock) {
                    return@withLock CartOperationResult.Failure(CartError.QuantityExceedsStock)
                }

                val cartItem = CartItem(
                    productId = product.id,
                    title = product.title,
                    thumbnail = product.thumbnail,
                    unitPrice = product.price,
                    discountPercentage = product.discountPercentage,
                    quantity = newQuantity,
                    availableStock = product.stock
                )

                cartRepository.upsertCartItem(cartItem).toCartOperationUnitResult()
            }
        } catch (e: CancellationException) {
            throw e
        }
    }
}