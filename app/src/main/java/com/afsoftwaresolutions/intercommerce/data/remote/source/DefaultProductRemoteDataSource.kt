package com.afsoftwaresolutions.intercommerce.data.remote.source

import com.afsoftwaresolutions.intercommerce.data.remote.api.ProductApi
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductPageDto
import com.afsoftwaresolutions.intercommerce.data.remote.util.safeApiCall
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import javax.inject.Inject

class DefaultProductRemoteDataSource @Inject constructor(
    private val productApi: ProductApi
) : ProductRemoteDataSource {

    override suspend fun getProducts(limit: Int, skip: Int): DataResult<ProductPageDto> {
        if (limit <= 0 || skip < 0) {
            return DataResult.Failure(DataError.Unknown)
        }
        return safeApiCall {
            productApi.getProducts(limit = limit, skip = skip)
        }
    }

    override suspend fun searchProducts(query: String): DataResult<ProductPageDto> {
        val normalizedQuery = query.trim()
        return safeApiCall {
            productApi.searchProducts(query = normalizedQuery)
        }
    }

    override suspend fun getProduct(productId: Int): DataResult<ProductDto> {
        if (productId <= 0) {
            return DataResult.Failure(DataError.Unknown)
        }
        return safeApiCall {
            productApi.getProduct(productId = productId)
        }
    }
}