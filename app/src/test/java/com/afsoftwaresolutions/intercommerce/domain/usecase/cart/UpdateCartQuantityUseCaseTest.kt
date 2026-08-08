package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCartQuantityUseCaseTest {

    @Test
    fun `returns invalid quantity when quantity is zero or negative`() = runTest {
        val repository = FakeCartRepository()
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 1, quantity = 0)

        assertEquals(CartOperationResult.Failure(CartError.InvalidQuantity), result)
    }

    @Test
    fun `returns product not found when item is absent`() = runTest {
        val repository = FakeCartRepository()
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 99, quantity = 2)

        assertEquals(CartOperationResult.Failure(CartError.ProductNotFound), result)
    }

    @Test
    fun `returns quantity exceeds stock when quantity is above available stock`() = runTest {
        val repository = FakeCartRepository(
            initialItems = listOf(cartItem(productId = 7, quantity = 1, availableStock = 3))
        )
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 7, quantity = 4)

        assertEquals(CartOperationResult.Failure(CartError.QuantityExceedsStock), result)
    }

    @Test
    fun `maps DataError NotFound to CartError ProductNotFound`() = runTest {
        val repository = FakeCartRepository(
            initialItems = listOf(cartItem(productId = 5, quantity = 1, availableStock = 5)),
            updateResult = DataResult.Failure(DataError.NotFound)
        )
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 5, quantity = 2)

        assertEquals(CartOperationResult.Failure(CartError.ProductNotFound), result)
    }

    @Test
    fun `maps other data errors to CartError Data`() = runTest {
        val repository = FakeCartRepository(
            initialItems = listOf(cartItem(productId = 5, quantity = 1, availableStock = 5)),
            updateResult = DataResult.Failure(DataError.Database)
        )
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 5, quantity = 2)

        assertTrue(result is CartOperationResult.Failure)
        assertEquals(CartError.Data(DataError.Database), (result as CartOperationResult.Failure).error)
    }

    @Test
    fun `returns success when quantity update succeeds`() = runTest {
        val repository = FakeCartRepository(
            initialItems = listOf(cartItem(productId = 3, quantity = 1, availableStock = 5)),
            updateResult = DataResult.Success(Unit)
        )
        val useCase = UpdateCartQuantityUseCase(repository)

        val result = useCase(productId = 3, quantity = 4)

        assertEquals(CartOperationResult.Success(Unit), result)
    }

    private fun cartItem(productId: Int, quantity: Int, availableStock: Int): CartItem {
        return CartItem(
            productId = productId,
            title = "Producto $productId",
            thumbnail = "",
            unitPrice = Money.ofCents(500),
            discountPercentage = Percentage.ZERO,
            quantity = quantity,
            availableStock = availableStock
        )
    }

    private class FakeCartRepository(
        initialItems: List<CartItem> = emptyList(),
        private val updateResult: DataResult<Unit> = DataResult.Success(Unit)
    ) : CartRepository {

        private val items = MutableStateFlow(initialItems)

        override fun observeCartItems(): Flow<List<CartItem>> = items

        override suspend fun upsertCartItem(item: CartItem): DataResult<Unit> {
            return DataResult.Success(Unit)
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int): DataResult<Unit> {
            return updateResult
        }

        override suspend fun removeCartItem(productId: Int): DataResult<Unit> {
            return DataResult.Success(Unit)
        }

        override suspend fun clearCart(): DataResult<Unit> {
            return DataResult.Success(Unit)
        }
    }
}

