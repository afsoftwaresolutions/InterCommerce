package com.afsoftwaresolutions.intercommerce.domain.error

sealed interface DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>
    data class Failure(val error: DataError) : DataResult<Nothing>
}