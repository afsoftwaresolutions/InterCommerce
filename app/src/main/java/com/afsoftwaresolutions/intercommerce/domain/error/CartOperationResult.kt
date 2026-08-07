package com.afsoftwaresolutions.intercommerce.domain.error

sealed interface CartOperationResult<out T> {
    data class Success<out T>(val data: T) : CartOperationResult<T>
    data class Failure(val error: CartError) : CartOperationResult<Nothing>
}

internal fun <T> DataResult<T>.toCartOperationResult(): CartOperationResult<T> {
    return when (this) {
        is DataResult.Success -> CartOperationResult.Success(data)
        is DataResult.Failure -> CartOperationResult.Failure(CartError.Data(error))
    }
}

internal fun DataResult<Unit>.toCartOperationUnitResult(): CartOperationResult<Unit> {
    return when (val result = toCartOperationResult()) {
        is CartOperationResult.Success -> CartOperationResult.Success(Unit)
        is CartOperationResult.Failure -> result
    }
}