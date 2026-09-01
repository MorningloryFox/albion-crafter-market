package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PriceEntity
import com.example.data.local.entity.PriceOverrideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {

    @Query("SELECT * FROM market_prices")
    fun getAllPricesFlow(): Flow<List<PriceEntity>>

    @Query("SELECT * FROM market_prices WHERE itemId IN (:itemIds)")
    suspend fun getPricesForItems(itemIds: List<String>): List<PriceEntity>

    @Query("SELECT * FROM market_prices WHERE itemId = :itemId")
    suspend fun getPricesForItem(itemId: String): List<PriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<PriceEntity>)

    @Query("DELETE FROM market_prices")
    suspend fun clearAllPrices()

    @Query("SELECT COUNT(*) FROM market_prices")
    suspend fun getPriceCount(): Int

    // Overrides
    @Query("SELECT * FROM price_overrides")
    fun getAllOverridesFlow(): Flow<List<PriceOverrideEntity>>

    @Query("SELECT * FROM price_overrides WHERE itemId = :itemId")
    suspend fun getOverrideForItem(itemId: String): PriceOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOverride(override: PriceOverrideEntity)

    @Query("DELETE FROM price_overrides WHERE itemId = :itemId")
    suspend fun deleteOverride(itemId: String)

    @Query("DELETE FROM price_overrides")
    suspend fun clearAllOverrides()
}
