package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.local.entity.PriceEntity
import com.example.data.local.entity.PriceOverrideEntity
import com.example.data.remote.AlbionApiService
import com.example.data.remote.NetworkClient
import com.example.domain.model.CraftingCalculation
import com.example.domain.model.CraftingConfig
import com.example.domain.model.ShapeshifterItem
import com.example.domain.model.StaffFamily
import com.example.domain.usecase.CalculateCraftingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

interface CraftingRepository {
    val calculationsFlow: Flow<List<CraftingCalculation>>
    val configFlow: Flow<CraftingConfig>
    val trackedFamiliesFlow: Flow<Set<StaffFamily>>

    suspend fun refreshPrices(): Result<Int>
    suspend fun updatePremium(isPremium: Boolean)
    suspend fun updateRrr(rrr: Double)
    suspend fun updatePlankQty(qty: Int)
    suspend fun updateLeatherQty(qty: Int)
    suspend fun setFamilyTracked(family: StaffFamily, isTracked: Boolean)
    suspend fun setPriceOverride(override: PriceOverrideEntity)
    suspend fun clearPriceOverride(itemId: String)
    suspend fun clearAllCache()
    suspend fun getCachedPriceCount(): Int
}

class CraftingRepositoryImpl(
    private val database: AppDatabase,
    private val preferencesRepository: UserPreferencesRepository,
    private val apiService: AlbionApiService = NetworkClient.apiService,
    private val calculateUseCase: CalculateCraftingUseCase = CalculateCraftingUseCase()
) : CraftingRepository {

    private val priceDao = database.priceDao()

    override val configFlow: Flow<CraftingConfig> = preferencesRepository.configFlow
    override val trackedFamiliesFlow: Flow<Set<StaffFamily>> = preferencesRepository.trackedFamiliesFlow

    override val calculationsFlow: Flow<List<CraftingCalculation>> = combine(
        priceDao.getAllPricesFlow(),
        priceDao.getAllOverridesFlow(),
        configFlow,
        trackedFamiliesFlow
    ) { cachedPrices, overrides, config, trackedFamilies ->
        val priceMap = mutableMapOf<String, MutableMap<String, Long>>()
        // Populate priceMap: key = itemId, sub-key = city ("Caerleon", "Black Market") -> price
        cachedPrices.forEach { entity ->
            val cityMap = priceMap.getOrPut(entity.itemId) { mutableMapOf() }
            val effectivePrice = if (entity.city.contains("Black", ignoreCase = true)) {
                if (entity.buyPriceMax > 0) entity.buyPriceMax else entity.sellPriceMin
            } else {
                if (entity.sellPriceMin > 0) entity.sellPriceMin else entity.buyPriceMax
            }
            if (effectivePrice > 0) {
                cityMap[entity.city] = effectivePrice
            }
        }

        val overrideMap = overrides.associateBy { it.itemId }

        val activeItems = ShapeshifterCatalog.allItems.filter { item ->
            trackedFamilies.contains(item.family)
        }

        activeItems.map { item ->
            val baseline = ShapeshifterCatalog.getBaselinePrices(item)
            val override = overrideMap[item.id]

            // Finished item in Caerleon
            val caerleonFromDb = priceMap[item.apiItemId]?.get("Caerleon")?.toDouble()
            val finalCaerleonPrice = override?.customCaerleonPrice
                ?: (if (caerleonFromDb != null && caerleonFromDb > 0) caerleonFromDb else baseline.caerleonPrice)

            // Finished item in Black Market
            val bmFromDb = priceMap[item.apiItemId]?.entries
                ?.find { it.key.contains("Black", ignoreCase = true) }?.value?.toDouble()
            val finalBmPrice = override?.customBlackMarketPrice
                ?: (if (bmFromDb != null && bmFromDb > 0) bmFromDb else baseline.blackMarketPrice)

            // Planks
            val plankFromDb = priceMap[item.apiPlanksId]?.get("Caerleon")?.toDouble()
            val finalPlankPrice = override?.customPlankPrice
                ?: (if (plankFromDb != null && plankFromDb > 0) plankFromDb else baseline.plankPrice)

            // Leather
            val leatherFromDb = priceMap[item.apiLeatherId]?.get("Caerleon")?.toDouble()
            val finalLeatherPrice = override?.customLeatherPrice
                ?: (if (leatherFromDb != null && leatherFromDb > 0) leatherFromDb else baseline.leatherPrice)

            // Artifact
            val finalArtifactPrice = if (item.family.isArtifactWeapon && item.apiArtifactId != null) {
                val artFromDb = priceMap[item.apiArtifactId]?.get("Caerleon")?.toDouble()
                override?.customArtifactPrice
                    ?: (if (artFromDb != null && artFromDb > 0) artFromDb else baseline.artifactPrice)
            } else {
                0.0
            }

            val hasRealApiData = caerleonFromDb != null || bmFromDb != null

            calculateUseCase(
                item = item.copy(
                    plankQuantity = config.plankQuantity,
                    leatherQuantity = config.leatherQuantity
                ),
                config = config,
                itemPriceCaerleon = finalCaerleonPrice,
                itemPriceBlackMarket = finalBmPrice,
                plankUnitPrice = finalPlankPrice,
                leatherUnitPrice = finalLeatherPrice,
                artifactUnitPrice = finalArtifactPrice,
                isEstimatedOrFallback = !hasRealApiData && override == null
            )
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun refreshPrices(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val allApiIds = ShapeshifterCatalog.getAllRequiredApiIds()
            // Batch API calls in chunks of 50 to avoid URL length limit and respect API rules
            val chunkSize = 40
            val chunks = allApiIds.chunked(chunkSize)
            val entitiesToInsert = mutableListOf<PriceEntity>()

            for (chunk in chunks) {
                val queryStr = chunk.joinToString(",")
                try {
                    val dtos = apiService.getPrices(itemsCommaSeparated = queryStr)
                    dtos.forEach { dto ->
                        if (dto.itemId.isNotBlank() && dto.city.isNotBlank()) {
                            val entity = PriceEntity(
                                primaryKey = "${dto.itemId}_${dto.city}_${dto.quality}",
                                itemId = dto.itemId,
                                city = dto.city,
                                quality = dto.quality,
                                sellPriceMin = dto.sellPriceMin,
                                sellPriceMax = dto.sellPriceMax,
                                buyPriceMin = dto.buyPriceMin,
                                buyPriceMax = dto.buyPriceMax,
                                updatedAtMillis = System.currentTimeMillis()
                            )
                            entitiesToInsert.add(entity)
                        }
                    }
                } catch (e: Exception) {
                    // Log and continue with next chunk if one fails
                }
            }

            if (entitiesToInsert.isNotEmpty()) {
                priceDao.insertPrices(entitiesToInsert)
                preferencesRepository.updateLastSyncTime(System.currentTimeMillis())
                Result.success(entitiesToInsert.size)
            } else {
                // If API was unreachable or empty, ensure baseline exists
                Result.success(0)
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePremium(isPremium: Boolean) {
        preferencesRepository.updatePremium(isPremium)
    }

    override suspend fun updateRrr(rrr: Double) {
        preferencesRepository.updateRrr(rrr)
    }

    override suspend fun updatePlankQty(qty: Int) {
        preferencesRepository.updatePlankQuantity(qty)
    }

    override suspend fun updateLeatherQty(qty: Int) {
        preferencesRepository.updateLeatherQuantity(qty)
    }

    override suspend fun setFamilyTracked(family: StaffFamily, isTracked: Boolean) {
        preferencesRepository.setFamilyTracked(family, isTracked)
    }

    override suspend fun setPriceOverride(override: PriceOverrideEntity) {
        withContext(Dispatchers.IO) {
            priceDao.saveOverride(override)
        }
    }

    override suspend fun clearPriceOverride(itemId: String) {
        withContext(Dispatchers.IO) {
            priceDao.deleteOverride(itemId)
        }
    }

    override suspend fun clearAllCache() {
        withContext(Dispatchers.IO) {
            priceDao.clearAllPrices()
            priceDao.clearAllOverrides()
            preferencesRepository.updateLastSyncTime(0L)
        }
    }

    override suspend fun getCachedPriceCount(): Int = withContext(Dispatchers.IO) {
        priceDao.getPriceCount()
    }
}
