package com.afsoftwaresolutions.intercommerce.domain.error

sealed interface DataError {
    data object NoConnection : DataError
    data object Timeout : DataError
    data object NotFound : DataError
    data class Http(val code: Int) : DataError
    data class Server(val code: Int) : DataError
    data object Serialization : DataError
    data object Database : DataError
    data object Unknown : DataError
}