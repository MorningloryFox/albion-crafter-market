package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BentoDarkColorScheme = darkColorScheme(
    primary = BentoEmerald,
    onPrimary = BentoBg,
    primaryContainer = BentoEmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = AlbionGold,
    onSecondary = BentoBg,
    secondaryContainer = AlbionGoldDark,
    onSecondaryContainer = TextPrimary,
    tertiary = CaerleonBlue,
    onTertiary = BentoBg,
    background = BentoBg,
    onBackground = TextSecondary,
    surface = BentoSurface,
    onSurface = TextPrimary,
    surfaceVariant = BentoSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BentoBorder,
    outlineVariant = BentoBorderSubtle,
    error = AlbionCrimson,
    onError = BentoBg
)

@Composable
fun AlbionCraftTheme(
    darkTheme: Boolean = true, // Force Gaming Bento Dark Theme
    content: @Composable () -> Unit
) {
    val colorScheme = BentoDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BentoBg.toArgb()
                window.navigationBarColor = BentoSurfaceBottomNav.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
