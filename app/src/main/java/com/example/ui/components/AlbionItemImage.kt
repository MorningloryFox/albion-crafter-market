package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.domain.model.Enchantment
import com.example.domain.model.ShapeshifterItem
import com.example.domain.model.Tier
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldBg
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.Ench0Color
import com.example.ui.theme.Ench1Color
import com.example.ui.theme.Ench2Color
import com.example.ui.theme.Ench3Color
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.Tier4Color
import com.example.ui.theme.Tier5Color
import com.example.ui.theme.Tier6Color
import com.example.ui.theme.Tier7Color
import com.example.ui.theme.Tier8Color

/**
 * Robust Coil item image loader for Albion Online items with shimmer loading and fallback.
 */
@Composable
fun AlbionItemImage(
    item: ShapeshifterItem,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showTierBadge: Boolean = true,
    showEnchantmentDots: Boolean = true
) {
    val tierColor = when (item.tier) {
        Tier.T4 -> Tier4Color
        Tier.T5 -> Tier5Color
        Tier.T6 -> Tier6Color
        Tier.T7 -> Tier7Color
        Tier.T8 -> Tier8Color
    }

    val enchColor = when (item.enchantment) {
        Enchantment.NONE -> Ench0Color
        Enchantment.UNCOMMON -> Ench1Color
        Enchantment.RARE -> Ench2Color
        Enchantment.EXCEPTIONAL -> Ench3Color
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        tierColor.copy(alpha = 0.22f),
                        BentoSurfaceElevated
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(tierColor, tierColor.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Coil SubcomposeAsyncImage with dedicated loading and error states
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.fullName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect(
                            shape = RoundedCornerShape(8.dp),
                            baseColor = BentoSurfaceElevated
                        )
                )
            },
            error = {
                // Fallback custom stylized icon when offline or render API unavailable
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ícone de Fallback",
                        tint = tierColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(size * 0.45f)
                    )
                }
            }
        )

        // Tier Badge (Top-Left)
        if (showTierBadge) {
            val tierFontSize = (size.value * 0.16f).coerceAtLeast(8f).sp
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BentoBg.copy(alpha = 0.9f))
                    .border(0.8.dp, tierColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 0.5.dp)
            ) {
                Text(
                    text = "${item.tier.label}${item.enchantment.label}",
                    fontSize = tierFontSize,
                    fontWeight = FontWeight.Bold,
                    color = tierColor
                )
            }
        }

        // Enchantment indicator dots (Bottom-Right)
        if (showEnchantmentDots && item.enchantment.level > 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-3).dp, y = (-3).dp)
                    .clip(CircleShape)
                    .background(BentoBg.copy(alpha = 0.85f))
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(item.enchantment.level) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 0.7.dp)
                            .size((size.value * 0.09).dp.coerceAtLeast(4.dp))
                            .background(enchColor, CircleShape)
                    )
                }
            }
        }
    }
}
