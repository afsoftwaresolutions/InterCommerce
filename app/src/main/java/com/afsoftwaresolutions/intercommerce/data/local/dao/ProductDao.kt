package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductEntityBundle
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Upsert
    suspend fun upsertProduct(product: ProductEntity)

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Upsert
    suspend fun upsertTags(tags: List<ProductTagEntity>)

    @Upsert
    suspend fun upsertImages(images: List<ProductImageEntity>)

    @Upsert
    suspend fun upsertReviews(reviews: List<ProductReviewEntity>)

    @Query("DELETE FROM product_tags WHERE productId = :productId")
    suspend fun deleteTagsByProductId(productId: Int)

    @Query("DELETE FROM product_images WHERE productId = :productId")
    suspend fun deleteImagesByProductId(productId: Int)

    @Query("DELETE FROM product_reviews WHERE productId = :productId")
    suspend fun deleteReviewsByProductId(productId: Int)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun countProducts(): Int

    @Transaction
    suspend fun replaceProduct(bundle: ProductEntityBundle) {
        upsertProduct(bundle.product)
        deleteTagsByProductId(bundle.product.id)
        deleteImagesByProductId(bundle.product.id)
        deleteReviewsByProductId(bundle.product.id)
        upsertTags(bundle.tags)
        upsertImages(bundle.images)
        upsertReviews(bundle.reviews)
    }

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    fun observeProduct(productId: Int): Flow<ProductWithDetails?>

    @Transaction
    suspend fun searchProducts(query: String): List<ProductWithDetails> {
        val normalizedQuery = "%${query.trim()}%"
        return searchProductsInternal(normalizedQuery)
    }

    @Transaction
    @Query(
        """
        SELECT * FROM products
        WHERE LOWER(title) LIKE LOWER(:normalizedQuery)
           OR LOWER(category) LIKE LOWER(:normalizedQuery)
           OR LOWER(COALESCE(brand, '')) LIKE LOWER(:normalizedQuery)
        ORDER BY title COLLATE NOCASE ASC, id ASC
        """
    )
    suspend fun searchProductsInternal(normalizedQuery: String): List<ProductWithDetails>

    @Transaction
    @Query(
        """
        SELECT products.*
        FROM products
        INNER JOIN catalog_entries
            ON products.id = catalog_entries.productId
        ORDER BY catalog_entries.position ASC, products.id ASC
        """
    )
    fun pagingSource(): PagingSource<Int, ProductWithDetails>
}