package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CraftingCalculation
import com.example.domain.model.Enchantment
import com.example.domain.model.StaffFamily
import com.example.domain.model.Tier
import com.example.ui.components.ShimmerStaffCard
import com.example.ui.components.StaffItemCard
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderEmerald
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Tier4Color
import com.example.ui.theme.Tier5Color
import com.example.ui.theme.Tier6Color
import com.example.ui.theme.Tier7Color
import com.example.ui.theme.Tier8Color
import com.example.ui.viewmodel.CraftingUiState
import com.example.ui.viewmodel.SortOption

@Composable
fun CalculationsListScreen(
    state: CraftingUiState,
    onNavigateBack: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onFamilySelected: (StaffFamily?) -> Unit,
    onTierSelected: (Tier?) -> Unit,
    onEnchantmentSelected: (Enchantment?) -> Unit,
    onToggleOnlyProfitable: () -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onOpenCalculator: (CraftingCalculation) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
    ) {
        // Top Bento Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("back_button")
                        .background(BentoSurface, CircleShape)
                        .border(1.dp, BentoBorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Cálculos de Fabricação",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${state.filteredCalculations.size} Cajados Ativos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoEmeraldLight
                    )
                }
            }

            // Sort Menu Button
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("sort_menu_btn")
                        .background(BentoSurface, CircleShape)
                        .border(1.dp, BentoBorderEmerald, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Ordenar",
                        tint = BentoEmeraldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier
                        .background(BentoSurfaceElevated)
                        .border(1.dp, BentoBorder, RoundedCornerShape(12.dp))
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.displayName,
                                    color = if (state.sortBy == option) BentoEmeraldLight else TextPrimary,
                                    fontWeight = if (state.sortBy == option) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortSelected(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Search Field styled as Bento Input
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("search_field"),
            placeholder = { Text("Buscar por nome, forma (ex: Lobo) ou tier...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = TextMuted
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar",
                            tint = TextMuted
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BentoEmerald,
                unfocusedBorderColor = BentoBorder,
                focusedContainerColor = BentoSurface,
                unfocusedContainerColor = BentoSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Filter Bar - Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" Family Chip
            FilterChip(
                selected = state.selectedFamilyFilter == null,
                onClick = { onFamilySelected(null) },
                label = { Text("Todas as Famílias", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BentoEmerald,
                    selectedLabelColor = BentoBg,
                    containerColor = BentoSurface,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = BentoBorderSubtle,
                    selectedBorderColor = BentoEmerald,
                    enabled = true,
                    selected = state.selectedFamilyFilter == null
                )
            )

            // Family Chips
            StaffFamily.entries.forEach { family ->
                val isSelected = state.selectedFamilyFilter == family
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onFamilySelected(if (isSelected) null else family)
                    },
                    label = { Text(family.displayName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BentoEmerald,
                        selectedLabelColor = BentoBg,
                        containerColor = BentoSurface,
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

        // Secondary Filters: Tier + Enchantment + Only Profitable Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tier chips
            Tier.entries.forEach { tier ->
                val tierColor = when (tier) {
                    Tier.T4 -> Tier4Color
                    Tier.T5 -> Tier5Color
                    Tier.T6 -> Tier6Color
                    Tier.T7 -> Tier7Color
                    Tier.T8 -> Tier8Color
                }
                val isSelected = state.selectedTierFilter == tier
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) tierColor.copy(alpha = 0.22f) else BentoSurface)
                        .border(1.dp, if (isSelected) tierColor else BentoBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onTierSelected(if (isSelected) null else tier) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tier.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) tierColor else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Enchantment Chips
            Enchantment.entries.forEach { ench ->
                val isSelected = state.selectedEnchantmentFilter == ench
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) BentoEmeraldBg else BentoSurface)
                        .border(1.dp, if (isSelected) BentoEmerald else BentoBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onEnchantmentSelected(if (isSelected) null else ench) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = ench.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BentoEmeraldLight else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Profitable only toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BentoSurface)
                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(10.dp))
                    .clickable { onToggleOnlyProfitable() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Apenas Lucrativos",
                    fontSize = 11.sp,
                    color = if (state.onlyProfitable) BentoEmeraldLight else TextSecondary,
                    fontWeight = if (state.onlyProfitable) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = state.onlyProfitable,
                    onCheckedChange = { onToggleOnlyProfitable() },
                    modifier = Modifier.size(width = 34.dp, height = 20.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BentoBg,
                        checkedTrackColor = BentoEmerald,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BentoSurfaceElevated
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calculations List or Shimmer Loading State
        if (state.isLoading || state.isRefreshing) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(6) {
                    ShimmerStaffCard()
                }
            }
        } else if (state.filteredCalculations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nenhum resultado encontrado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tente ajustar seus filtros ou termos de busca.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("calculations_lazy_column"),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.filteredCalculations,
                    key = { it.item.id }
                ) { calculation ->
                    StaffItemCard(
                        calculation = calculation,
                        onOpenCalculator = onOpenCalculator
                    )
                }
            }
        }
    }
}
