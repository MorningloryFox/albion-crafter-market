package com.example.data.repository

import com.example.domain.model.Enchantment
import com.example.domain.model.ShapeshifterItem
import com.example.domain.model.StaffFamily
import com.example.domain.model.Tier

object ShapeshifterCatalog {

    /**
     * All 140 Shape-shifter staff variations (7 families * 5 tiers * 4 enchantments)
     */
    val allItems: List<ShapeshifterItem> by lazy {
        val list = mutableListOf<ShapeshifterItem>()
        for (family in StaffFamily.entries) {
            for (tier in Tier.entries) {
                for (ench in Enchantment.entries) {
                    list.add(
                        ShapeshifterItem(
                            family = family,
                            tier = tier,
                            enchantment = ench,
                            plankQuantity = 16,
                            leatherQuantity = 4,
                            artifactQuantity = if (family.isArtifactWeapon) 1 else 0
                        )
                    )
                }
            }
        }
        list
    }

    /**
     * Returns all unique Albion API resource IDs required to fetch real-time market data
     * (Weapons, Artifacts, Planks, Leathers).
     */
    fun getAllRequiredApiIds(): List<String> {
        val ids = mutableSetOf<String>()
        
        // Weapons
        allItems.forEach { item ->
            ids.add(item.apiItemId)
            item.apiArtifactId?.let { ids.add(it) }
            ids.add(item.apiPlanksId)
            ids.add(item.apiLeatherId)
        }

        return ids.toList()
    }

    /**
     * Baseline realistic pricing model calibrated against the live Albion Online economy.
     * Used when offline or as immediate seed while fresh API calls complete.
     */
    fun getBaselinePrices(item: ShapeshifterItem): ItemPriceProfile {
        val tierMult = when (item.tier) {
            Tier.T4 -> 1.0
            Tier.T5 -> 2.6
            Tier.T6 -> 7.2
            Tier.T7 -> 22.0
            Tier.T8 -> 75.0
        }

        val enchMult = when (item.enchantment) {
            Enchantment.NONE -> 1.0
            Enchantment.UNCOMMON -> 2.1
            Enchantment.RARE -> 4.8
            Enchantment.EXCEPTIONAL -> 12.5
        }

        // Raw materials
        val basePlank = (450.0 * tierMult * enchMult).coerceAtLeast(300.0)
        val baseLeather = (520.0 * tierMult * enchMult).coerceAtLeast(350.0)

        // Artifact baseline
        val baseArtifact = if (item.family.isArtifactWeapon) {
            val familyFactor = when (item.family) {
                StaffFamily.LIGHTCALLER -> 42000.0 // Undead / high demand
                StaffFamily.BLOODMOON -> 38000.0   // Morgana / Werewolf popular
                StaffFamily.HELLSPAWN -> 26000.0   // Hell
                StaffFamily.EARTHRUNE -> 24000.0   // Keeper
                StaffFamily.PRIMAL -> 75000.0      // Avalonian / ultra rare
                StaffFamily.ROOTBOUND -> 31000.0   // Sylvan / Tree
                StaffFamily.PROWLING -> 0.0
            }
            familyFactor * (tierMult * 0.75).coerceAtLeast(1.0)
        } else {
            0.0
        }

        // Finished item craft value & Black Market demand premium
        val rawCraftCost = (basePlank * 16.0) + (baseLeather * 4.0) + baseArtifact
        
        // Caerleon sell order
        val caerleonPrice = rawCraftCost * 1.18
        
        // Black market buy order (fluctuates, often higher with luxury multiplier)
        val bmPremiumFactor = when (item.family) {
            StaffFamily.LIGHTCALLER -> 1.34
            StaffFamily.BLOODMOON -> 1.28
            StaffFamily.PRIMAL -> 1.39
            StaffFamily.HELLSPAWN -> 1.22
            StaffFamily.EARTHRUNE -> 1.16
            StaffFamily.ROOTBOUND -> 1.25
            StaffFamily.PROWLING -> 1.20
        }
        val blackMarketPrice = rawCraftCost * bmPremiumFactor

        return ItemPriceProfile(
            caerleonPrice = caerleonPrice,
            blackMarketPrice = blackMarketPrice,
            plankPrice = basePlank,
            leatherPrice = baseLeather,
            artifactPrice = baseArtifact
        )
    }
}

data class ItemPriceProfile(
    val caerleonPrice: Double,
    val blackMarketPrice: Double,
    val plankPrice: Double,
    val leatherPrice: Double,
    val artifactPrice: Double
)
