package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateCartTotalsUseCaseTest {

    private val useCase = CalculateCartTotalsUseCase(taxRate = Percentage.ofBasisPoints(1_600))

    @Test
    fun `returns zeros for empty cart`() {
        val result = useCase(emptyList())

        assertEquals(Money.ZERO, result.subtotal)
        assertEquals(Money.ZERO, result.discount)
        assertEquals(Money.ZERO, result.subtotalAfterDiscount)
        assertEquals(Money.ZERO, result.tax)
        assertEquals(Money.ZERO, result.total)
        assertEquals(0, result.totalUnits)
    }

    @Test
    fun `calculates totals for mixed discounts`() {
        val items = listOf(
            cartItem(
                productId = 1,
                unitPriceCents = 1_000,
                quantity = 2,
                discountBasisPoints = 1_000
            ),
            cartItem(
                productId = 2,
                unitPriceCents = 250,
                quantity = 3,
                discountBasisPoints = 0
            )
        )

        val result = useCase(items)

        assertEquals(Money.ofCents(2_750), result.subtotal)
        assertEquals(Money.ofCents(200), result.discount)
        assertEquals(Money.ofCents(2_550), result.subtotalAfterDiscount)
        assertEquals(Money.ofCents(408), result.tax)
        assertEquals(Money.ofCents(2_958), result.total)
        assertEquals(5, result.totalUnits)
    }

    @Test
    fun `handles full discount with zero tax base`() {
        val items = listOf(
            cartItem(
                productId = 7,
                unitPriceCents = 1_500,
                quantity = 2,
                discountBasisPoints = 10_000
            )
        )

        val result = useCase(items)

        assertEquals(Money.ofCents(3_000), result.subtotal)
        assertEquals(Money.ofCents(3_000), result.discount)
        assertEquals(Money.ZERO, result.subtotalAfterDiscount)
        assertEquals(Money.ZERO, result.tax)
        assertEquals(Money.ZERO, result.total)
        assertEquals(2, result.totalUnits)
    }

    private fun cartItem(
        productId: Int,
        unitPriceCents: Long,
        quantity: Int,
        discountBasisPoints: Int
    ): CartItem {
        return CartItem(
            productId = productId,
            title = "Producto $productId",
            thumbnail = "",
            unitPrice = Money.ofCents(unitPriceCents),
            discountPercentage = Percentage.ofBasisPoints(discountBasisPoints),
            quantity = quantity,
            availableStock = 20
        )
    }
}

