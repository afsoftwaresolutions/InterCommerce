package com.afsoftwaresolutions.intercommerce.data.remote.util

import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityStatus
import com.afsoftwaresolutions.intercommerce.core.network.NetworkTransport
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConnectivityResultMapperTest {

    @Test
    fun `validated status maps to null error`() {
        val error = NetworkConnectivityStatus.Validated(NetworkTransport.WIFI).toDataErrorOrNull()

        assertNull(error)
    }

    @Test
    fun `connected without internet maps to no connection`() {
        val error = NetworkConnectivityStatus.ConnectedNoInternet(NetworkTransport.CELLULAR).toDataErrorOrNull()

        assertEquals(DataError.NoConnection, error)
    }

    @Test
    fun `disconnected maps to no connection`() {
        val error = NetworkConnectivityStatus.Disconnected().toDataErrorOrNull()

        assertEquals(DataError.NoConnection, error)
    }

    @Test
    fun `airplane mode maps to no connection`() {
        val error = NetworkConnectivityStatus.AirplaneMode().toDataErrorOrNull()

        assertEquals(DataError.NoConnection, error)
    }
}

