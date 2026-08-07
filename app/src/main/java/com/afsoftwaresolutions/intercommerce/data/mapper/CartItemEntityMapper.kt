package com.afsoftwaresolutions.intercommerce.data.mapper

import com.afsoftwaresolutions.intercommerce.data.local.entity.CartItemEntity
import com.afsoftwaresolutions.intercommerce.domain.model.CartItem
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage

fun CartItem.toEntity(updatedAtEpochMillis: Long): CartItemEntity {
    return CartItemEntity(
        productId = productId,
        title = title,
        thumbnail = thumbnail,
        unitPriceCents = unitPrice.cents,
        discountBasisPoints = discountPercentage.basisPoints,
        quantity = quantity,
        availableStock = availableStock,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
}

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        productId = productId,
        title = title,
        thumbnail = thumbnail,
        unitPrice = Money.ofCents(unitPriceCents),
        discountPercentage = Percentage.ofBasisPoints(discountBasisPoints),
        quantity = quantity,
        availableStock = availableStock
    )
}