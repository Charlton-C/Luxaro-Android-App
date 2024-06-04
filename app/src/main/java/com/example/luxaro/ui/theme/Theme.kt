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
    previewCardBorderStrokeColor = Color.Transparent,
    previewCardTextColor = Color.White,
    previewCardIconColor = Color.White,
    moreInfoCardBackground = MediumPersianBlue1,
    moreInfoCardBorderStrokeColor = Color.Transparent,
    moreInfoCardTextColor = Color.White,
    moreInfoCardIconColor = Color.White,
    moreInfoCardContactUsButtonBackground = VividCerulean,
    moreInfoCardContactUsButtonTextColor = Color.White,
    contactUsCardBackground = OceanBoatBlue2,
    contactUsCardBorderStrokeColor = Color.Transparent,
    contactUsCardButtonBackground = VividCerulean,
    contactUsCardButtonBorderStrokeColor = Color.Transparent,
    contactUsCardButtonTextColor = Color.White,
    contactUsCardWhatsappButtonBackground = LimeGreen,
    contactUsCardWhatsappButtonBorderStrokeColor = Color.Transparent,
    profileTextFieldTitleTextColor = Color.White,
    profileTextFieldBackground = RichElectricBlue,
    profileTextFieldBorderColor = Color.Transparent,
    profileTextFieldCursorColor = Color.White,
    profileTextFieldTextColor = Color.White,
    profileTextFieldIconColor = Color.White,
    profileTextFieldLoadingAnimationColor = Color.White,
    logOutButtonBackground = BlueCola,
    logOutButtonBorderStrokeColor = Color.Transparent,
    logOutButtonTextColor = Color.White,
    deleteAccountPreviewButtonBackground = MediumPersianBlue2,
    deleteAccountPreviewButtonTextColor = Color.White,
    deleteAccountButtonBackground = DarkCandyAppleRed,
    deleteAccountButtonBorderStrokeColor = Color.Transparent,
    deleteAccountButtonTextColor = Color.White,
    closeSearchIconColor = Color.White,
    searchTextFieldBackground = RichElectricBlue,
    searchTextFieldBorderColor = Color.Transparent,
    searchTextFieldCursorColor = Color.White,
    searchTextFieldTextColor = Color.White,
    searchTextFieldIconColor = Color.White,
    searchIconColor = Color.White,
    searchResultCardBackground = MediumPersianBlue2,
    searchResultCardBorderStrokeColor = Color.Transparent,
    searchResultCardTextColor = Color.White,
)

private val LightBlueColorScheme = CustomColorScheme(
    sideNavigationBackground = White500,
    windowLoadingAnimationColor = OceanBoatBlue3,
    windowBackground = White200,
    windowTextColor = Color.Black,
    windowIconColor = OceanBoatBlue3,
    previewCardBackground = White200,
    previewCardBorderStrokeColor = Black200,
    previewCardTextColor = Color.Black,
    previewCardIconColor = OceanBoatBlue3,
    moreInfoCardBackground = White200,
    moreInfoCardBorderStrokeColor = Black200,
    moreInfoCardTextColor = Color.Black,
    moreInfoCardIconColor = OceanBoatBlue3,
    moreInfoCardContactUsButtonBackground = VividCerulean,
    moreInfoCardContactUsButtonTextColor = Color.Black,
    contactUsCardBackground = White200,
    contactUsCardBorderStrokeColor = Black200,
    contactUsCardButtonBackground = White500,
    contactUsCardButtonBorderStrokeColor = Color.Transparent,
    contactUsCardButtonTextColor = Color.Black,
    contactUsCardWhatsappButtonBackground = ScreaminGreen,
    contactUsCardWhatsappButtonBorderStrokeColor = Color.Transparent,
    profileTextFieldTitleTextColor = Color.Black,
    profileTextFieldBackground = Color.White,
    profileTextFieldBorderColor = Black200,
    profileTextFieldCursorColor = Color.Black,
    profileTextFieldTextColor = Color.Black,
    profileTextFieldIconColor = OceanBoatBlue3,
    profileTextFieldLoadingAnimationColor = Color.Black,
    logOutButtonBackground = Color.White,
    logOutButtonBorderStrokeColor = Black200,
    logOutButtonTextColor = Color.Black,
    deleteAccountPreviewButtonBackground = White500,
    deleteAccountPreviewButtonTextColor = Color.Black,
    deleteAccountButtonBackground = ElectricRed,
    deleteAccountButtonBorderStrokeColor = Color.Transparent,
    deleteAccountButtonTextColor = Color.White,
    closeSearchIconColor = Color.Black,
    searchTextFieldBackground = Color.White,
    searchTextFieldBorderColor = Black200,
    searchTextFieldCursorColor = Color.Black,
    searchTextFieldTextColor = Color.Black,
    searchTextFieldIconColor = Color.Black,
    searchIconColor = Color.Black,
    searchResultCardBackground = White200,
    searchResultCardBorderStrokeColor = Black200,
    searchResultCardTextColor = Color.Black,
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