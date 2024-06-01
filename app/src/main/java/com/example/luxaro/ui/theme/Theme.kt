package com.example.luxaro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkBlueColorScheme = CustomColorScheme(
    sideNavigationBackground = MediumPersianBlue2,
    windowLoadingAnimationColor = Color.White,
    windowBackground = AteneoBlue,
    windowTextColor = Color.White,
    windowIconColor = Color.White,
    previewCardBackground = MediumPersianBlue1,
    previewCardTextColor = Color.White,
    previewCardIconColor = Color.White,
    moreInfoCardBackground = MediumPersianBlue1,
    moreInfoCardTextColor = Color.White,
    moreInfoCardIconColor = Color.White,
    moreInfoCardContactUsButtonBackground = VividCerulean,
    moreInfoCardContactUsButtonTextColor = Color.White,
    contactUsCardBackground = OceanBoatBlue2,
    contactUsCardButtonBackground = VividCerulean,
    contactUsCardButtonTextColor = Color.White,
    contactUsCardWhatsappButtonBackground = LimeGreen,
    profileTextFieldTitleTextColor = Color.White,
    profileTextFieldBackground = RichElectricBlue,
    profileTextFieldCursorColor = Color.White,
    profileTextFieldTextColor = Color.White,
    profileTextFieldIconColor = Color.White,
    profileTextFieldLoadingAnimationColor = Color.White,
    logOutButtonBackground = BlueCola,
    logOutButtonTextColor = Color.White,
    deleteAccountPreviewButtonBackground = MediumPersianBlue2,
    deleteAccountPreviewButtonTextColor = Color.White,
    deleteAccountButtonBackground = DarkCandyAppleRed,
    deleteAccountButtonTextColor = Color.White,
    closeSearchIconColor = Color.White,
    searchTextFieldBackground = RichElectricBlue,
    searchTextFieldCursorColor = Color.White,
    searchTextFieldTextColor = Color.White,
    searchTextFieldIconColor = Color.White,
    searchIconColor = Color.White,
    searchResultCardBackground = MediumPersianBlue2,
    searchResultCardTextColor = Color.White,
)

private val LightBlueColorScheme = CustomColorScheme(
    sideNavigationBackground = Color.Unspecified,
    windowLoadingAnimationColor = Color.Unspecified,
    windowBackground = Color.Unspecified,
    windowTextColor = Color.Unspecified,
    windowIconColor = Color.Unspecified,
    previewCardBackground = Color.Unspecified,
    previewCardTextColor = Color.Unspecified,
    previewCardIconColor = Color.Unspecified,
    moreInfoCardBackground = Color.Unspecified,
    moreInfoCardTextColor = Color.Unspecified,
    moreInfoCardIconColor = Color.Unspecified,
    moreInfoCardContactUsButtonBackground = Color.Unspecified,
    moreInfoCardContactUsButtonTextColor = Color.Unspecified,
    contactUsCardBackground = Color.Unspecified,
    contactUsCardButtonBackground = Color.Unspecified,
    contactUsCardButtonTextColor = Color.Unspecified,
    contactUsCardWhatsappButtonBackground = Color.Unspecified,
    profileTextFieldTitleTextColor = Color.Unspecified,
    profileTextFieldBackground = Color.Unspecified,
    profileTextFieldTextColor = Color.Unspecified,
    profileTextFieldIconColor = Color.Unspecified,
    profileTextFieldLoadingAnimationColor = Color.Unspecified,
    logOutButtonBackground = Color.Unspecified,
    logOutButtonTextColor = Color.Unspecified,
    deleteAccountPreviewButtonBackground = Color.Unspecified,
    deleteAccountPreviewButtonTextColor = Color.Unspecified,
    deleteAccountButtonBackground = Color.Unspecified,
    deleteAccountButtonTextColor = Color.Unspecified,
    closeSearchIconColor = Color.Unspecified,
    searchTextFieldBackground = Color.Unspecified,
    searchTextFieldTextColor = Color.Unspecified,
    searchTextFieldIconColor = Color.Unspecified,
    searchIconColor = Color.Unspecified,
    searchResultCardBackground = Color.Unspecified,
    searchResultCardTextColor = Color.Unspecified,
)


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LuxaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LightColorScheme else DarkColorScheme
    val customColor = if (darkTheme) DarkBlueColorScheme else LightBlueColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColor) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}