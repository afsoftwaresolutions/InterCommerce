package com.afsoftwaresolutions.intercommerce.domain.model

data class CartTotals(
    val subtotal: Money,
    val discount: Money,
    val subtotalAfterDiscount: Money,
    val tax: Money,
    val total: Money,
    val totalUnits: Int
)
