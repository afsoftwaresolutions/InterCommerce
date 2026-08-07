package com.afsoftwaresolutions.intercommerce.data.mapper

import com.afsoftwaresolutions.intercommerce.data.remote.dto.DimensionsDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ReviewDto
import com.afsoftwaresolutions.intercommerce.domain.model.Dimensions
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.model.Review
import java.math.BigDecimal
import java.math.RoundingMode

fun ProductDto.toDomain(): Product {
    require(id > 0) { "Product id debe ser mayor que 0" }
    require(price.isFinite() && price >= 0.0) { "El precio del producto debe ser finito y >= 0" }
    require(discountPercentage.isFinite() && discountPercentage in 0.0..100.0) {
        "El porcentaje de descuento debe ser finito y estar en 0.0..100.0"
    }
    require(rating.isFinite()) { "La calificación del producto debe ser finita" }
    require(stock >= 0) { "El stock del producto no puede ser negativo" }

    val cents = BigDecimal.valueOf(price)
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

    val basisPoints = BigDecimal.valueOf(discountPercentage)
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .intValueExact()

    return Product(
        id = id,
        title = title,
        description = description,
        category = category,
        price = Money.ofCents(cents),
        discountPercentage = Percentage.ofBasisPoints(basisPoints),
        rating = rating,
        stock = stock,
        tags = tags.toList(),
        brand = brand,
        sku = sku,
        weight = weight,
        dimensions = dimensions.toDomain(),
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        availabilityStatus = availabilityStatus,
        reviews = reviews.map { it.toDomain() }.toList(),
        returnPolicy = returnPolicy,
        minimumOrderQuantity = minimumOrderQuantity,
        images = images.toList(),
        thumbnail = thumbnail
    )
}

fun List<ProductDto>.toDomain(): List<Product> = map { it.toDomain() }

private fun DimensionsDto.toDomain(): Dimensions {
    return Dimensions(
        width = width,
        height = height,
        depth = depth
    )
}

private fun ReviewDto.toDomain(): Review {
    return Review(
        rating = rating,
        comment = comment,
        date = date,
        reviewerName = reviewerName,
        reviewerEmail = reviewerEmail
    )
}