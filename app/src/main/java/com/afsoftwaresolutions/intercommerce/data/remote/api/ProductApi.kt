package com.afsoftwaresolutions.intercommerce.data.remote.api

import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductPageDto

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductPageDto

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") productId: Int
    ): ProductDto
}