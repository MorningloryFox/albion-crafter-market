package com.example.domain.model

/**
 * Global crafting configuration for Albion Online tax & return rates.
 */
data class CraftingConfig(
    val userPremium: Boolean = false,
    val resourceReturnRate: Double = 0.248, // 24.8% default for Caerleon without focus
    val plankQuantity: Int = 16,
    val leatherQuantity: Int = 4,
    val artifactQuantity: Int = 1,
    val stationFeePer100Nutrition: Double = 0.0
) {
    /**
     * Albion Market Tax: 4% if Premium is active, 8% without Premium.
     */
    val marketTax: Double
        get() = if (userPremium) 0.04 else 0.08

    val returnRatePercent: Double
        get() = resourceReturnRate * 100.0
}

/**
 * Represents a specific Shapeshifter weapon tier and enchantment in Albion Online.
 */
data class ShapeshifterItem(
    val family: StaffFamily,
    val tier: Tier,
    val enchantment: Enchantment,
    val plankQuantity: Int = 16,
    val leatherQuantity: Int = 4,
    val artifactQuantity: Int = if (family.isArtifactWeapon) 1 else 0
) {
    val id: String
        get() = "${tier.label}_${family.name}_${enchantment.label}"

    val fullName: String
        get() = "${tier.label}${enchantment.label} ${family.displayName}"

    /**
     * API Item ID format for Albion Online Data Project:
     * e.g. T4_2H_SHAPESHIFTER_UNDEAD, T5_2H_SHAPESHIFTER_UNDEAD@1
     */
    val apiItemId: String
        get() = "T${tier.level}_${family.apiItemSuffix}${enchantment.dotSuffix}"

    /**
     * API Artifact ID:
     * e.g. T4_ARTEFACT_2H_SHAPESHIFTER_UNDEAD
     */
    val apiArtifactId: String?
        get() = family.apiArtifactSuffix?.let { "T${tier.level}_$it" }

    /**
     * API Planks ID:
     * e.g. T4_PLANKS or T4_PLANKS_LEVEL1@1
     */
    val apiPlanksId: String
        get() = if (enchantment.level == 0) {
            "T${tier.level}_PLANKS"
        } else {
            "T${tier.level}_PLANKS_LEVEL${enchantment.level}@${enchantment.level}"
        }

    /**
     * API Leather ID:
     * e.g. T4_LEATHER or T4_LEATHER_LEVEL1@1
     */
    val apiLeatherId: String
        get() = if (enchantment.level == 0) {
            "T${tier.level}_LEATHER"
        } else {
            "T${tier.level}_LEATHER_LEVEL${enchantment.level}@${enchantment.level}"
        }

    /**
     * Renderable icon URL from Albion Render API (Coil can display, or fallback to custom vector)
     */
    val iconUrl: String
        get() = "https://render.albiononline.com/v1/item/$apiItemId.png"

    val artifactIconUrl: String?
        get() = apiArtifactId?.let { "https://render.albiononline.com/v1/item/$it.png" }
}

/**
 * Result of the full crafting profitability formula engine.
 */
data class CraftingCalculation(
    val item: ShapeshifterItem,
    val config: CraftingConfig,
    
    // Per-item input prices
    val itemPriceCaerleon: Double,
    val itemPriceBlackMarket: Double,
    val plankUnitPrice: Double,
    val leatherUnitPrice: Double,
    val artifactUnitPrice: Double,
    
    // Step 1: Raw Material Cost
    // total_raw_mat_cost = (plank_price * plank_quantity) + (leather_price * leather_quantity)
    val totalRawMatCost: Double,
    
    // Step 2: Net Material Cost (with RRR)
    // net_mat_cost = total_raw_mat_cost * (1.0 - resource_return_rate_rrr)
    val netMatCost: Double,
    
    // Step 3: Artifact Opportunity Cost
    // artifact_opp_cost = artifact_price * (1.0 - market_tax)
    val artifactOppCost: Double,
    
    // Step 4: Net Revenue (Caerleon)
    // net_rev_caerleon = item_price_caerleon * (1.0 - market_tax)
    val netRevCaerleon: Double,
    
    // Step 5: Net Revenue (Black Market)
    // net_rev_bm = item_price_blackmarket * (1.0 - market_tax)
    val netRevBlackMarket: Double,
    
    // Step 6: Best Revenue
    // best_revenue = max(net_rev_caerleon, net_rev_bm)
    val bestRevenue: Double,
    val bestMarket: MarketType,
    
    // Step 7: Final Profit
    // final_profit = best_revenue - net_mat_cost - artifact_opp_cost
    val finalProfit: Double,
    
    // Step 8: ROI (Return on Investment)
    // roi = final_profit / (net_mat_cost + artifact_price)
    val roi: Double,
    
    // Step 9: Action Recommendation
    // CRAFTAR (if Profit > Artifact Opp Cost) else VENDER CRU
    val recommendation: ActionRecommendation,
    
    // Metadata
    val lastUpdatedMillis: Long = System.currentTimeMillis(),
    val isEstimatedOrFallback: Boolean = false
) {
    val roiPercentage: Double
        get() = roi * 100.0

    val isProfitable: Boolean
        get() = finalProfit > 0.0

    val savingsFromRrr: Double
        get() = totalRawMatCost - netMatCost
}
