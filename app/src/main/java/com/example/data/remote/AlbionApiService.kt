package com.example.data.remote

import com.example.data.remote.model.AlbionPriceDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbionApiService {

    /**
     * Fetches current market prices for given item IDs.
     * Example: https://www.albion-online-data.com/api/v2/stats/Prices/T4_2H_SHAPESHIFTER_UNDEAD,T4_PLANKS.json?locations=Caerleon,BlackMarket
     */
    @GET("stats/Prices/{items}.json")
    suspend fun getPrices(
        @Path("items") itemsCommaSeparated: String,
        @Query("locations") locations: String = "Caerleon,BlackMarket",
        @Query("qualities") qualities: String = "1,2,3"
    ): List<AlbionPriceDto>
}
