package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.PriceOverrideEntity
import com.example.data.repository.CraftingRepository
import com.example.domain.model.CraftingCalculation
import com.example.domain.model.CraftingConfig
import com.example.domain.model.Enchantment
import com.example.domain.model.StaffFamily
import com.example.domain.model.Tier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val displayName: String) {
    PROFIT_DESC("Maior Lucro (Prata)"),
    ROI_DESC("Maior ROI %"),
    REVENUE_DESC("Melhor Receita"),
    TIER_ASC("Tier (T4 -> T8)")
}

data class CraftingUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val config: CraftingConfig = CraftingConfig(),
    val topOpportunities: List<CraftingCalculation> = emptyList(),
    val filteredCalculations: List<CraftingCalculation> = emptyList(),
    val totalProfitableCount: Int = 0,
    val averageRoi: Double = 0.0,
    val selectedFamilyFilter: StaffFamily? = null,
    val selectedTierFilter: Tier? = null,
    val selectedEnchantmentFilter: Enchantment? = null,
    val searchQuery: String = "",
    val onlyProfitable: Boolean = false,
    val sortBy: SortOption = SortOption.PROFIT_DESC,
    val trackedFamilies: Set<StaffFamily> = StaffFamily.entries.toSet(),
    val lastSyncTimestamp: Long = 0L,
    val cachedItemsCount: Int = 0,
    val activeDetailCalculation: CraftingCalculation? = null,
    val userMessage: String? = null
)

