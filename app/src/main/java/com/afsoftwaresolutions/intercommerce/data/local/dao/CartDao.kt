package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.afsoftwaresolutions.intercommerce.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query(
        """
        SELECT * FROM cart_items
        ORDER BY productId ASC
        """
    )
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItem(productId: Int): CartItemEntity?

    @Upsert
    suspend fun upsertCartItem(item: CartItemEntity)

    @Query(
        """
        UPDATE cart_items
        SET quantity = :quantity,
            updatedAtEpochMillis = :updatedAtEpochMillis
        WHERE productId = :productId
        """
    )
    suspend fun updateQuantity(
        productId: Int,
        quantity: Int,
        updatedAtEpochMillis: Long
    ): Int

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: Int): Int

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM cart_items")
    suspend fun countCartItems(): Int

}