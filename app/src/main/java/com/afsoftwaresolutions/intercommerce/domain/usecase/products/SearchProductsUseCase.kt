package com.afsoftwaresolutions.intercommerce.domain.usecase.products

import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository

class SearchProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(query: String): DataResult<List<Product>> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            return DataResult.Success(emptyList())
        }
        return productRepository.searchProducts(normalizedQuery)
    }
}