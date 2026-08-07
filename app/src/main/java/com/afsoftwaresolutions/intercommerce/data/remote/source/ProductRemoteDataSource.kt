package com.afsoftwaresolutions.intercommerce.data.remote.source

import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductPageDto
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult

interface ProductRemoteDataSource {
    suspend fun getProducts(limit: Int, skip: Int): DataResult<ProductPageDto>

    suspend fun searchProducts(query: String): DataResult<ProductPageDto>

    suspend fun getProduct(productId: Int): DataResult<ProductDto>
}