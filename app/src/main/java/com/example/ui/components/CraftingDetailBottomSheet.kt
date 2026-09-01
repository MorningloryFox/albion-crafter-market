package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PriceOverrideEntity
import com.example.domain.model.ActionRecommendation
import com.example.domain.model.CraftingCalculation
import com.example.domain.usecase.CalculateCraftingUseCase
import com.example.ui.theme.AlbionCrimson
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderCrimson
import com.example.ui.theme.BentoBorderEmerald
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoCrimsonBg
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextSubtle
import com.example.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CraftingDetailBottomSheet(
    calculation: CraftingCalculation,
    onDismiss: () -> Unit,
    onSaveOverride: (PriceOverrideEntity) -> Unit,
    onResetOverride: (String) -> Unit
) {
    var plankPriceText by remember { mutableStateOf(calculation.plankUnitPrice.toLong().toString()) }
    var leatherPriceText by remember { mutableStateOf(calculation.leatherUnitPrice.toLong().toString()) }
    var artifactPriceText by remember { mutableStateOf(calculation.artifactUnitPrice.toLong().toString()) }
    var caerleonPriceText by remember { mutableStateOf(calculation.itemPriceCaerleon.toLong().toString()) }
    var bmPriceText by remember { mutableStateOf(calculation.itemPriceBlackMarket.toLong().toString()) }

    // Live reactive calculation based on local input modifications
    val currentPlankPrice = plankPriceText.toDoubleOrNull() ?: calculation.plankUnitPrice
    val currentLeatherPrice = leatherPriceText.toDoubleOrNull() ?: calculation.leatherUnitPrice
    val currentArtifactPrice = artifactPriceText.toDoubleOrNull() ?: calculation.artifactUnitPrice
    val currentCaerleonPrice = caerleonPriceText.toDoubleOrNull() ?: calculation.itemPriceCaerleon
    val currentBmPrice = bmPriceText.toDoubleOrNull() ?: calculation.itemPriceBlackMarket

    val calculateUseCase = remember { CalculateCraftingUseCase() }
    val liveCalc = calculateUseCase(
        item = calculation.item,
        config = calculation.config,
        itemPriceCaerleon = currentCaerleonPrice,
        itemPriceBlackMarket = currentBmPrice,
        plankUnitPrice = currentPlankPrice,
        leatherUnitPrice = currentLeatherPrice,
        artifactUnitPrice = currentArtifactPrice
    )

    val isCraft = liveCalc.recommendation == ActionRecommendation.CRAFTAR
    val profitColor = if (liveCalc.isProfitable) BentoEmeraldLight else AlbionCrimson

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BentoBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("crafting_detail_bottom_sheet")
        ) {
            // Drag Indicator Pill
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BentoBorder)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AlbionItemImage(item = calculation.item, size = 46.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = calculation.item.fullName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Forma: ${calculation.item.family.morphName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoEmeraldLight
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(BentoSurface, CircleShape)
                        .border(1.dp, BentoBorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Profit Result Bento Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BentoSurface)
                    .border(
                        width = 1.dp,
                        color = if (isCraft) BentoBorderEmerald else BentoBorderCrimson,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LUCRO ESTIMADO NA SIMULAÇÃO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${Formatters.formatSilver(liveCalc.finalProfit)} prata",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = profitColor
                        )
                        Text(
                            text = "ROI: ${Formatters.formatPercent(liveCalc.roiPercentage)} • Melhor Rota: ${liveCalc.bestMarket.displayName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    // Bold Action Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCraft) BentoEmeraldBg else BentoCrimsonBg)
                            .border(
                                width = 1.dp,
                                color = if (isCraft) BentoEmerald.copy(alpha = 0.4f) else AlbionCrimson.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = liveCalc.recommendation.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isCraft) BentoEmeraldLight else AlbionCrimson
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Sobrescrever Preços Unitários (Prata)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Inputs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceInputField(
                    label = "Tábua Unit.",
                    value = plankPriceText,
                    onValueChange = { plankPriceText = it },
                    modifier = Modifier.weight(1f)
                )
                PriceInputField(
                    label = "Couro Unit.",
                    value = leatherPriceText,
                    onValueChange = { leatherPriceText = it },
                    modifier = Modifier.weight(1f)
                )
            }

            if (calculation.item.family.isArtifactWeapon) {
                Spacer(modifier = Modifier.height(8.dp))
                PriceInputField(
                    label = "Preço Unitário do Artefato (Prata)",
                    value = artifactPriceText,
                    onValueChange = { artifactPriceText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceInputField(
                    label = "Preço Caerleon",
                    value = caerleonPriceText,
                    onValueChange = { caerleonPriceText = it },
                    modifier = Modifier.weight(1f)
                )
                PriceInputField(
                    label = "Preço Mercado Negro",
                    value = bmPriceText,
                    onValueChange = { bmPriceText = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BentoBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Formula Spec Card
            Text(
                text = "Tabela de Requisitos de Fabricação",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "• 16x Tábuas Refinadas", fontSize = 11.sp, color = TextMuted)
                Text(text = "• 4x Couros Refinados", fontSize = 11.sp, color = TextMuted)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (calculation.item.family.isArtifactWeapon) "• 1x Artefato Específico" else "• Sem Artefato (Arma Base)",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(text = "• Taxa de Mercado: ${(calculation.config.marketTax * 100).toInt()}%", fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons: Reset & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onResetOverride(calculation.item.id)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(BentoBorder, BentoBorderSubtle))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Restaurar",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Restaurar API", color = TextSecondary, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onSaveOverride(
                            PriceOverrideEntity(
                                itemId = calculation.item.id,
                                customCaerleonPrice = currentCaerleonPrice,
                                customBlackMarketPrice = currentBmPrice,
                                customPlankPrice = currentPlankPrice,
                                customLeatherPrice = currentLeatherPrice,
                                customArtifactPrice = currentArtifactPrice
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Salvar",
                        tint = BentoBg,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Salvar Personalizado", color = BentoBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PriceInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp, color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BentoEmerald,
            unfocusedBorderColor = BentoBorderSubtle,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = BentoSurfaceElevated,
            unfocusedContainerColor = BentoSurfaceElevated
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}
