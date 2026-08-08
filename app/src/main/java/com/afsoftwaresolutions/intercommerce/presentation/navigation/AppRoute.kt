package com.afsoftwaresolutions.intercommerce.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CatalogRoute

@Serializable
data class ProductDetailRoute(
    val productId: Int
)

@Serializable
data object CartRoute