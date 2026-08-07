package com.afsoftwaresolutions.intercommerce.data.remote.util

import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    call: suspend () -> T
): DataResult<T> {
    return try {
        DataResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (_: UnknownHostException) {
        DataResult.Failure(DataError.NoConnection)
    } catch (_: ConnectException) {
        DataResult.Failure(DataError.NoConnection)
    } catch (_: SocketTimeoutException) {
        DataResult.Failure(DataError.Timeout)
    } catch (e: HttpException) {
        when (val code = e.code()) {
            404 -> DataResult.Failure(DataError.NotFound)
            in 500..599 -> DataResult.Failure(DataError.Server(code))
            else -> DataResult.Failure(DataError.Http(code))
        }
    } catch (_: SerializationException) {
        DataResult.Failure(DataError.Serialization)
    } catch (_: IOException) {
        DataResult.Failure(DataError.NoConnection)
    } catch (_: Exception) {
        DataResult.Failure(DataError.Unknown)
    }
}