class CraftingViewModel(
    private val repository: CraftingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFamily = MutableStateFlow<StaffFamily?>(null)
    private val _selectedTier = MutableStateFlow<Tier?>(null)
    private val _selectedEnchantment = MutableStateFlow<Enchantment?>(null)
    private val _onlyProfitable = MutableStateFlow(false)
    private val _sortBy = MutableStateFlow(SortOption.PROFIT_DESC)
    private val _isRefreshing = MutableStateFlow(false)
    private val _activeDetail = MutableStateFlow<CraftingCalculation?>(null)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    val uiState: StateFlow<CraftingUiState> = combine(
        repository.calculationsFlow,
        repository.configFlow,
        repository.trackedFamiliesFlow,
        _searchQuery,
        _selectedFamily,
        _selectedTier,
        _selectedEnchantment,
        _onlyProfitable,
        _sortBy,
        _isRefreshing,
        _activeDetail,
        _userMessage
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val rawCalculations = args[0] as List<CraftingCalculation>
        val config = args[1] as CraftingConfig
        @Suppress("UNCHECKED_CAST")
        val trackedFamilies = args[2] as Set<StaffFamily>
        val query = args[3] as String
        val familyFilter = args[4] as StaffFamily?
        val tierFilter = args[5] as Tier?
        val enchFilter = args[6] as Enchantment?
        val onlyProfit = args[7] as Boolean
        val sort = args[8] as SortOption
        val refreshing = args[9] as Boolean
        val activeDetail = args[10] as CraftingCalculation?
        val message = args[11] as String?

        // Top 5 opportunities sorted by highest Profit
        val topOpportunities = rawCalculations
            .filter { it.isProfitable }
            .sortedByDescending { it.finalProfit }
            .take(5)

        // Filter and sort for Detailed screen
        var filtered = rawCalculations

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                it.item.fullName.lowercase().contains(q) ||
                it.item.family.morphName.lowercase().contains(q) ||
                it.item.tier.label.lowercase().contains(q)
            }
        }

        if (familyFilter != null) {
            filtered = filtered.filter { it.item.family == familyFilter }
        }

        if (tierFilter != null) {
            filtered = filtered.filter { it.item.tier == tierFilter }
        }

        if (enchFilter != null) {
            filtered = filtered.filter { it.item.enchantment == enchFilter }
        }

        if (onlyProfit) {
            filtered = filtered.filter { it.isProfitable }
        }

        val sorted = when (sort) {
            SortOption.PROFIT_DESC -> filtered.sortedByDescending { it.finalProfit }
            SortOption.ROI_DESC -> filtered.sortedByDescending { it.roi }
            SortOption.REVENUE_DESC -> filtered.sortedByDescending { it.bestRevenue }
            SortOption.TIER_ASC -> filtered.sortedWith(
                compareBy<CraftingCalculation> { it.item.tier.level }
                    .thenBy { it.item.enchantment.level }
                    .thenByDescending { it.finalProfit }
            )
        }

        val profitableItems = rawCalculations.filter { it.isProfitable }
        val avgRoi = if (profitableItems.isNotEmpty()) {
            profitableItems.map { it.roiPercentage }.average()
        } else {
            0.0
        }

        CraftingUiState(
            isLoading = false,
            isRefreshing = refreshing,
            config = config,
            topOpportunities = topOpportunities,
            filteredCalculations = sorted,
            totalProfitableCount = profitableItems.size,
            averageRoi = avgRoi,
            selectedFamilyFilter = familyFilter,
            selectedTierFilter = tierFilter,
            selectedEnchantmentFilter = enchFilter,
            searchQuery = query,
            onlyProfitable = onlyProfit,
            sortBy = sort,
            trackedFamilies = trackedFamilies,
            activeDetailCalculation = activeDetail,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CraftingUiState(isLoading = true)
    )

    init {
        // Initial silent fetch on startup
        refreshPrices(showUserFeedback = false)
    }

    fun refreshPrices(showUserFeedback: Boolean = true) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.refreshPrices()
            _isRefreshing.value = false

            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                if (showUserFeedback) {
                    val msg = if (count > 0) {
                        "Preços atualizados! Sincronizadas $count ordens do Albion Data Project."
                    } else {
                        "Preços sincronizados com o cache local e linha de base."
                    }
                    _userMessage.value = msg
                    _eventFlow.emit(msg)
                }
            } else {
                if (showUserFeedback) {
                    val errorMsg = "Modo offline: Utilizando preços de mercado em cache."
                    _userMessage.value = errorMsg
                    _eventFlow.emit(errorMsg)
                }
            }
        }
    }

    fun setPremium(isPremium: Boolean) {
        viewModelScope.launch {
            repository.updatePremium(isPremium)
        }
    }

    fun setRrr(rrr: Double) {
        viewModelScope.launch {
            repository.updateRrr(rrr)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFamilyFilter(family: StaffFamily?) {
        _selectedFamily.value = family
    }

    fun setTierFilter(tier: Tier?) {
        _selectedTier.value = tier
    }

    fun setEnchantmentFilter(ench: Enchantment?) {
        _selectedEnchantment.value = ench
    }

    fun toggleOnlyProfitable() {
        _onlyProfitable.value = !_onlyProfitable.value
    }

    fun setSortBy(sort: SortOption) {
        _sortBy.value = sort
    }

    fun setFamilyTracked(family: StaffFamily, isTracked: Boolean) {
        viewModelScope.launch {
            repository.setFamilyTracked(family, isTracked)
        }
    }

    fun openDetail(calculation: CraftingCalculation) {
        _activeDetail.value = calculation
    }

    fun closeDetail() {
        _activeDetail.value = null
    }

    fun savePriceOverride(override: PriceOverrideEntity) {
        viewModelScope.launch {
            repository.setPriceOverride(override)
            _userMessage.value = "Preço personalizado salvo com sucesso!"
        }
    }

    fun resetPriceOverride(itemId: String) {
        viewModelScope.launch {
            repository.clearPriceOverride(itemId)
            _userMessage.value = "Restaurado para os preços de mercado da API."
        }
    }

    fun clearAllCache() {
        viewModelScope.launch {
            repository.clearAllCache()
            _userMessage.value = "Cache limpo. Pronto para sincronizar novamente."
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}

class CraftingViewModelFactory(
    private val repository: CraftingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CraftingViewModel::class.java)) {
            return CraftingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
