package com.afsoftwaresolutions.intercommerce.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PercentageTest {

    @Test
    fun `ofBasisPoints accepts boundaries`() {
        assertEquals(0, Percentage.ofBasisPoints(0).basisPoints)
        assertEquals(10_000, Percentage.ofBasisPoints(10_000).basisPoints)
    }

    @Test
    fun `ofBasisPoints throws below minimum`() {
        assertThrows(IllegalArgumentException::class.java) {
            Percentage.ofBasisPoints(-1)
        }
    }

    @Test
    fun `ofBasisPoints throws above maximum`() {
        assertThrows(IllegalArgumentException::class.java) {
            Percentage.ofBasisPoints(10_001)
        }
    }
}

