package com.afsoftwaresolutions.intercommerce.presentation.common.formatter

import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import java.math.BigDecimal
import java.math.RoundingMode

object PercentageFormatter {
    fun format(percentage: Percentage): String {
        val value = BigDecimal.valueOf(percentage.basisPoints.toLong())
            .movePointLeft(2)
            .setScale(2, RoundingMode.UNNECESSARY)
            .stripTrailingZeros()
            .toPlainString()
        return "$value%"
    }
}