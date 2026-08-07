package com.afsoftwaresolutions.intercommerce.di

import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.AddToCartUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.CalculateCartTotalsUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ClearCartUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ObserveCartItemsUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ObserveCartOverviewUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.RemoveCartItemUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.UpdateCartQuantityUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.ObservePagedProductsUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.ObserveProductUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.RefreshProductUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.SearchProductsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideCalculateCartTotalsUseCase(): CalculateCartTotalsUseCase {
        // 19% is a configurable assumption for this stage because no explicit tax rate was provided.
        return CalculateCartTotalsUseCase(
            taxRate = Percentage.ofBasisPoints(1_900)
        )
    }

    @Provides
    fun provideObservePagedProductsUseCase(
        productRepository: ProductRepository
    ): ObservePagedProductsUseCase = ObservePagedProductsUseCase(productRepository)

    @Provides
    fun provideSearchProductsUseCase(
        productRepository: ProductRepository
    ): SearchProductsUseCase = SearchProductsUseCase(productRepository)

    @Provides
    fun provideObserveProductUseCase(
        productRepository: ProductRepository
    ): ObserveProductUseCase = ObserveProductUseCase(productRepository)

    @Provides
    fun provideRefreshProductUseCase(
        productRepository: ProductRepository
    ): RefreshProductUseCase = RefreshProductUseCase(productRepository)

    @Provides
    fun provideObserveCartItemsUseCase(
        cartRepository: CartRepository
    ): ObserveCartItemsUseCase = ObserveCartItemsUseCase(cartRepository)

    @Provides
    @Singleton
    fun provideAddToCartUseCase(
        cartRepository: CartRepository
    ): AddToCartUseCase = AddToCartUseCase(cartRepository)

    @Provides
    fun provideUpdateCartQuantityUseCase(
        cartRepository: CartRepository
    ): UpdateCartQuantityUseCase = UpdateCartQuantityUseCase(cartRepository)

    @Provides
    fun provideRemoveCartItemUseCase(
        cartRepository: CartRepository
    ): RemoveCartItemUseCase = RemoveCartItemUseCase(cartRepository)

    @Provides
    fun provideClearCartUseCase(
        cartRepository: CartRepository
    ): ClearCartUseCase = ClearCartUseCase(cartRepository)

    @Provides
    fun provideObserveCartOverviewUseCase(
        observeCartItemsUseCase: ObserveCartItemsUseCase,
        calculateCartTotalsUseCase: CalculateCartTotalsUseCase
    ): ObserveCartOverviewUseCase {
        return ObserveCartOverviewUseCase(
            observeCartItemsUseCase = observeCartItemsUseCase,
            calculateCartTotalsUseCase = calculateCartTotalsUseCase
        )
    }
}