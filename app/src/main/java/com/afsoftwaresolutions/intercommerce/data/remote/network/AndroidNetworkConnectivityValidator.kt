package com.afsoftwaresolutions.intercommerce.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityStatus
import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityValidator
import com.afsoftwaresolutions.intercommerce.core.network.NetworkTransport

class AndroidNetworkConnectivityValidator(
    private val context: Context,
    private val connectivityManager: ConnectivityManager
) : NetworkConnectivityValidator {

    override fun currentStatus(): NetworkConnectivityStatus {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = activeNetwork?.let(connectivityManager::getNetworkCapabilities)

        if (capabilities == null) {
            return if (isAirplaneModeEnabled()) {
                NetworkConnectivityStatus.AirplaneMode()
            } else {
                NetworkConnectivityStatus.Disconnected()
            }
        }

        val transport = capabilities.toTransport()
        val hasInternetCapability = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return when {
            isValidated -> NetworkConnectivityStatus.Validated(transport)
            hasInternetCapability -> NetworkConnectivityStatus.ConnectedNoInternet(transport)
            else -> NetworkConnectivityStatus.Disconnected(transport)
        }
    }

    private fun isAirplaneModeEnabled(): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        ) == 1
    }

    private fun NetworkCapabilities.toTransport(): NetworkTransport {
        return when {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkTransport.WIFI
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkTransport.CELLULAR
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkTransport.ETHERNET
            hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkTransport.VPN
            else -> NetworkTransport.OTHER
        }
    }
}

