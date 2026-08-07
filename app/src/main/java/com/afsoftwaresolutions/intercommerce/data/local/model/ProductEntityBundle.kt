package com.afsoftwaresolutions.intercommerce.data.local.model

import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity

data class ProductEntityBundle(
    val product: ProductEntity,
    val tags: List<ProductTagEntity>,
    val images: List<ProductImageEntity>,
    val reviews: List<ProductReviewEntity>
)