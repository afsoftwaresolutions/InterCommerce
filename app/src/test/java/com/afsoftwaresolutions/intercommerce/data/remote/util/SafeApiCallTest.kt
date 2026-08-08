package com.afsoftwaresolutions.intercommerce.data.remote.util

import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeApiCallTest {

    @Test
    fun `returns success when call succeeds`() = runTest {
        val result = safeApiCall { "ok" }

        assertEquals(DataResult.Success("ok"), result)
    }

    @Test
    fun `maps UnknownHostException to NoConnection`() = runTest {
        val result = safeApiCall<String> { throw UnknownHostException() }

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
    }

    @Test
    fun `maps ConnectException to NoConnection`() = runTest {
        val result = safeApiCall<String> { throw ConnectException() }

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
    }

    @Test
    fun `maps SocketTimeoutException to Timeout`() = runTest {
        val result = safeApiCall<String> { throw SocketTimeoutException() }

        assertEquals(DataResult.Failure(DataError.Timeout), result)
    }

    @Test
    fun `maps HttpException 404 to NotFound`() = runTest {
        val response = Response.error<String>(
            404,
            "not found".toResponseBody("text/plain".toMediaType())
        )

        val result = safeApiCall<String> { throw HttpException(response) }

        assertEquals(DataResult.Failure(DataError.NotFound), result)
    }

    @Test
    fun `maps HttpException 500 to Server`() = runTest {
        val response = Response.error<String>(
            500,
            "server".toResponseBody("text/plain".toMediaType())
        )

        val result = safeApiCall<String> { throw HttpException(response) }

        assertEquals(DataResult.Failure(DataError.Server(500)), result)
    }

    @Test
    fun `maps SerializationException to Serialization`() = runTest {
        val result = safeApiCall<String> { throw SerializationException("bad json") }

        assertEquals(DataResult.Failure(DataError.Serialization), result)
    }

    @Test
    fun `maps IOException to NoConnection`() = runTest {
        val result = safeApiCall<String> { throw IOException("io") }

        assertEquals(DataResult.Failure(DataError.NoConnection), result)
    }

    @Test
    fun `maps unknown exception to Unknown`() = runTest {
        val result = safeApiCall<String> { throw IllegalStateException("boom") }

        assertTrue(result is DataResult.Failure)
        assertEquals(DataError.Unknown, (result as DataResult.Failure).error)
    }
}

