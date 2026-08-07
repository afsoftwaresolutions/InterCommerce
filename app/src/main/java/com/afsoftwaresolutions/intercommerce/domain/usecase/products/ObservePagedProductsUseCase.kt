package com.afsoftwaresolutions.intercommerce.domain.usecase.products

import androidx.paging.PagingData
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ObservePagedProductsUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<PagingData<Product>> {
        return productRepository.observePagedProducts()
    }
}