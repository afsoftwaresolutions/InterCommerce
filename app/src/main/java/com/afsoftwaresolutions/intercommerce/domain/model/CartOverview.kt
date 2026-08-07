package com.afsoftwaresolutions.intercommerce.domain.model

data class CartOverview(
    val items: List<CartItem>,
    val totals: CartTotals
)

