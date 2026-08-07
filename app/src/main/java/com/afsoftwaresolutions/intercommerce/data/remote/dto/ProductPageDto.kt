package com.afsoftwaresolutions.intercommerce.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductPageDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
