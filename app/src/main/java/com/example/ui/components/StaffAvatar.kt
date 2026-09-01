package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.ShapeshifterItem

@Composable
fun StaffAvatar(
    item: ShapeshifterItem,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    AlbionItemImage(
        item = item,
        size = size,
        modifier = modifier
    )
}
