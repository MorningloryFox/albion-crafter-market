package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Formatters {

    private val silverFormatter = DecimalFormat("#,###")
    private val percentFormatter = DecimalFormat("0.0")

    fun formatSilver(amount: Double): String {
        return silverFormatter.format(amount.toLong())
    }

    fun formatSilverCompact(amount: Double): String {
        val absVal = abs(amount)
        val sign = if (amount < 0) "-" else ""
        return when {
            absVal >= 1_000_000_000 -> "$sign${percentFormatter.format(absVal / 1_000_000_000.0)}B"
            absVal >= 1_000_000 -> "$sign${percentFormatter.format(absVal / 1_000_000.0)}M"
            absVal >= 1_000 -> "$sign${percentFormatter.format(absVal / 1_000.0)}K"
            else -> "$sign${absVal.toLong()}"
        }
    }

    fun formatSilverSigned(amount: Double): String {
        val formatted = formatSilverCompact(amount)
        return if (amount > 0) "+$formatted" else formatted
    }

    fun formatPercent(percent: Double): String {
        val sign = if (percent > 0) "+" else ""
        return "$sign${percentFormatter.format(percent)}%"
    }

    fun formatTimestamp(millis: Long): String {
        if (millis <= 0) return "Never updated"
        val diff = System.currentTimeMillis() - millis
        val minutes = diff / (1000 * 60)
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(millis))
        }
    }
}
