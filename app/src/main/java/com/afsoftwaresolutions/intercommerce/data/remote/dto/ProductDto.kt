package com.afsoftwaresolutions.intercommerce.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val tags: List<String> = emptyList(),
    val brand: String? = null,
    val sku: String = "",
    val weight: Int = 0,
    val dimensions: DimensionsDto = DimensionsDto(),
    val warrantyInformation: String = "",
    val shippingInformation: String = "",
    val availabilityStatus: String = "",
    val reviews: List<ReviewDto> = emptyList(),
    val returnPolicy: String = "",
    val minimumOrderQuantity: Int = 1,
    val images: List<String> = emptyList(),
    val thumbnail: String = ""
)
