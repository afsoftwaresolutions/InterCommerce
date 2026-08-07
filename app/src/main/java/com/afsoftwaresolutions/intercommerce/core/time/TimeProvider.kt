package com.afsoftwaresolutions.intercommerce.core.time

interface TimeProvider {
    fun currentTimeMillis(): Long
}