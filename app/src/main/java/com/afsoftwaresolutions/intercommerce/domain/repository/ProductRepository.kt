package com.afsoftwaresolutions.intercommerce.domain.repository

import androidx.paging.PagingData
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observePagedProducts(): Flow<PagingData<Product>>

    suspend fun searchProducts(query: String): DataResult<List<Product>>

    fun observeProduct(productId: Int): Flow<Product?>

    suspend fun refreshProduct(productId: Int): DataResult<Unit>
}