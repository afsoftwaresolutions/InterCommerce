package com.afsoftwaresolutions.intercommerce.core.network

interface NetworkConnectivityValidator {
    fun currentStatus(): NetworkConnectivityStatus

    fun hasValidatedInternet(): Boolean {
        return currentStatus() is NetworkConnectivityStatus.Validated
    }
}

