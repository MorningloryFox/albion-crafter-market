package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ActionRecommendation
import com.example.domain.model.CraftingCalculation
import com.example.domain.model.MarketType
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoOrange
import com.example.ui.theme.BentoOrangeBg
import com.example.ui.theme.BentoOrangeBorder
import com.example.ui.theme.BentoSurfaceItem
import com.example.ui.theme.BlackMarketPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextSubtle
import com.example.ui.util.Formatters

@Composable
fun StaffItemCard(
    calculation: CraftingCalculation,
    onOpenCalculator: (CraftingCalculation) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val isCraftAction = calculation.recommendation == ActionRecommendation.CRAFTAR
    val profitColor = if (calculation.isProfitable) BentoEmeraldLight else BentoOrange

    val recommendationBg = if (isCraftAction) BentoEmeraldBg else BentoOrangeBg
    val recommendationBorder = if (isCraftAction) BentoEmerald.copy(alpha = 0.3f) else BentoOrangeBorder
    val recommendationTextColor = if (isCraftAction) BentoEmeraldLight else BentoOrange

    val routeText = if (calculation.bestMarket == MarketType.BLACK_MARKET) "MN > CAER" else "APENAS CAER"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("staff_card_${calculation.item.id}")
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurfaceItem),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                colors = listOf(BentoBorderSubtle, BentoBorder)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Main Top Row: Avatar + Name/Morph + Profit & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item Avatar with Coil and Shimmer
                AlbionItemImage(item = calculation.item, size = 48.dp)

                Spacer(modifier = Modifier.width(12.dp))

                // Name, Morph & Action Recommendation
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = calculation.item.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Formatters.formatSilverSigned(calculation.finalProfit),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = profitColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Secondary Subrow: CRAFTAR / VENDER CRU badge & Market Route
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ACTION RECOMMENDATION BADGE
                        Box(
                            modifier = Modifier
                                .testTag("action_badge_${calculation.item.id}")
                                .clip(RoundedCornerShape(6.dp))
                                .background(recommendationBg)
                                .border(1.dp, recommendationBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = calculation.recommendation.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = recommendationTextColor
                            )
                        }

                        // Market Route (MN > CAER) & ROI
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ROI ${Formatters.formatPercent(calculation.roiPercentage)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = routeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 0.5.sp,
                                color = TextSubtle
                            )
                        }
                    }
                }
            }

            // Quick Expand Arrow indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (calculation.isEstimatedOrFallback) {
                    Text(
                        text = "• Linha de Base (Cache)",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                } else {
                    Text(
                        text = "• Dados em Tempo Real (${calculation.bestMarket.shortName})",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = BentoEmeraldLight
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (isExpanded) "Recolher" else "Expandir",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expandable Breakdown Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = BentoBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Formula Breakdown Rows
                    BreakdownRow(
                        label = "Materiais Brutos (16 Tábuas + 4 Couros)",
                        value = "${Formatters.formatSilver(calculation.totalRawMatCost)} s",
                        subValue = "Tábua: ${Formatters.formatSilver(calculation.plankUnitPrice)} | Couro: ${Formatters.formatSilver(calculation.leatherUnitPrice)}"
                    )

                    BreakdownRow(
                        label = "Custo Líquido (Após ${(calculation.config.returnRatePercent).toInt()}% RRR)",
                        value = "${Formatters.formatSilver(calculation.netMatCost)} s",
                        highlightColor = BentoEmeraldLight,
                        subValue = "Economia de ${Formatters.formatSilver(calculation.savingsFromRrr)} prata pelo RRR"
                    )

                    if (calculation.item.family.isArtifactWeapon) {
                        BreakdownRow(
                            label = "Custo de Oportunidade do Artefato",
                            value = "${Formatters.formatSilver(calculation.artifactOppCost)} s",
                            subValue = "Artefato Bruto: ${Formatters.formatSilver(calculation.artifactUnitPrice)} (Após ${(calculation.config.marketTax * 100).toInt()}% Taxa)"
                        )
                    }

                    BreakdownRow(
                        label = "Receita Líquida Caerleon",
                        value = "${Formatters.formatSilver(calculation.netRevCaerleon)} s",
                        subValue = "Preço no Mercado: ${Formatters.formatSilver(calculation.itemPriceCaerleon)}"
                    )

                    BreakdownRow(
                        label = "Receita Líquida Mercado Negro",
                        value = "${Formatters.formatSilver(calculation.netRevBlackMarket)} s",
                        highlightColor = BlackMarketPurple,
                        subValue = "Preço de Compra BM: ${Formatters.formatSilver(calculation.itemPriceBlackMarket)}"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Simulator Button
                    OutlinedButton(
                        onClick = { onOpenCalculator(calculation) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_calculator_btn_${calculation.item.id}"),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(BentoBorder, BentoEmerald.copy(alpha = 0.4f)))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Simular",
                            tint = BentoEmeraldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simular Preços e Quantidades",
                            color = BentoEmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    subValue: String? = null,
    highlightColor: Color = TextPrimary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = highlightColor,
                fontSize = 13.sp
            )
        }
        if (subValue != null) {
            Text(
                text = subValue,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
