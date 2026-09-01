package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.StaffFamily
import com.example.ui.theme.AlbionCrimson
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.CraftingUiState

@Composable
fun SettingsScreen(
    state: CraftingUiState,
    onNavigateBack: () -> Unit,
    onRefreshClicked: () -> Unit,
    onClearCacheClicked: () -> Unit,
    onFamilyTrackedToggled: (StaffFamily, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("settings_back_btn")
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
                    text = "Configurações e API",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Albion Data Project • Caerleon ⇄ Mercado Negro",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoEmeraldLight
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("settings_scroll_column"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // API Status Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(BentoBorderSubtle, BentoBorder)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BentoEmeraldBg)
                                        .border(1.dp, BentoEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Api,
                                        contentDescription = "Status da API",
                                        tint = BentoEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Albion Data Project API",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(BentoEmeraldBg)
                                    .border(1.dp, BentoEmerald.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "ATIVO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoEmeraldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsDetailRow(label = "Endpoint:", value = "Caerleon & Mercado Negro")
                        SettingsDetailRow(label = "Taxa de Retorno (RRR):", value = "${(state.config.returnRatePercent).toInt()}% (Caerleon)")
                        SettingsDetailRow(label = "Taxa de Mercado:", value = "${(state.config.marketTax * 100).toInt()}% (${if (state.config.userPremium) "Com Premium" else "Sem Premium"})")
                        SettingsDetailRow(label = "Itens Rastreados:", value = "${state.filteredCalculations.size} variantes de armas")

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onRefreshClicked,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoEmerald)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sincronizar",
                                    tint = BentoBg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Sincronizar", color = BentoBg, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onClearCacheClicked,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(BentoBorder, AlbionCrimson.copy(alpha = 0.5f)))
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Limpar Cache",
                                    tint = AlbionCrimson,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Limpar Cache", color = AlbionCrimson, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Tracked Staff Families Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(BentoBorderSubtle, BentoBorder)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BentoSurfaceElevated)
                                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Famílias de Cajados",
                                    tint = BentoEmeraldLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Famílias de Cajados Rastreadas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Selecione quais árvores de Metamorfo calcular",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StaffFamily.entries.forEach { family ->
                            val isChecked = state.trackedFamilies.contains(family)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${family.displayName} (${family.morphName})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = family.morphDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        maxLines = 2
                                    )
                                }

                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onFamilyTrackedToggled(family, it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = BentoEmerald,
                                        checkmarkColor = BentoBg,
                                        uncheckedColor = BentoBorder
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Crafting Formulas Logic Engine Guide Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(BentoBorderSubtle, BentoBorder)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Fórmulas",
                                tint = BentoEmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fórmulas do Motor de Decisão",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FormulaItem(
                            title = "1. Custo dos Materiais Brutos",
                            formula = "total_mat_bruto = (preco_tabua * 16) + (preco_couro * 4)",
                            explanation = "Calcula o custo total dos recursos refinados consumidos em 1 fabricação."
                        )

                        FormulaItem(
                            title = "2. Custo Líquido com Taxa de Retorno (RRR)",
                            formula = "custo_liquido = total_mat_bruto * (1.0 - taxa_rrr)",
                            explanation = "Aplica os 24.8% de retorno de recursos das oficinas de fabricação em Caerleon."
                        )

                        FormulaItem(
                            title = "3. Custo de Oportunidade do Artefato",
                            formula = "custo_op_artefato = preco_artefato * (1.0 - taxa_mercado)",
                            explanation = "O valor líquido em prata que você receberia vendendo o artefato diretamente após impostos."
                        )

                        FormulaItem(
                            title = "4. Receita Líquida (Caerleon vs Mercado Negro)",
                            formula = "receita_liquida = preco_venda * (1.0 - taxa_mercado)",
                            explanation = "Determina o melhor canal de venda comparando Caerleon com as ordens de compra do Mercado Negro."
                        )

                        FormulaItem(
                            title = "5. Lucro Final e Recomendação de Ação",
                            formula = "lucro_final = melhor_receita - custo_liquido - custo_op_artefato",
                            explanation = "Recomenda CRAFTAR se o lucro líquido superar a venda dos componentes brutos; caso contrário recomenda VENDER CRU."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FormulaItem(
    title: String,
    formula: String,
    explanation: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = BentoEmeraldLight
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BentoSurfaceElevated)
                .border(1.dp, BentoBorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = formula,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BentoEmeraldLight
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
