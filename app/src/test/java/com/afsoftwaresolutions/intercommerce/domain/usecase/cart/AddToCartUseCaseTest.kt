package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.error.CartError
import com.afsoftwaresolutions.intercommerce.domain.error.CartOperationResult
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.Dimensions
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.model.Review
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddToCartUseCaseTest {

    @Test
    fun `returns out of stock when product stock is zero`() = runTest {
        val repository = FakeCartRepository()
        val useCase = AddToCartUseCase(repository)

        val result = useCase(product(stock = 0))

        assertEquals(CartOperationResult.Failure(CartError.OutOfStock), result)
    }

    @Test
    fun `adds new item with quantity one`() = runTest {
        val repository = FakeCartRepository()
        val useCase = AddToCartUseCase(repository)

        val result = useCase(product(id = 10, stock = 5))

        assertEquals(CartOperationResult.Success(Unit), result)
        assertEquals(1, repository.items.value.size)
        assertEquals(1, repository.items.value.first().quantity)
    }

    @Test
    fun `increments quantity for existing item`() = runTest {
        val existing = cartItem(productId = 3, quantity = 1, availableStock = 5)
        val repository = FakeCartRepository(initialItems = listOf(existing))
        val useCase = AddToCartUseCase(repository)

        val result = useCase(product(id = 3, stock = 5))

        assertEquals(CartOperationResult.Success(Unit), result)
        assertEquals(2, repository.items.value.first().quantity)
    }

    @Test
    fun `returns quantity exceeds stock when adding above limit`() = runTest {
        val existing = cartItem(productId = 8, quantity = 3, availableStock = 3)
        val repository = FakeCartRepository(initialItems = listOf(existing))
        val useCase = AddToCartUseCase(repository)

        val result = useCase(product(id = 8, stock = 3))

        assertEquals(CartOperationResult.Failure(CartError.QuantityExceedsStock), result)
    }

    @Test
    fun `maps repository failure to CartError Data`() = runTest {
        val repository = FakeCartRepository(upsertResult = DataResult.Failure(DataError.Database))
        val useCase = AddToCartUseCase(repository)

        val result = useCase(product(id = 11, stock = 2))

        assertTrue(result is CartOperationResult.Failure)
        assertEquals(CartError.Data(DataError.Database), (result as CartOperationResult.Failure).error)
    }

    private fun product(id: Int = 1, stock: Int = 4): Product {
        return Product(
            id = id,
            title = "Producto $id",
            description = "Desc",
            category = "cat",
            price = Money.ofCents(1_000),
            discountPercentage = Percentage.ZERO,
            rating = 4.0,
            stock = stock,
            tags = emptyList(),
            brand = "Brand",
            sku = "SKU-$id",
            weight = 1,
            dimensions = Dimensions(1.0, 1.0, 1.0),
            warrantyInformation = "",
            shippingInformation = "",
            availabilityStatus = "In Stock",
            reviews = listOf(Review(5, "ok", "2026-01-01", "A", "a@x.com")),
            returnPolicy = "",
            minimumOrderQuantity = 1,
            images = emptyList(),
            thumbnail = ""
        )
    }

    private fun cartItem(productId: Int, quantity: Int, availableStock: Int): CartItem {
        return CartItem(
            productId = productId,
            title = "Producto $productId",
            thumbnail = "",
            unitPrice = Money.ofCents(1_000),
            discountPercentage = Percentage.ZERO,
            quantity = quantity,
            availableStock = availableStock
        )
    }

    private class FakeCartRepository(
        initialItems: List<CartItem> = emptyList(),
        private val upsertResult: DataResult<Unit> = DataResult.Success(Unit)
    ) : CartRepository {

        val items = MutableStateFlow(initialItems)

        override fun observeCartItems(): Flow<List<CartItem>> = items

        override suspend fun upsertCartItem(item: CartItem): DataResult<Unit> {
            if (upsertResult is DataResult.Failure) {
                return upsertResult
            }

            val updated = items.value.filterNot { it.productId == item.productId } + item
            items.value = updated.sortedBy { it.productId }
            return DataResult.Success(Unit)
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int): DataResult<Unit> {
            return DataResult.Success(Unit)
        }

        override suspend fun removeCartItem(productId: Int): DataResult<Unit> {
            return DataResult.Success(Unit)
        }

        override suspend fun clearCart(): DataResult<Unit> {
            items.value = emptyList()
            return DataResult.Success(Unit)
        }
    }
}

