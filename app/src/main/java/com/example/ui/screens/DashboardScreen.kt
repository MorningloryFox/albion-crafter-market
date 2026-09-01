package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ActionRecommendation
import com.example.domain.model.CraftingCalculation
import com.example.ui.components.AlbionItemImage
import com.example.ui.components.ShimmerOpportunityCard
import com.example.ui.components.StaffAvatar
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderEmerald
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoOrange
import com.example.ui.theme.BentoOrangeBg
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceCard
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BlackMarketPurple
import com.example.ui.theme.CaerleonBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextSubtle
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.CraftingUiState

@Composable
fun DashboardScreen(
    state: CraftingUiState,
    onNavigateToCalculations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRefreshClicked: () -> Unit,
    onPremiumToggled: (Boolean) -> Unit,
    onRrrSelected: (Double) -> Unit,
    onCalculationClicked: (CraftingCalculation) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header: App Branding + API Engine Indicator & Settings
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ALBION CRAFT MASTER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = BentoEmeraldLight
                        )
                    }
                    Text(
                        text = "Árvore de Metamorfo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sincronizar Button with Spinner
                    IconButton(
                        onClick = onRefreshClicked,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("refresh_prices_button")
                            .background(BentoSurface, CircleShape)
                            .border(1.dp, BentoBorderSubtle, CircleShape)
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = BentoEmerald,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sincronizar Preços",
                                tint = BentoEmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("settings_button")
                            .background(BentoSurface, CircleShape)
                            .border(1.dp, BentoBorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // BENTO CARD 1: Live Engine Global Configuration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crafting_config_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        colors = listOf(BentoBorderSubtle, BentoBorder)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Card Subheader
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Configuração",
                                tint = BentoEmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONFIGURAÇÃO DE FABRICAÇÃO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextMuted
                            )
                        }

                        // Status Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(BentoEmeraldBg)
                                .border(1.dp, BentoEmerald.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (state.isRefreshing) "ATUALIZANDO..." else "TEMPO REAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoEmeraldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // RRR Selector Chips
                    Text(
                        text = "Taxa de Retorno de Recursos (RRR)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val rrrOptions = listOf(
                            Pair(0.152, "15.2% (Sem Bônus)"),
                            Pair(0.248, "24.8% (Caerleon)"),
                            Pair(0.479, "47.9% (Com Foco)")
                        )

                        rrrOptions.forEach { (rate, label) ->
                            val isSelected = kotlin.math.abs(state.config.resourceReturnRate - rate) < 0.005
                            FilterChip(
                                selected = isSelected,
                                onClick = { onRrrSelected(rate) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoEmerald,
                                    selectedLabelColor = BentoBg,
                                    containerColor = BentoSurfaceElevated,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = BentoBorderSubtle,
                                    selectedBorderColor = BentoEmerald,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Premium Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BentoSurfaceElevated)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Status Premium",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (state.config.userPremium) "Taxa de Mercado reduzida para 4%" else "Taxa de Mercado padrão de 8%",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = state.config.userPremium,
                            onCheckedChange = onPremiumToggled,
                            modifier = Modifier.testTag("premium_toggle_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BentoBg,
                                checkedTrackColor = BentoEmerald,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BentoSurfaceCard
                            )
                        )
                    }
                }
            }
        }

        // BENTO CARD 2: 2-Column Analytics Module (Caerleon/BM Telemetry + Top Highlight)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Tile: Market Telemetry
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(BentoBorderSubtle, BentoBorder)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Mercado",
                                tint = BlackMarketPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TELEMETRIA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${state.totalProfitableCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = BentoEmeraldLight
                        )
                        Text(
                            text = "Opções Lucrativas",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ROI Médio",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = Formatters.formatPercent(state.averageRoi),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = BentoEmerald
                            )
                        }
                    }
                }

                // Right Tile: Top Highlight Quick Card
                val topOpportunity = state.topOpportunities.firstOrNull()
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = topOpportunity != null) {
                            topOpportunity?.let { onCalculationClicked(it) }
                        },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(BentoBorderEmerald, BentoBorder)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Top Lucro",
                                tint = BentoEmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TOP OPORTUNIDADE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = BentoEmeraldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (topOpportunity != null) {
                            Text(
                                text = Formatters.formatSilver(topOpportunity.finalProfit),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = BentoEmeraldLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = topOpportunity.item.fullName,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ROI ${Formatters.formatPercent(topOpportunity.roiPercentage)}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BentoEmerald
                            )
                        } else {
                            Text(
                                text = "Calculando...",
                                fontSize = 14.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // BENTO SECTION: Top Opportunities List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Oportunidades",
                        tint = BentoEmeraldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OPORTUNIDADES DE MAIOR ROI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "Ver Todos (${state.filteredCalculations.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoEmeraldLight,
                    modifier = Modifier
                        .testTag("view_all_calculations_button")
                        .clickable { onNavigateToCalculations() }
                )
            }
        }

        // Top 5 Calculation Cards or Shimmer skeleton placeholders
        if (state.isLoading || state.isRefreshing) {
            items(4) {
                ShimmerOpportunityCard()
            }
        } else if (state.topOpportunities.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurfaceElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma oportunidade lucrativa encontrada com os filtros atuais.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(
                items = state.topOpportunities,
                key = { "dash_${it.item.id}" }
            ) { calc ->
                OpportunityCard(
                    calculation = calc,
                    onClick = { onCalculationClicked(calc) }
                )
            }
        }
    }
}

@Composable
private fun OpportunityCard(
    calculation: CraftingCalculation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("opportunity_card_${calculation.item.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(BentoBorderSubtle, BentoBorder)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weapon Icon loaded with Coil
            AlbionItemImage(item = calculation.item, size = 42.dp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = calculation.item.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Forma: ${calculation.item.family.morphName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatSilverSigned(calculation.finalProfit),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoEmeraldLight
                )
                Text(
                    text = "ROI ${Formatters.formatPercent(calculation.roiPercentage)}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoEmerald
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver Detalhes",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
