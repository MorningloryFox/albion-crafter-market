package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CraftingConfig
import com.example.ui.theme.AlbionGold
import com.example.ui.theme.AlbionGoldDark
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoEmeraldDark
import com.example.ui.theme.BentoEmeraldLight
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoSurfaceItem
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextSubtle
import java.text.DecimalFormat

@Composable
fun GlobalConfigCard(
    config: CraftingConfig,
    onPremiumChanged: (Boolean) -> Unit,
    onRrrChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("0.0")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_config_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                colors = listOf(BentoBorderSubtle, BentoBorder)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Premium Status Bento Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (config.userPremium) BentoEmeraldBg else BentoSurfaceElevated)
                            .border(
                                1.dp,
                                if (config.userPremium) BentoEmerald.copy(alpha = 0.3f) else BentoBorder,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Premium",
                            tint = if (config.userPremium) BentoEmerald else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Premium Status",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSubtle,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (config.userPremium) "4% Tax Active" else "8% Tax Active",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (config.userPremium) TextPrimary else TextSecondary
                        )
                    }
                }

                Switch(
                    checked = config.userPremium,
                    onCheckedChange = onPremiumChanged,
                    modifier = Modifier.testTag("premium_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BentoBg,
                        checkedTrackColor = BentoEmerald,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BentoSurfaceElevated
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Resource Return Rate (RRR) Bento Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RESOURCE RETURN RATE (RRR)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${decimalFormat.format(config.returnRatePercent)}%",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoEmeraldLight
                    )
                }

                // Bento Sleek Slider
                Slider(
                    value = config.resourceReturnRate.toFloat(),
                    onValueChange = { onRrrChanged(it.toDouble()) },
                    valueRange = 0.0f..0.55f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .testTag("rrr_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = BentoEmerald,
                        activeTrackColor = BentoEmerald,
                        inactiveTrackColor = BentoSurfaceElevated
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Quick Buttons styled as Bento Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RrrPresetChip(
                    label = "24.8% Caerleon",
                    targetRrr = 0.248,
                    currentRrr = config.resourceReturnRate,
                    onClick = { onRrrChanged(0.248) },
                    modifier = Modifier.weight(1.1f)
                )
                RrrPresetChip(
                    label = "47.9% Focus",
                    targetRrr = 0.479,
                    currentRrr = config.resourceReturnRate,
                    onClick = { onRrrChanged(0.479) },
                    modifier = Modifier.weight(1f)
                )
                RrrPresetChip(
                    label = "15.2% City",
                    targetRrr = 0.152,
                    currentRrr = config.resourceReturnRate,
                    onClick = { onRrrChanged(0.152) },
                    modifier = Modifier.weight(0.9f)
                )
                RrrPresetChip(
                    label = "0% None",
                    targetRrr = 0.0,
                    currentRrr = config.resourceReturnRate,
                    onClick = { onRrrChanged(0.0) },
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}

@Composable
private fun RrrPresetChip(
    label: String,
    targetRrr: Double,
    currentRrr: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = Math.abs(currentRrr - targetRrr) < 0.005
    val borderCol = if (isSelected) BentoEmerald.copy(alpha = 0.6f) else BentoBorderSubtle
    val bgCol = if (isSelected) BentoEmeraldBg else BentoSurfaceItem
    val textCol = if (isSelected) BentoEmeraldLight else TextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgCol)
            .border(1.dp, borderCol, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textCol,
            maxLines = 1
        )
    }
}

