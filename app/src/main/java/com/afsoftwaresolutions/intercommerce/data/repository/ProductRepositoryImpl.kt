package com.afsoftwaresolutions.intercommerce.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.afsoftwaresolutions.intercommerce.core.time.TimeProvider
import com.afsoftwaresolutions.intercommerce.data.local.dao.CatalogDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.ProductDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.RemoteKeyDao
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import com.afsoftwaresolutions.intercommerce.data.local.util.safeDatabaseCall
import com.afsoftwaresolutions.intercommerce.data.mapper.toDomain
import com.afsoftwaresolutions.intercommerce.data.mapper.toEntityBundle
import com.afsoftwaresolutions.intercommerce.data.paging.ProductRemoteMediator
import com.afsoftwaresolutions.intercommerce.data.remote.source.ProductRemoteDataSource
import com.afsoftwaresolutions.intercommerce.di.qualifier.IoDispatcher
import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl  @Inject constructor(
    private val database: AppDatabase,
    private val remoteDataSource: ProductRemoteDataSource,
    private val productDao: ProductDao,
    private val catalogDao: CatalogDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val timeProvider: TimeProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun observePagedProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = ProductRemoteMediator.PAGE_SIZE,
                initialLoadSize = ProductRemoteMediator.PAGE_SIZE,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(
                database = database,
                remoteDataSource = remoteDataSource,
                productDao = productDao,
                catalogDao = catalogDao,
                remoteKeyDao = remoteKeyDao,
                timeProvider = timeProvider,
                ioDispatcher = ioDispatcher
            ),
            pagingSourceFactory = {
                productDao.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { details -> details.toDomain() }
        }
    }

    override suspend fun searchProducts(query: String): DataResult<List<Product>> {

        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            return DataResult.Success(emptyList())
        }

        return when (val remoteResult = remoteDataSource.searchProducts(normalizedQuery)) {
            is DataResult.Success -> {
                val products = try {
                    remoteResult.data.products.toDomain()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: IllegalArgumentException) {
                    return DataResult.Failure(DataError.Serialization)
                } catch (_: ArithmeticException) {
                    return DataResult.Failure(DataError.Serialization)
                }

                val saveResult = safeDatabaseCall(ioDispatcher) {
                    val timestamp = timeProvider.currentTimeMillis()
                    database.withTransaction {
                        products.forEach { product ->
                            productDao.replaceProduct(product.toEntityBundle(timestamp))
                        }
                    }
                }

                when (saveResult) {
                    is DataResult.Success -> DataResult.Success(products)
                    is DataResult.Failure -> DataResult.Failure(saveResult.error)
                }
            }

            is DataResult.Failure -> {
                val localResult = safeDatabaseCall(ioDispatcher) {
                    productDao.searchProducts(normalizedQuery)
                }

                when (localResult) {
                    is DataResult.Success -> {
                        val localProducts = localResult.data.map { it.toDomain() }
                        if (localProducts.isNotEmpty()) {
                            DataResult.Success(localProducts)
                        } else {
                            DataResult.Failure(remoteResult.error)
                        }
                    }

                    is DataResult.Failure -> DataResult.Failure(DataError.Database)
                }
            }
        }
    }

    override fun observeProduct(productId: Int): Flow<Product?> {
        return productDao.observeProduct(productId).map { details ->
            details?.toDomain()
        }
    }

    override suspend fun refreshProduct(productId: Int): DataResult<Unit> {
        return when (val remoteResult = remoteDataSource.getProduct(productId)) {
            is DataResult.Failure -> remoteResult
            is DataResult.Success -> {
                val product = try {
                    remoteResult.data.toDomain()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: IllegalArgumentException) {
                    return DataResult.Failure(DataError.Serialization)
                } catch (_: ArithmeticException) {
                    return DataResult.Failure(DataError.Serialization)
                }

                val saveResult = safeDatabaseCall(ioDispatcher) {
                    database.withTransaction {
                        productDao.replaceProduct(
                            product.toEntityBundle(
                                cachedAtEpochMillis = timeProvider.currentTimeMillis()
                            )
                        )
                    }
                }

                when (saveResult) {
                    is DataResult.Success -> DataResult.Success(Unit)
                    is DataResult.Failure -> DataResult.Failure(saveResult.error)
                }
            }
        }
    }
}