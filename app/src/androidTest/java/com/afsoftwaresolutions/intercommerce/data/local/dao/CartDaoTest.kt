package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import com.afsoftwaresolutions.intercommerce.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        cartDao = database.cartDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndObserve_returnsItemsOrderedByUpdatedAtDescThenIdAsc() = runBlocking {
        cartDao.upsertCartItem(cartItem(productId = 2, updatedAt = 1_000L))
        cartDao.upsertCartItem(cartItem(productId = 1, updatedAt = 1_000L))
        cartDao.upsertCartItem(cartItem(productId = 3, updatedAt = 2_000L))

        val observed = cartDao.observeCartItems().first()

        assertEquals(listOf(1, 2, 3), observed.map { it.productId })
    }

    @Test
    fun updateQuantity_updatesRowAndTimestamp() = runBlocking {
        cartDao.upsertCartItem(cartItem(productId = 8, quantity = 1, updatedAt = 100L))

        val rows = cartDao.updateQuantity(productId = 8, quantity = 4, updatedAtEpochMillis = 777L)
        val updated = cartDao.getCartItem(8)

        assertEquals(1, rows)
        assertEquals(4, updated?.quantity)
        assertEquals(777L, updated?.updatedAtEpochMillis)
    }

    @Test
    fun deleteAndClear_affectCount() = runBlocking {
        cartDao.upsertCartItem(cartItem(productId = 1))
        cartDao.upsertCartItem(cartItem(productId = 2))

        val deletedRows = cartDao.deleteCartItem(productId = 1)
        val countAfterDelete = cartDao.countCartItems()

        cartDao.clearCart()
        val countAfterClear = cartDao.countCartItems()

        assertEquals(1, deletedRows)
        assertEquals(1, countAfterDelete)
        assertEquals(0, countAfterClear)
    }

    private fun cartItem(
        productId: Int,
        quantity: Int = 1,
        updatedAt: Long = 10L
    ): CartItemEntity {
        return CartItemEntity(
            productId = productId,
            title = "Producto $productId",
            thumbnail = "",
            unitPriceCents = 500,
            discountBasisPoints = 0,
            quantity = quantity,
            availableStock = 10,
            updatedAtEpochMillis = updatedAt
        )
    }
}

