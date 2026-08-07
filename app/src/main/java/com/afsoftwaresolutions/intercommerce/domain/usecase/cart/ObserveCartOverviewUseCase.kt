package com.afsoftwaresolutions.intercommerce.domain.usecase.cart

import com.afsoftwaresolutions.intercommerce.domain.model.CartOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveCartOverviewUseCase(
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val calculateCartTotalsUseCase: CalculateCartTotalsUseCase
) {
    operator fun invoke(): Flow<CartOverview> {
        return observeCartItemsUseCase().map { items ->
            CartOverview(
                items = items,
                totals = calculateCartTotalsUseCase(items)
            )
        }
    }
}