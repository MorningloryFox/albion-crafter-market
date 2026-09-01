package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.CraftingConfig
import com.example.domain.model.StaffFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("albion_craft_prefs", Context.MODE_PRIVATE)

    private val _configFlow = MutableStateFlow(loadConfig())
    val configFlow: StateFlow<CraftingConfig> = _configFlow.asStateFlow()

    private val _trackedFamiliesFlow = MutableStateFlow(loadTrackedFamilies())
    val trackedFamiliesFlow: StateFlow<Set<StaffFamily>> = _trackedFamiliesFlow.asStateFlow()

    private fun loadConfig(): CraftingConfig {
        val isPremium = prefs.getBoolean(KEY_PREMIUM, false)
        val rrr = prefs.getFloat(KEY_RRR, 0.248f).toDouble()
        val plankQty = prefs.getInt(KEY_PLANK_QTY, 16)
        val leatherQty = prefs.getInt(KEY_LEATHER_QTY, 4)
        return CraftingConfig(
            userPremium = isPremium,
            resourceReturnRate = rrr,
            plankQuantity = plankQty,
            leatherQuantity = leatherQty
        )
    }

    private fun loadTrackedFamilies(): Set<StaffFamily> {
        val saved = prefs.getStringSet(KEY_TRACKED_FAMILIES, null)
        return if (saved.isNullOrEmpty()) {
            StaffFamily.entries.toSet()
        } else {
            saved.mapNotNull { StaffFamily.fromId(it) }.toSet().ifEmpty { StaffFamily.entries.toSet() }
        }
    }

    fun updatePremium(isPremium: Boolean) {
        prefs.edit().putBoolean(KEY_PREMIUM, isPremium).apply()
        _configFlow.value = _configFlow.value.copy(userPremium = isPremium)
    }

    fun updateRrr(rrr: Double) {
        prefs.edit().putFloat(KEY_RRR, rrr.toFloat()).apply()
        _configFlow.value = _configFlow.value.copy(resourceReturnRate = rrr)
    }

    fun updatePlankQuantity(qty: Int) {
        prefs.edit().putInt(KEY_PLANK_QTY, qty).apply()
        _configFlow.value = _configFlow.value.copy(plankQuantity = qty)
    }

    fun updateLeatherQuantity(qty: Int) {
        prefs.edit().putInt(KEY_LEATHER_QTY, qty).apply()
        _configFlow.value = _configFlow.value.copy(leatherQuantity = qty)
    }

    fun setFamilyTracked(family: StaffFamily, isTracked: Boolean) {
        val current = _trackedFamiliesFlow.value.toMutableSet()
        if (isTracked) {
            current.add(family)
        } else {
            if (current.size > 1) { // keep at least 1
                current.remove(family)
            }
        }
        prefs.edit().putStringSet(KEY_TRACKED_FAMILIES, current.map { it.name }.toSet()).apply()
        _trackedFamiliesFlow.value = current
    }

    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC, 0L)

    fun updateLastSyncTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
    }

    companion object {
        private const val KEY_PREMIUM = "key_premium"
        private const val KEY_RRR = "key_rrr"
        private const val KEY_PLANK_QTY = "key_plank_qty"
        private const val KEY_LEATHER_QTY = "key_leather_qty"
        private const val KEY_TRACKED_FAMILIES = "key_tracked_families"
        private const val KEY_LAST_SYNC = "key_last_sync"
    }
}
