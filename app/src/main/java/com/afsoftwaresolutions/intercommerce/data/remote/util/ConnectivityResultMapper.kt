package com.afsoftwaresolutions.intercommerce.data.remote.util

import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityStatus
import com.afsoftwaresolutions.intercommerce.domain.error.DataError

fun NetworkConnectivityStatus.toDataErrorOrNull(): DataError? {
    return when (this) {
        is NetworkConnectivityStatus.Validated -> null
        is NetworkConnectivityStatus.ConnectedNoInternet,
        is NetworkConnectivityStatus.Disconnected,
        is NetworkConnectivityStatus.AirplaneMode -> DataError.NoConnection
    }
}

