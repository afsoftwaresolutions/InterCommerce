package com.afsoftwaresolutions.intercommerce.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DimensionsDto(
    val width: Double = 0.0,
    val height: Double = 0.0,
    val depth: Double = 0.0
)
