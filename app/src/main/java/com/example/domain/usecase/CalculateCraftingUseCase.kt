package com.example.domain.usecase

import com.example.domain.model.ActionRecommendation
import com.example.domain.model.CraftingCalculation
import com.example.domain.model.CraftingConfig
import com.example.domain.model.MarketType
import com.example.domain.model.ShapeshifterItem
import kotlin.math.max

/**
 * Pure business logic engine porting Excel crafting formulas for Albion Shape-shifter staffs.
 */
class CalculateCraftingUseCase {

    operator fun invoke(
        item: ShapeshifterItem,
        config: CraftingConfig,
        itemPriceCaerleon: Double,
        itemPriceBlackMarket: Double,
        plankUnitPrice: Double,
        leatherUnitPrice: Double,
        artifactUnitPrice: Double,
        isEstimatedOrFallback: Boolean = false,
        lastUpdatedMillis: Long = System.currentTimeMillis()
    ): CraftingCalculation {
        val plankQty = item.plankQuantity
        val leatherQty = item.leatherQuantity
        val artifactQty = item.artifactQuantity
        val tax = config.marketTax
        val rrr = config.resourceReturnRate

        // 1. Raw Material Cost:
        // total_raw_mat_cost = (plank_price * plank_quantity) + (leather_price * leather_quantity)
        val totalRawMatCost = (plankUnitPrice * plankQty) + (leatherUnitPrice * leatherQty)

        // 2. Net Material Cost (with RRR):
        // net_mat_cost = total_raw_mat_cost * (1.0 - resource_return_rate_rrr)
        val netMatCost = totalRawMatCost * (1.0 - rrr)

        // 3. Artifact Opportunity Cost:
        // artifact_opp_cost = (artifact_price * artifact_quantity) * (1.0 - market_tax)
        val effectiveArtifactPrice = if (item.family.isArtifactWeapon) artifactUnitPrice * artifactQty else 0.0
        val artifactOppCost = effectiveArtifactPrice * (1.0 - tax)

        // 4. Net Revenue (Caerleon):
        // net_rev_caerleon = item_price_caerleon * (1.0 - market_tax)
        val netRevCaerleon = itemPriceCaerleon * (1.0 - tax)

        // 5. Net Revenue (Black Market):
        // net_rev_bm = item_price_blackmarket * (1.0 - market_tax)
        val netRevBm = itemPriceBlackMarket * (1.0 - tax)

        // 6. Best Revenue & Best Market
        val bestRevenue: Double
        val bestMarket: MarketType
        if (netRevBm >= netRevCaerleon) {
            bestRevenue = max(0.0, netRevBm)
            bestMarket = MarketType.BLACK_MARKET
        } else {
            bestRevenue = max(0.0, netRevCaerleon)
            bestMarket = MarketType.CAERLEON
        }

        // 7. Final Profit:
        // final_profit = best_revenue - net_mat_cost - artifact_opp_cost
        val finalProfit = bestRevenue - netMatCost - artifactOppCost

        // 8. ROI (Return on Investment):
        // roi = final_profit / (net_mat_cost + artifact_price)
        val investedCapital = netMatCost + effectiveArtifactPrice
        val roi = if (investedCapital > 0.0) {
            finalProfit / investedCapital
        } else {
            0.0
        }

        // 9. Action Recommendation:
        // CRAFTAR if Profit > Artifact Opp Cost (or Profit > 0 for non-artifact) else VENDER CRU
        val recommendation = if (item.family.isArtifactWeapon) {
            if (finalProfit > artifactOppCost && finalProfit > 0) {
                ActionRecommendation.CRAFTAR
            } else {
                ActionRecommendation.VENDER_CRU
            }
        } else {
            if (finalProfit > 0) {
                ActionRecommendation.CRAFTAR
            } else {
                ActionRecommendation.VENDER_CRU
            }
        }

        return CraftingCalculation(
            item = item,
            config = config,
            itemPriceCaerleon = itemPriceCaerleon,
            itemPriceBlackMarket = itemPriceBlackMarket,
            plankUnitPrice = plankUnitPrice,
            leatherUnitPrice = leatherUnitPrice,
            artifactUnitPrice = effectiveArtifactPrice,
            totalRawMatCost = totalRawMatCost,
            netMatCost = netMatCost,
            artifactOppCost = artifactOppCost,
            netRevCaerleon = netRevCaerleon,
            netRevBlackMarket = netRevBm,
            bestRevenue = bestRevenue,
            bestMarket = bestMarket,
            finalProfit = finalProfit,
            roi = roi,
            recommendation = recommendation,
            lastUpdatedMillis = lastUpdatedMillis,
            isEstimatedOrFallback = isEstimatedOrFallback
        )
    }
}
