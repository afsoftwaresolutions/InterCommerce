package com.afsoftwaresolutions.intercommerce.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afsoftwaresolutions.intercommerce.domain.usecase.cart.ObserveCartOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CartBadgeViewModel @Inject constructor(
    observeCartOverviewUseCase: ObserveCartOverviewUseCase
) : ViewModel() {

    val cartItemCount: StateFlow<Int> = observeCartOverviewUseCase()
        .map { overview -> overview.items.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )
}