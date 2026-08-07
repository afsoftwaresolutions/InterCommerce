package com.afsoftwaresolutions.intercommerce.data.local.util

import android.database.sqlite.SQLiteException
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun <T> safeDatabaseCall(
    dispatcher: CoroutineDispatcher,
    call: suspend () -> T
): DataResult<T> {
    return try {
        val result = withContext(dispatcher) {
            call()
        }
        DataResult.Success(result)
    } catch (e: CancellationException) {
        throw e
    } catch (_: SQLiteException) {
        DataResult.Failure(DataError.Database)
    } catch (e: IllegalStateException) {
        val message = e.message.orEmpty()
        if (message.contains("room", ignoreCase = true)) {
            DataResult.Failure(DataError.Database)
        } else {
            DataResult.Failure(DataError.Unknown)
        }
    } catch (_: Exception) {
        DataResult.Failure(DataError.Unknown)
    }
}