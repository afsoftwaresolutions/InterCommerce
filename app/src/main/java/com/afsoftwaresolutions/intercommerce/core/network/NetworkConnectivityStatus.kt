package com.afsoftwaresolutions.intercommerce.core.network

enum class NetworkTransport {
    NONE,
    WIFI,
    CELLULAR,
    ETHERNET,
    VPN,
    OTHER
}

sealed interface NetworkConnectivityStatus {
    val transport: NetworkTransport

    data class Validated(
        override val transport: NetworkTransport
    ) : NetworkConnectivityStatus

    data class ConnectedNoInternet(
        override val transport: NetworkTransport
    ) : NetworkConnectivityStatus

    data class Disconnected(
        override val transport: NetworkTransport = NetworkTransport.NONE
    ) : NetworkConnectivityStatus

    data class AirplaneMode(
        override val transport: NetworkTransport = NetworkTransport.NONE
    ) : NetworkConnectivityStatus
}

