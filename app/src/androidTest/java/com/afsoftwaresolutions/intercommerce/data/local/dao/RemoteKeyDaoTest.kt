package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import com.afsoftwaresolutions.intercommerce.data.local.entity.RemoteKeyEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndGetRemoteKey_returnsPersistedValue() = runBlocking {
        val key = RemoteKeyEntity(
            scope = AppDatabase.CATALOG_SCOPE,
            nextSkip = 20,
            endOfPaginationReached = false,
            updatedAtEpochMillis = 1_000L
        )

        remoteKeyDao.upsertRemoteKey(key)
        val stored = remoteKeyDao.getRemoteKey(AppDatabase.CATALOG_SCOPE)

        assertEquals(key, stored)
    }

    @Test
    fun deleteRemoteKey_removesOnlySpecifiedScope() = runBlocking {
        remoteKeyDao.upsertRemoteKey(
            RemoteKeyEntity("catalog", nextSkip = 10, endOfPaginationReached = false, updatedAtEpochMillis = 10L)
        )
        remoteKeyDao.upsertRemoteKey(
            RemoteKeyEntity("detail", nextSkip = 0, endOfPaginationReached = true, updatedAtEpochMillis = 20L)
        )

        remoteKeyDao.deleteRemoteKey("catalog")

        assertNull(remoteKeyDao.getRemoteKey("catalog"))
        assertEquals("detail", remoteKeyDao.getRemoteKey("detail")?.scope)
    }
}

