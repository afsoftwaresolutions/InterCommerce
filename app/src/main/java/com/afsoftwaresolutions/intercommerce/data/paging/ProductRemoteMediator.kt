package com.afsoftwaresolutions.intercommerce.data.paging

import android.database.sqlite.SQLiteException
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.withTransaction
import com.afsoftwaresolutions.intercommerce.core.time.TimeProvider
import com.afsoftwaresolutions.intercommerce.data.local.dao.CatalogDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.ProductDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.RemoteKeyDao
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import com.afsoftwaresolutions.intercommerce.data.local.entity.CatalogEntryEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.RemoteKeyEntity
import com.afsoftwaresolutions.intercommerce.data.local.model.ProductWithDetails
import com.afsoftwaresolutions.intercommerce.data.mapper.toDomain
import com.afsoftwaresolutions.intercommerce.data.mapper.toEntityBundle
import com.afsoftwaresolutions.intercommerce.data.remote.source.ProductRemoteDataSource
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@OptIn(androidx.paging.ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val database: AppDatabase,
    private val remoteDataSource: ProductRemoteDataSource,
    private val productDao: ProductDao,
    private val catalogDao: CatalogDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val timeProvider: TimeProvider,
    private val ioDispatcher: CoroutineDispatcher
) : RemoteMediator<Int, ProductWithDetails>() {

    override suspend fun initialize(): InitializeAction {
        return try {
            withContext(ioDispatcher) {
                val key = remoteKeyDao.getRemoteKey(AppDatabase.CATALOG_SCOPE)
                    ?: return@withContext InitializeAction.LAUNCH_INITIAL_REFRESH

                val age = timeProvider.currentTimeMillis() - key.updatedAtEpochMillis
                if (age < CACHE_TIMEOUT_MILLIS) {
                    InitializeAction.SKIP_INITIAL_REFRESH
                } else {
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductWithDetails>
    ): MediatorResult {
        return try {
            withContext(ioDispatcher) {
                if (loadType == LoadType.PREPEND) {
                    return@withContext MediatorResult.Success(endOfPaginationReached = true)
                }

                val skip = when (loadType) {
                    LoadType.REFRESH -> 0
                    LoadType.APPEND -> {
                        val key = remoteKeyDao.getRemoteKey(AppDatabase.CATALOG_SCOPE)
                        if (key?.endOfPaginationReached == true) {
                            return@withContext MediatorResult.Success(endOfPaginationReached = true)
                        }
                        key?.nextSkip ?: 0
                    }
                    LoadType.PREPEND -> 0
                }

                val remoteResult = remoteDataSource.getProducts(
                    limit = state.config.pageSize,
                    skip = skip
                )

                when (remoteResult) {
                    is DataResult.Failure -> {
                        MediatorResult.Error(PagingDataException(remoteResult.error))
                    }

                    is DataResult.Success -> {
                        val page = remoteResult.data
                        val domainProducts = try {
                            page.products.toDomain()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: IllegalArgumentException) {
                            return@withContext MediatorResult.Error(
                                PagingDataException(DataError.Serialization)
                            )
                        } catch (_: ArithmeticException) {
                            return@withContext MediatorResult.Error(
                                PagingDataException(DataError.Serialization)
                            )
                        }

                        val timestamp = timeProvider.currentTimeMillis()
                        val bundles = domainProducts.map { product ->
                            product.toEntityBundle(cachedAtEpochMillis = timestamp)
                        }

                        val endReached = page.products.isEmpty() || skip + page.products.size >= page.total
                        val nextSkip = if (endReached) null else skip + page.products.size

                        try {
                            database.withTransaction {
                                if (loadType == LoadType.REFRESH) {
                                    catalogDao.clearCatalogEntries()
                                    remoteKeyDao.deleteRemoteKey(AppDatabase.CATALOG_SCOPE)
                                }

                                bundles.forEach { bundle ->
                                    productDao.replaceProduct(bundle)
                                }

                                if (bundles.isNotEmpty()) {
                                    catalogDao.upsertCatalogEntries(
                                        bundles.mapIndexed { index, bundle ->
                                            CatalogEntryEntity(
                                                productId = bundle.product.id,
                                                position = skip + index
                                            )
                                        }
                                    )
                                }

                                remoteKeyDao.upsertRemoteKey(
                                    RemoteKeyEntity(
                                        scope = AppDatabase.CATALOG_SCOPE,
                                        nextSkip = nextSkip,
                                        endOfPaginationReached = endReached,
                                        updatedAtEpochMillis = timestamp
                                    )
                                )
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: SQLiteException) {
                            return@withContext MediatorResult.Error(
                                PagingDataException(DataError.Database)
                            )
                        } catch (_: IllegalStateException) {
                            return@withContext MediatorResult.Error(
                                PagingDataException(DataError.Database)
                            )
                        } catch (_: Exception) {
                            return@withContext MediatorResult.Error(
                                PagingDataException(DataError.Unknown)
                            )
                        }

                        MediatorResult.Success(endOfPaginationReached = endReached)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: SQLiteException) {
            MediatorResult.Error(PagingDataException(DataError.Database))
        } catch (_: IllegalStateException) {
            MediatorResult.Error(PagingDataException(DataError.Database))
        } catch (_: Exception) {
            MediatorResult.Error(PagingDataException(DataError.Unknown))
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val CACHE_TIMEOUT_MILLIS = 60L * 60L * 1000L
    }
}