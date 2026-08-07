package com.afsoftwaresolutions.intercommerce.core.time

object SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}