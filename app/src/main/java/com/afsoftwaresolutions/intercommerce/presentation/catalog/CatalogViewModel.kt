package com.afsoftwaresolutions.intercommerce.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afsoftwaresolutions.intercommerce.domain.error.DataResult
import com.afsoftwaresolutions.intercommerce.domain.model.Product
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.ObservePagedProductsUseCase
import com.afsoftwaresolutions.intercommerce.domain.usecase.products.SearchProductsUseCase
import com.afsoftwaresolutions.intercommerce.presentation.common.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    observePagedProductsUseCase: ObservePagedProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val products: Flow<PagingData<Product>> = observePagedProductsUseCase().cachedIn(viewModelScope)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchState: Flow<CatalogUiState> = searchQuery
        .debounce(400)
        .distinctUntilChanged()
        .transformLatest { query ->
            val normalized = query.trim()
            if (normalized.isEmpty()) {
                emit(CatalogUiState())
                return@transformLatest
            }

            emit(
                CatalogUiState(
                    isSearching = true,
                    isSearchActive = true
                )
            )

            when (val result = searchProductsUseCase(normalized)) {
                is DataResult.Success -> emit(
                    CatalogUiState(
                        isSearching = false,
                        searchResults = result.data,
                        searchError = null,
                        isSearchActive = true
                    )
                )

                is DataResult.Failure -> emit(
                    CatalogUiState(
                        isSearching = false,
                        searchResults = emptyList(),
                        searchError = result.error.toUiText(),
                        isSearchActive = true
                    )
                )
            }
        }
        .onStart { emit(CatalogUiState()) }

    val uiState: StateFlow<CatalogUiState> = combine(searchQuery, searchState) { query, state ->
        state.copy(
            searchQuery = query,
            isSearchActive = query.trim().isNotEmpty()
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogUiState()
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun clearSearch() {
        searchQuery.value = ""
    }
}