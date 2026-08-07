package com.afsoftwaresolutions.intercommerce.domain.repository

import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun observeCartItems(): Flow<List<CartItem>>

    suspend fun upsertCartItem(item: CartItem): DataResult<Unit>

    suspend fun updateQuantity(productId: Int, quantity: Int): DataResult<Unit>

    suspend fun removeCartItem(productId: Int): DataResult<Unit>

    suspend fun clearCart(): DataResult<Unit>
}