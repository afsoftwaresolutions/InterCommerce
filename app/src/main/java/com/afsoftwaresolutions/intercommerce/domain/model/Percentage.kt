package com.afsoftwaresolutions.intercommerce.domain.model

@JvmInline
value class Percentage private constructor(val basisPoints: Int) {

    companion object {
        private const val MIN_BASIS_POINTS = 0
        private const val MAX_BASIS_POINTS = 10_000

        val ZERO: Percentage = Percentage(0)

        fun ofBasisPoints(basisPoints: Int): Percentage {
            require(basisPoints in MIN_BASIS_POINTS..MAX_BASIS_POINTS) {
                "Basis points debe estar en un rango entre 0..10_000"
            }
            return Percentage(basisPoints)
        }
    }
}