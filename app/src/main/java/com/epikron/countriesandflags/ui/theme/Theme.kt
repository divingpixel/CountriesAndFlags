package com.epikron.countriesandflags.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.epikron.countriesandflags.R

private val darkThemeColors = darkColorScheme(
    primary = DarkThemeColors.primary,
    onPrimary = DarkThemeColors.onPrimary,
    primaryContainer = DarkThemeColors.primaryContainer,
    onPrimaryContainer = DarkThemeColors.onPrimaryContainer,
    secondary = DarkThemeColors.secondary,
    onSecondary = DarkThemeColors.onSecondary,
    secondaryContainer = DarkThemeColors.secondaryContainer,
    onSecondaryContainer = DarkThemeColors.onSecondaryContainer,
    tertiary = DarkThemeColors.tertiary,
    onTertiary = DarkThemeColors.onTertiary,
    error = DarkThemeColors.error,
    errorContainer = DarkThemeColors.errorContainer,
    onError = DarkThemeColors.onError,
    onErrorContainer = DarkThemeColors.onErrorContainer,
    background = DarkThemeColors.background,
    onBackground = DarkThemeColors.onBackground,
    surface = DarkThemeColors.surface,
    onSurface = DarkThemeColors.onSurface,
    outline = DarkThemeColors.outline,
    outlineVariant = DarkThemeColors.outlineVariant,
    scrim = DarkThemeColors.scrim
)

private val lightThemeColors = darkThemeColors.copy(
    tertiary = LightThemeColors.tertiary,
    onTertiary = LightThemeColors.onTertiary,
    surface = LightThemeColors.surface,
    onSurface = LightThemeColors.onSurface,
    background = LightThemeColors.background,
    onBackground = LightThemeColors.onBackground
)

@Composable
fun SwipingCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
            window.statusBarColor = (if (darkTheme) darkThemeColors.background else lightThemeColors.background).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.P)) darkTheme else !darkTheme
            WindowCompat.setDecorFitsSystemWindows(window, (Build.VERSION.SDK_INT < Build.VERSION_CODES.P))
        }
    }

    val colorScheme = when {
        darkTheme || (Build.VERSION.SDK_INT < 29) -> darkThemeColors
        else -> lightThemeColors
    }

    val background = if (darkTheme || (Build.VERSION.SDK_INT < 29)) {
        R.mipmap.polygonal_world_map
    } else {
        R.mipmap.polygonal_world_map_day
    }

    Image(
        painter = painterResource(id = background),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = mainTypography,
        content = content,
        shapes = Shapes
    )
}
