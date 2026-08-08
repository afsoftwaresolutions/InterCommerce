package com.afsoftwaresolutions.intercommerce.presentation.cart

import com.afsoftwaresolutions.intercommerce.domain.model.CartOverview
import com.afsoftwaresolutions.intercommerce.domain.model.CartTotals
import com.afsoftwaresolutions.intercommerce.domain.model.Money

data class CartUiState(
    val overview: CartOverview = CartOverview(
        items = emptyList(),
        totals = CartTotals(
            subtotal = Money.ZERO,
            discount = Money.ZERO,
            subtotalAfterDiscount = Money.ZERO,
            tax = Money.ZERO,
            total = Money.ZERO,
            totalUnits = 0
        )
    ),
    val isUpdating: Boolean = false
)