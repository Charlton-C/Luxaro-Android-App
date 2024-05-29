package com.example.luxaro.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColorScheme(
    var sideNavigationBackground: Color = Color.Unspecified,
    var windowLoadingAnimationColor: Color = Color.Unspecified,
    var windowBackground: Color = Color.Unspecified,
    var windowTextColor: Color = Color.Unspecified,
    var windowIconColor: Color = Color.Unspecified,
    var previewCardBackground: Color = Color.Unspecified,
    var previewCardTextColor: Color = Color.Unspecified,
    var previewCardIconColor: Color = Color.Unspecified,
    var moreInfoCardBackground: Color = Color.Unspecified,
    var moreInfoCardTextColor: Color = Color.Unspecified,
    var moreInfoCardIconColor: Color = Color.Unspecified,
    var moreInfoCardContactUsButtonBackground: Color = Color.Unspecified,
    var moreInfoCardContactUsButtonTextColor: Color = Color.Unspecified,
    var contactUsCardBackground: Color = Color.Unspecified,
    var contactUsCardButtonBackground: Color = Color.Unspecified,
    var contactUsCardButtonTextColor: Color = Color.Unspecified,
    var contactUsCardWhatsappButtonBackground: Color = Color.Unspecified,
    var profileTextFieldTitleTextColor: Color = Color.Unspecified,
    var profileTextFieldBackground: Color = Color.Unspecified,
    var profileTextFieldCursorColor: Color = Color.Unspecified,
    var profileTextFieldTextColor: Color = Color.Unspecified,
    var profileTextFieldIconColor: Color = Color.Unspecified,
    var profileTextFieldLoadingAnimationColor: Color = Color.Unspecified,
    var logOutButtonBackground: Color = Color.Unspecified,
    var deleteAccountPreviewButtonBackground: Color = Color.Unspecified,
    var deleteAccountPreviewButtonTextColor: Color = Color.Unspecified,
    var deleteAccountButtonBackground: Color = Color.Unspecified,
    var deleteAccountButtonTextColor: Color = Color.Unspecified,
    var closeSearchIconColor: Color = Color.Unspecified,
    var searchTextFieldBackground: Color = Color.Unspecified,
    var searchTextFieldCursorColor: Color = Color.Unspecified,
    var searchTextFieldTextColor: Color = Color.Unspecified,
    var searchTextFieldIconColor: Color = Color.Unspecified,
    var searchIconColor: Color = Color.Unspecified,
    var searchResultCardBackground: Color = Color.Unspecified,
    var searchResultCardTextColor: Color = Color.Unspecified,
)

var LocalCustomColors = staticCompositionLocalOf { CustomColorScheme() }