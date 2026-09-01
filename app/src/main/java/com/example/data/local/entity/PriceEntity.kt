package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_prices")
data class PriceEntity(
    @PrimaryKey val primaryKey: String, // "${itemId}_${city}_${quality}"
    val itemId: String,
    val city: String,
    val quality: Int,
    val sellPriceMin: Long,
    val sellPriceMax: Long,
    val buyPriceMin: Long,
    val buyPriceMax: Long,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_overrides")
data class PriceOverrideEntity(
    @PrimaryKey val itemId: String,
    val customCaerleonPrice: Double? = null,
    val customBlackMarketPrice: Double? = null,
    val customPlankPrice: Double? = null,
    val customLeatherPrice: Double? = null,
    val customArtifactPrice: Double? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
