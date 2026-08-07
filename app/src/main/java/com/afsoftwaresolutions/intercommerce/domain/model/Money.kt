package com.afsoftwaresolutions.intercommerce.domain.model


@JvmInline
value class Money private constructor(val cents: Long) {

    operator fun plus(other: Money): Money {
        return ofCents(Math.addExact(cents, other.cents))
    }

    operator fun minus(other: Money): Money {
        val result = Math.subtractExact(cents, other.cents)
        require(result >= 0L) { "Money result cannot be negative" }
        return ofCents(result)
    }

    fun multiply(quantity: Int): Money {
        require(quantity >= 0) { "Quantity cannot be negative" }
        return ofCents(Math.multiplyExact(cents, quantity.toLong()))
    }

    fun percentageAmount(percentage: Percentage): Money {
        val multiplied = Math.multiplyExact(cents, percentage.basisPoints.toLong())
        val rounded = (multiplied + BASIS_POINTS_SCALE / 2L) / BASIS_POINTS_SCALE
        return ofCents(rounded)
    }

    companion object {
        private const val BASIS_POINTS_SCALE = 10_000L

        val ZERO: Money = Money(0L)

        fun ofCents(cents: Long): Money {
            require(cents >= 0L) { "Money no puede ser negativo" }
            return Money(cents)
        }
    }
}
