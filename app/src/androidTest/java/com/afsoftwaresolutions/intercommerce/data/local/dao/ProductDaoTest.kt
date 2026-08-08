package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductEntityBundle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        productDao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceProduct_replacesRelatedCollections() = runBlocking {
        val initial = bundle(
            id = 1,
            title = "Phone",
            tags = listOf("smartphone"),
            images = listOf("img-1"),
            reviews = listOf("ok")
        )
        productDao.replaceProduct(initial)

        val updated = bundle(
            id = 1,
            title = "Phone Pro",
            tags = listOf("premium", "android"),
            images = listOf("img-2", "img-3"),
            reviews = listOf("great", "nice")
        )
        productDao.replaceProduct(updated)

        val product = productDao.observeProduct(1).first()

        assertNotNull(product)
        assertEquals("Phone Pro", product?.product?.title)
        assertEquals(listOf("premium", "android"), product?.tags?.sortedBy { it.position }?.map { it.value })
        assertEquals(listOf("img-2", "img-3"), product?.images?.sortedBy { it.position }?.map { it.url })
        assertEquals(listOf("great", "nice"), product?.reviews?.sortedBy { it.position }?.map { it.comment })
    }

    @Test
    fun searchProducts_matchesTitleCategoryAndBrand_ignoreCase() = runBlocking {
        productDao.replaceProduct(bundle(id = 1, title = "Pixel Phone", category = "electronics", brand = "Google"))
        productDao.replaceProduct(bundle(id = 2, title = "Coffee Mug", category = "home", brand = "KitchenCo"))

        val byTitle = productDao.searchProducts("pixel")
        val byCategory = productDao.searchProducts("ELECTRONICS")
        val byBrand = productDao.searchProducts("google")

        assertEquals(listOf(1), byTitle.map { it.product.id })
        assertEquals(listOf(1), byCategory.map { it.product.id })
        assertEquals(listOf(1), byBrand.map { it.product.id })
    }

    private fun bundle(
        id: Int,
        title: String,
        category: String = "category",
        brand: String? = "brand",
        tags: List<String> = emptyList(),
        images: List<String> = emptyList(),
        reviews: List<String> = emptyList()
    ): ProductEntityBundle {
        val product = ProductEntity(
            id = id,
            title = title,
            description = "description",
            category = category,
            priceCents = 9_999,
            discountBasisPoints = 0,
            rating = 4.0,
            stock = 10,
            brand = brand,
            sku = "SKU-$id",
            weight = 1,
            dimensionWidth = 1.0,
            dimensionHeight = 1.0,
            dimensionDepth = 1.0,
            warrantyInformation = "",
            shippingInformation = "",
            availabilityStatus = "In Stock",
            returnPolicy = "",
            minimumOrderQuantity = 1,
            thumbnail = "thumb",
            cachedAtEpochMillis = 100L
        )

        val tagEntities = tags.mapIndexed { index, value ->
            ProductTagEntity(productId = id, position = index, value = value)
        }

        val imageEntities = images.mapIndexed { index, url ->
            ProductImageEntity(productId = id, position = index, url = url)
        }

        val reviewEntities = reviews.mapIndexed { index, comment ->
            ProductReviewEntity(
                productId = id,
                position = index,
                rating = 5,
                comment = comment,
                date = "2026-01-01",
                reviewerName = "A",
                reviewerEmail = "a@x.com"
            )
        }

        return ProductEntityBundle(
            product = product,
            tags = tagEntities,
            images = imageEntities,
            reviews = reviewEntities
        )
    }
}

