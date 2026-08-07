package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.CartTotals
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage

class CalculateCartTotalsUseCase(
    private val taxRate: Percentage
) {
    operator fun invoke(items: List<CartItem>): CartTotals {
        if (items.isEmpty()) {
            return CartTotals(
                subtotal = Money.ZERO,
                discount = Money.ZERO,
                subtotalAfterDiscount = Money.ZERO,
                tax = Money.ZERO,
                total = Money.ZERO,
                totalUnits = 0
            )
        }

        val subtotal = items.fold(Money.ZERO) { acc, item ->
            acc + item.unitPrice.multiply(item.quantity)
        }

        val discount = items.fold(Money.ZERO) { acc, item ->
            val lineSubtotal = item.unitPrice.multiply(item.quantity)
            acc + lineSubtotal.percentageAmount(item.discountPercentage)
        }

        val subtotalAfterDiscount = subtotal - discount
        val tax = subtotalAfterDiscount.percentageAmount(taxRate)
        val total = subtotalAfterDiscount + tax
        val totalUnits = items.sumOf { it.quantity }

        return CartTotals(
            subtotal = subtotal,
            discount = discount,
            subtotalAfterDiscount = subtotalAfterDiscount,
            tax = tax,
            total = total,
            totalUnits = totalUnits
        )
    }
}