package com.afsoftwaresolutions.intercommerce.data.mapper

import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductEntityBundle
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductWithDetails
import com.afsoftwaresolutions.intercommerce.domain.model.Dimensions
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.model.Review

fun Product.toEntityBundle(cachedAtEpochMillis: Long): ProductEntityBundle {
    val entity = ProductEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        priceCents = price.cents,
        discountBasisPoints = discountPercentage.basisPoints,
        rating = rating,
        stock = stock,
        brand = brand,
        sku = sku,
        weight = weight,
        dimensionWidth = dimensions.width,
        dimensionHeight = dimensions.height,
        dimensionDepth = dimensions.depth,
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        availabilityStatus = availabilityStatus,
        returnPolicy = returnPolicy,
        minimumOrderQuantity = minimumOrderQuantity,
        thumbnail = thumbnail,
        cachedAtEpochMillis = cachedAtEpochMillis
    )

    val tagEntities = tags.mapIndexed { index, tag ->
        ProductTagEntity(productId = id, position = index, value = tag)
    }

    val imageEntities = images.mapIndexed { index, image ->
        ProductImageEntity(productId = id, position = index, url = image)
    }

    val reviewEntities = reviews.mapIndexed { index, review ->
        ProductReviewEntity(
            productId = id,
            position = index,
            rating = review.rating,
            comment = review.comment,
            date = review.date,
            reviewerName = review.reviewerName,
            reviewerEmail = review.reviewerEmail
        )
    }

    return ProductEntityBundle(
        product = entity,
        tags = tagEntities,
        images = imageEntities,
        reviews = reviewEntities
    )
}

fun ProductWithDetails.toDomain(): Product {
    val sortedTags = tags.sortedBy { it.position }
    val sortedImages = images.sortedBy { it.position }
    val sortedReviews = reviews.sortedBy { it.position }

    return Product(
        id = product.id,
        title = product.title,
        description = product.description,
        category = product.category,
        price = Money.ofCents(product.priceCents),
        discountPercentage = Percentage.ofBasisPoints(product.discountBasisPoints),
        rating = product.rating,
        stock = product.stock,
        tags = sortedTags.map { it.value }.toList(),
        brand = product.brand,
        sku = product.sku,
        weight = product.weight,
        dimensions = Dimensions(
            width = product.dimensionWidth,
            height = product.dimensionHeight,
            depth = product.dimensionDepth
        ),
        warrantyInformation = product.warrantyInformation,
        shippingInformation = product.shippingInformation,
        availabilityStatus = product.availabilityStatus,
        reviews = sortedReviews.map {
            Review(
                rating = it.rating,
                comment = it.comment,
                date = it.date,
                reviewerName = it.reviewerName,
                reviewerEmail = it.reviewerEmail
            )
        }.toList(),
        returnPolicy = product.returnPolicy,
        minimumOrderQuantity = product.minimumOrderQuantity,
        images = sortedImages.map { it.url }.toList(),
        thumbnail = product.thumbnail
    )
}
