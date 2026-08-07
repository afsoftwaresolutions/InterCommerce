package com.afsoftwaresolutions.intercommerce.data.repository

import com.afsoftwaresolutions.intercommerce.core.time.TimeProvider
import com.afsoftwaresolutions.intercommerce.data.local.dao.CartDao
import com.afsoftwaresolutions.intercommerce.data.local.util.safeDatabaseCall
import com.afsoftwaresolutions.intercommerce.data.mapper.toDomain
import com.afsoftwaresolutions.intercommerce.data.mapper.toEntity
import com.afsoftwaresolutions.intercommerce.di.qualifier.IoDispatcher
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val timeProvider: TimeProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CartRepository {

    override fun observeCartItems(): Flow<List<CartItem>> {
        return cartDao.observeCartItems().map { items ->
            items.map { it.toDomain() }
        }
    }

    override suspend fun upsertCartItem(item: CartItem): DataResult<Unit> {
        val result = safeDatabaseCall(ioDispatcher) {
            cartDao.upsertCartItem(
                item.toEntity(
                    updatedAtEpochMillis = timeProvider.currentTimeMillis()
                )
            )
        }
        return when (result) {
            is DataResult.Success -> DataResult.Success(Unit)
            is DataResult.Failure -> DataResult.Failure(result.error)
        }
    }

    override suspend fun updateQuantity(
        productId: Int,
        quantity: Int
    ): DataResult<Unit> {
        if (quantity <= 0) {
            return DataResult.Failure(DataError.Unknown)
        }

        val result = safeDatabaseCall(ioDispatcher) {
            cartDao.updateQuantity(
                productId = productId,
                quantity = quantity,
                updatedAtEpochMillis = timeProvider.currentTimeMillis()
            )
        }

        return when (result) {
            is DataResult.Failure -> DataResult.Failure(result.error)
            is DataResult.Success -> {
                if (result.data == 0) {
                    DataResult.Failure(DataError.NotFound)
                } else {
                    DataResult.Success(Unit)
                }
            }
        }
    }

    override suspend fun removeCartItem(productId: Int): DataResult<Unit> {
        val result = safeDatabaseCall(ioDispatcher) {
            cartDao.deleteCartItem(productId)
        }

        return when (result) {
            is DataResult.Failure -> DataResult.Failure(result.error)
            is DataResult.Success -> {
                if (result.data == 0) {
                    DataResult.Failure(DataError.NotFound)
                } else {
                    DataResult.Success(Unit)
                }
            }
        }
    }

    override suspend fun clearCart(): DataResult<Unit> {
        val result = safeDatabaseCall(ioDispatcher) {
            cartDao.clearCart()
        }
        return when (result) {
            is DataResult.Success -> DataResult.Success(Unit)
            is DataResult.Failure -> DataResult.Failure(result.error)
        }
    }

}