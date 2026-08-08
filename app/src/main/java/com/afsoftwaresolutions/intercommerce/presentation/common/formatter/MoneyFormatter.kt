package com.afsoftwaresolutions.intercommerce.presentation.common.formatter

import com.afsoftwaresolutions.intercommerce.domain.model.Money
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {
    fun format(money: Money): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            currency = Currency.getInstance("USD")
        }
        val amount = BigDecimal.valueOf(money.cents).movePointLeft(2)
        return formatter.format(amount)
    }
}