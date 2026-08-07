package com.afsoftwaresolutions.intercommerce.data.remote.util

import kotlinx.serialization.json.Json

val appJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = false
    coerceInputValues = true
    explicitNulls = false
}