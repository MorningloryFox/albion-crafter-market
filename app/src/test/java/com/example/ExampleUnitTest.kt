package com.example

import com.example.domain.model.ActionRecommendation
import com.example.domain.model.CraftingConfig
import com.example.domain.model.Enchantment
import com.example.domain.model.MarketType
import com.example.domain.model.ShapeshifterItem
import com.example.domain.model.StaffFamily
import com.example.domain.model.Tier
import com.example.domain.usecase.CalculateCraftingUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for the Albion Craft Master Shapeshifter Logic Engine.
 */
class ExampleUnitTest {

    private val calculateUseCase = CalculateCraftingUseCase()

    @Test
    fun testCraftingProfitFormulas_NonPremium_CaerleonBest() {
        val item = ShapeshifterItem(
            family = StaffFamily.LIGHTCALLER,
            tier = Tier.T4,
            enchantment = Enchantment.NONE,
            plankQuantity = 16,
            leatherQuantity = 4,
            artifactQuantity = 1
        )
        val config = CraftingConfig(
            userPremium = false, // 8% tax
            resourceReturnRate = 0.248 // 24.8% RRR
        )

        val result = calculateUseCase(
            item = item,
            config = config,
            itemPriceCaerleon = 120_000.0,
            itemPriceBlackMarket = 100_000.0,
            plankUnitPrice = 1_000.0,
            leatherUnitPrice = 1_200.0,
            artifactUnitPrice = 40_000.0
        )

        // 1. Raw Mat Cost = (1000 * 16) + (1200 * 4) = 16000 + 4800 = 20800
        assertEquals(20800.0, result.totalRawMatCost, 0.001)

        // 2. Net Mat Cost = 20800 * (1 - 0.248) = 20800 * 0.752 = 15641.6
        assertEquals(15641.6, result.netMatCost, 0.001)

        // 3. Artifact Opp Cost = 40000 * (1 - 0.08) = 36800.0
        assertEquals(36800.0, result.artifactOppCost, 0.001)

        // 4. Net Rev Caerleon = 120000 * (1 - 0.08) = 110400.0
        assertEquals(110400.0, result.netRevCaerleon, 0.001)

        // 5. Net Rev BM = 100000 * (1 - 0.08) = 92000.0
        assertEquals(92000.0, result.netRevBlackMarket, 0.001)

        // 6. Best Rev = 110400.0 (Caerleon)
        assertEquals(110400.0, result.bestRevenue, 0.001)
        assertEquals(MarketType.CAERLEON, result.bestMarket)

        // 7. Final Profit = 110400 - 15641.6 - 36800 = 57958.4
        assertEquals(57958.4, result.finalProfit, 0.001)

        // 8. ROI = 57958.4 / (15641.6 + 40000) = 57958.4 / 55641.6 ~= 1.0416
        assertEquals(57958.4 / 55641.6, result.roi, 0.001)

        // 9. Recommendation = CRAFTAR (57958.4 > 36800.0)
        assertEquals(ActionRecommendation.CRAFTAR, result.recommendation)
        assertTrue(result.isProfitable)
    }

    @Test
    fun testCraftingFormulas_VenderCruRecommendation() {
        val item = ShapeshifterItem(
            family = StaffFamily.BLOODMOON,
            tier = Tier.T6,
            enchantment = Enchantment.NONE,
            plankQuantity = 16,
            leatherQuantity = 4,
            artifactQuantity = 1
        )
        val config = CraftingConfig(
            userPremium = true, // 4% tax
            resourceReturnRate = 0.248
        )

        // Low finished staff price compared to artifact
        val result = calculateUseCase(
            item = item,
            config = config,
            itemPriceCaerleon = 50_000.0,
            itemPriceBlackMarket = 52_000.0,
            plankUnitPrice = 2_000.0,
            leatherUnitPrice = 2_500.0,
            artifactUnitPrice = 80_000.0
        )

        // Final profit is negative or lower than artifact opp cost -> VENDER CRU
        assertEquals(ActionRecommendation.VENDER_CRU, result.recommendation)
    }
}
