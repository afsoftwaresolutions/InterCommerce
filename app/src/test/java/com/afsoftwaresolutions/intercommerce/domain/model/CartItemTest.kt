package com.afsoftwaresolutions.intercommerce.domain.model

import org.junit.Assert.assertThrows
import org.junit.Test

class CartItemTest {

    @Test
    fun `throws when productId is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            cartItem(productId = 0)
        }
    }

    @Test
    fun `throws when title is blank`() {
        assertThrows(IllegalArgumentException::class.java) {
            cartItem(title = " ")
        }
    }

    @Test
    fun `throws when quantity is zero or negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            cartItem(quantity = 0)
        }
    }

    @Test
    fun `throws when availableStock is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            cartItem(availableStock = -1)
        }
    }

    private fun cartItem(
        productId: Int = 1,
        title: String = "Producto",
        quantity: Int = 1,
        availableStock: Int = 5
    ): CartItem {
        return CartItem(
            productId = productId,
            title = title,
            thumbnail = "",
            unitPrice = Money.ofCents(100),
            discountPercentage = Percentage.ZERO,
            quantity = quantity,
            availableStock = availableStock
        )
    }
}

