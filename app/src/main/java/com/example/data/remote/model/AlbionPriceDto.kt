package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbionPriceDto(
    @Json(name = "item_id") val itemId: String = "",
    @Json(name = "city") val city: String = "",
    @Json(name = "quality") val quality: Int = 1,
    @Json(name = "sell_price_min") val sellPriceMin: Long = 0L,
    @Json(name = "sell_price_min_date") val sellPriceMinDate: String? = null,
    @Json(name = "sell_price_max") val sellPriceMax: Long = 0L,
    @Json(name = "sell_price_max_date") val sellPriceMaxDate: String? = null,
    @Json(name = "buy_price_min") val buyPriceMin: Long = 0L,
    @Json(name = "buy_price_min_date") val buyPriceMinDate: String? = null,
    @Json(name = "buy_price_max") val buyPriceMax: Long = 0L,
    @Json(name = "buy_price_max_date") val buyPriceMaxDate: String? = null
)
