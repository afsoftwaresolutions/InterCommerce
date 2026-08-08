package com.afsoftwaresolutions.intercommerce.data.remote.source

import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityStatus
import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityValidator
import com.afsoftwaresolutions.intercommerce.core.network.NetworkTransport
import com.afsoftwaresolutions.intercommerce.data.remote.api.ProductApi
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductDto
import com.afsoftwaresolutions.intercommerce.data.remote.dto.ProductPageDto
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultProductRemoteDataSourceTest {

    private lateinit var productApi: ProductApi
    private lateinit var connectivityValidator: NetworkConnectivityValidator
    private lateinit var dataSource: DefaultProductRemoteDataSource

    @Before
    fun setUp() {
        productApi = mockk()
        connectivityValidator = mockk()
        dataSource = DefaultProductRemoteDataSource(productApi, connectivityValidator)
    }

    @Test
    fun `getProducts returns unknown when pagination params are invalid`() = runTest {
        val result = dataSource.getProducts(limit = 0, skip = -1)

        assertEquals(DataResult.Failure(DataError.Unknown), result)
    }

    @Test
    fun `getProducts returns no connection when internet is not validated`() = runTest {
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.Disconnected()

        val result = dataSource.getProducts(limit = 10, skip = 0)

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
        coVerify(exactly = 0) { productApi.getProducts(any(), any()) }
    }

    @Test
    fun `getProducts calls api when internet is validated`() = runTest {
        val page = ProductPageDto(products = listOf(productDto(id = 1)), total = 1, skip = 0, limit = 10)
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.Validated(NetworkTransport.WIFI)
        coEvery { productApi.getProducts(limit = 10, skip = 0) } returns page

        val result = dataSource.getProducts(limit = 10, skip = 0)

        assertEquals(DataResult.Success(page), result)
        coVerify(exactly = 1) { productApi.getProducts(limit = 10, skip = 0) }
    }

    @Test
    fun `searchProducts trims query before calling api`() = runTest {
        val page = ProductPageDto(products = emptyList(), total = 0, skip = 0, limit = 20)
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.Validated(NetworkTransport.CELLULAR)
        coEvery { productApi.searchProducts(query = "phone") } returns page

        val result = dataSource.searchProducts("  phone  ")

        assertEquals(DataResult.Success(page), result)
        coVerify(exactly = 1) { productApi.searchProducts(query = "phone") }
    }

    @Test
    fun `searchProducts returns no connection when offline`() = runTest {
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.AirplaneMode()

        val result = dataSource.searchProducts("query")

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
        coVerify(exactly = 0) { productApi.searchProducts(any()) }
    }

    @Test
    fun `getProduct returns unknown when id is invalid`() = runTest {
        val result = dataSource.getProduct(0)

        assertEquals(DataResult.Failure(DataError.Unknown), result)
    }

    @Test
    fun `getProduct returns no connection when offline`() = runTest {
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.ConnectedNoInternet(NetworkTransport.WIFI)

        val result = dataSource.getProduct(10)

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
        coVerify(exactly = 0) { productApi.getProduct(any()) }
    }

    @Test
    fun `getProduct calls api when online`() = runTest {
        val dto = productDto(id = 88)
        every { connectivityValidator.currentStatus() } returns NetworkConnectivityStatus.Validated(NetworkTransport.WIFI)
        coEvery { productApi.getProduct(productId = 88) } returns dto

        val result = dataSource.getProduct(88)

        assertTrue(result is DataResult.Success)
        assertEquals(dto, (result as DataResult.Success).data)
        coVerify(exactly = 1) { productApi.getProduct(productId = 88) }
    }

    private fun productDto(id: Int): ProductDto {
        return ProductDto(
            id = id,
            title = "Producto $id",
            description = "Desc",
            category = "cat",
            price = 9.99,
            stock = 5,
            thumbnail = "thumb"
        )
    }
}

