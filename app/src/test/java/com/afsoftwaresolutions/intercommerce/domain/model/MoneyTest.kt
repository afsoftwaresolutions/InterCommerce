package com.afsoftwaresolutions.intercommerce.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MoneyTest {

    @Test
    fun `ofCents throws when cents is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            Money.ofCents(-1)
        }
    }

    @Test
    fun `plus returns summed value`() {
        val result = Money.ofCents(1250) + Money.ofCents(750)

        assertEquals(Money.ofCents(2000), result)
    }

    @Test
    fun `plus throws on overflow`() {
        assertThrows(ArithmeticException::class.java) {
            Money.ofCents(Long.MAX_VALUE) + Money.ofCents(1)
        }
    }

    @Test
    fun `minus throws when result is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            Money.ofCents(100) - Money.ofCents(101)
        }
    }

    @Test
    fun `multiply throws when quantity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            Money.ofCents(100).multiply(-1)
        }
    }

    @Test
    fun `multiply throws on overflow`() {
        assertThrows(ArithmeticException::class.java) {
            Money.ofCents(Long.MAX_VALUE).multiply(2)
        }
    }

    @Test
    fun `percentageAmount rounds half up`() {
        val amount = Money.ofCents(999)

        val result = amount.percentageAmount(Percentage.ofBasisPoints(3_333))

        assertEquals(Money.ofCents(333), result)
    }

    @Test
    fun `percentageAmount is zero when percentage is zero`() {
        val result = Money.ofCents(9_999).percentageAmount(Percentage.ZERO)

        assertEquals(Money.ZERO, result)
    }
}

