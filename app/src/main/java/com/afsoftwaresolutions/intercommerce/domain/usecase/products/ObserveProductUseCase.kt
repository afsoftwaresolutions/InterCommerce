package com.afsoftwaresolutions.intercommerce.domain.usecase.products

import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveProductUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: Int): Flow<Product?> {
        if (productId <= 0) {
            return flowOf(null)
        }
        return productRepository.observeProduct(productId)
    }
}