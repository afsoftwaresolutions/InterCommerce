package com.afsoftwaresolutions.intercommerce.domain.model


data class CartItem(
    val productId: Int,
    val title: String,
    val thumbnail: String,
    val unitPrice: Money,
    val discountPercentage: Percentage,
    val quantity: Int,
    val availableStock: Int
) {
    init {
        require(productId > 0) { "el producto debe ser mayor a 0" }
        require(title.isNotBlank()) { "el titulo no puede estar vacio" }
        require(quantity > 0) { "cantidad debe ser mayor a 0" }
        require(availableStock >= 0) { "availableStock no puede ser negativo" }
    }
}