package com.afsoftwaresolutions.intercommerce.domain.usecase.products

import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository

class RefreshProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: Int): DataResult<Unit> {
        if (productId <= 0) {
            return DataResult.Failure(DataError.NotFound)
        }
        return productRepository.refreshProduct(productId)
    }
}