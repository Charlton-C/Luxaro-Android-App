package com.example.luxaro

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.luxaro.model.PropertyModelPackage
import com.example.luxaro.ui.home.DisplaySpecificPropertyContactCard
import com.example.luxaro.ui.home.DisplaySpecificPropertyDetailsCard
import com.example.luxaro.ui.theme.LocalCustomColors
import com.example.luxaro.ui.theme.LuxaroTheme

class Search : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            LuxaroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = LocalCustomColors.current.windowTextColor,
                    color = LocalCustomColors.current.windowBackground,
                ) {
                    supportActionBar?.hide()
                    window.navigationBarColor = LocalCustomColors.current.windowBackground.toArgb()
                    DisplaySearch()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySearch(modifier: Modifier = Modifier) {
    val localContext = (LocalContext.current as? Activity)
    val searchText = remember { mutableStateOf("") }
    val searchTextOld = remember { mutableStateOf("") }
    var displaySearchingForPropertiesAnimation by remember { mutableStateOf(false) }
    var displayNoPropertiesFound by remember { mutableStateOf(false) }
    var displayMoreInfo by remember { mutableStateOf(false) }
    var displayContactUs by remember { mutableStateOf(false) }
    var specificPropertyToDisplay by remember { mutableStateOf(PropertyModelPackage()) }
    val searchResults = remember { mutableStateOf(listOf<PropertyModelPackage>()) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    // To handel back button clicks
    BackHandler(
        enabled = (displayContactUs or displayMoreInfo)
    ) {
        if (displayContactUs) {
            displayContactUs = false
        } else if (displayMoreInfo) {
            displayMoreInfo = false
        }
    }

    Box {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 12.dp, top = 20.dp, end = 12.dp, bottom = 0.dp),
        ) {
            IconButton(
                onClick = { localContext?.finish() },
                modifier = modifier
                    .align(Alignment.End)
                    .padding(start = 0.dp, top = 0.dp, end = 5.dp, bottom = 12.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                    contentDescription = stringResource(id = R.string.close_search),
                    tint = LocalCustomColors.current.closeSearchIconColor,
                    modifier = modifier
                        .padding(0.dp)
                        .size(38.dp),
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp),
            ) {
                BasicTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    modifier = modifier
                        .fillMaxWidth(0.865f)
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .focusRequester(focusRequester),
                    interactionSource = interactionSource,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = LocalCustomColors.current.searchTextFieldTextColor,
                        fontSize = 18.sp
                    ),
                    cursorBrush = SolidColor(LocalCustomColors.current.searchTextFieldCursorColor),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    visualTransformation = VisualTransformation.None,
                    keyboardActions = KeyboardActions(onSearch = {
                        if (!TextUtils.isEmpty(searchText.value.trim()) and (searchText.value != searchTextOld.value)) {
                            displaySearchingForPropertiesAnimation = true
                            searchResults.value = search(
                                searchText.value.filterNot { it.isWhitespace() },
                                propertiesAvailable
                            )
                            displaySearchingForPropertiesAnimation = false
                            // Check if searchResults is empty and if it is display no search results found message
                            displayNoPropertiesFound = searchResults.value.isEmpty()
                            searchTextOld.value = searchText.value
                            focusManager.clearFocus()
                        } else if (TextUtils.isEmpty(searchText.value)) {
                            focusRequester.requestFocus()
                        }
                    }),
                ) { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = searchText.value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        placeholder = { Text(text = stringResource(id = R.string.search_placeholder)) },
                        trailingIcon = {
                            if (!TextUtils.isEmpty(searchText.value)) {
                                IconButton(onClick = {
                                    searchText.value = ""
                                    focusRequester.requestFocus()
                                }) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                                        contentDescription = stringResource(id = R.string.clear_search),
                                        tint = LocalCustomColors.current.searchTextFieldIconColor,
                                        modifier = modifier
                                            .padding(0.dp)
                                            .size(20.dp),
                                    )
                                }
                            }
                        },
                        contentPadding = PaddingValues(
                            start = 15.dp,
                            top = 8.dp,
                            end = 0.dp,
                            bottom = 8.dp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledTextColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                alpha = 0.5f
                            ),
                            errorTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            focusedContainerColor = LocalCustomColors.current.searchTextFieldBackground,
                            unfocusedContainerColor = LocalCustomColors.current.searchTextFieldBackground,
                            disabledContainerColor = LocalCustomColors.current.searchTextFieldBackground.copy(
                                alpha = 0.5f
                            ),
                            errorContainerColor = LocalCustomColors.current.searchTextFieldBackground,
                            cursorColor = LocalCustomColors.current.searchTextFieldCursorColor,
                            errorCursorColor = LocalCustomColors.current.searchTextFieldCursorColor,
                            focusedBorderColor = LocalCustomColors.current.searchTextFieldBorderColor,
                            unfocusedBorderColor = LocalCustomColors.current.searchTextFieldBorderColor,
                            disabledBorderColor = LocalCustomColors.current.searchTextFieldBorderColor.copy(
                                alpha = 0.5f
                            ),
                            errorBorderColor = LocalCustomColors.current.searchTextFieldBorderColor,
                            focusedLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            unfocusedLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            disabledLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor.copy(
                                alpha = 0.5f
                            ),
                            errorLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            focusedTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            unfocusedTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            disabledTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor.copy(
                                alpha = 0.5f
                            ),
                            errorTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                            focusedLabelColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedLabelColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledLabelColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                alpha = 0.5f
                            ),
                            errorLabelColor = LocalCustomColors.current.searchTextFieldTextColor,
                            focusedPlaceholderColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedPlaceholderColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledPlaceholderColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                alpha = 0.5f
                            ),
                            errorPlaceholderColor = LocalCustomColors.current.searchTextFieldTextColor,
                            focusedSupportingTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedSupportingTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledSupportingTextColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                0.5f
                            ),
                            errorSupportingTextColor = LocalCustomColors.current.searchTextFieldTextColor,
                            focusedPrefixColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedPrefixColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledPrefixColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                0.5f
                            ),
                            errorPrefixColor = LocalCustomColors.current.searchTextFieldTextColor,
                            focusedSuffixColor = LocalCustomColors.current.searchTextFieldTextColor,
                            unfocusedSuffixColor = LocalCustomColors.current.searchTextFieldTextColor,
                            disabledSuffixColor = LocalCustomColors.current.searchTextFieldTextColor.copy(
                                0.5f
                            ),
                            errorSuffixColor = LocalCustomColors.current.searchTextFieldTextColor,
                        ),
                    )
                }
                IconButton(modifier = modifier.align(Alignment.CenterVertically),
                    onClick = {
                        if (!TextUtils.isEmpty(searchText.value.trim()) and (searchText.value != searchTextOld.value)) {
                            displaySearchingForPropertiesAnimation = true
                            searchResults.value = search(
                                searchText.value.filterNot { it.isWhitespace() },
                                propertiesAvailable
                            )
                            displaySearchingForPropertiesAnimation = false
                            // Check if searchResults is empty and if it is display no search results found message
                            displayNoPropertiesFound = searchResults.value.isEmpty()
                            searchTextOld.value = searchText.value
                            focusManager.clearFocus()
                        } else if (TextUtils.isEmpty(searchText.value)) {
                            focusRequester.requestFocus()
                        }
                    }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_search_24),
                        contentDescription = stringResource(id = R.string.search_for) + " " + searchText.value,
                        tint = LocalCustomColors.current.searchIconColor,
                        modifier = modifier.size(32.dp),
                    )
                }
            }
            if (searchResults.value.isNotEmpty() and !displaySearchingForPropertiesAnimation and !displayNoPropertiesFound) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 0.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    for (property in searchResults.value) {
                        CreateSearchResultCard(property = property, onCardClickAction = {
                            specificPropertyToDisplay = property
                            displayMoreInfo = true
                            focusManager.clearFocus()
                        })
                    }
                }
            } else if (displaySearchingForPropertiesAnimation) {
                CircularProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 50.dp),
                    color = LocalCustomColors.current.windowLoadingAnimationColor
                )
            } else {
                DisplayNoPropertiesFound(displayNoPropertiesFound)
            }

            // To focus the BasicTextField when the Search activity starts
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
        AnimatedVisibility(
            visible = displayMoreInfo,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(0.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState()),
            ) {
                Button(
                    onClick = { displayMoreInfo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = modifier
                        .matchParentSize()
                        .padding(0.dp),
                ) {}
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(14.dp, 25.dp),
                )
                {
                    DisplaySpecificPropertyDetailsCard(
                        property = specificPropertyToDisplay,
                        onCardClickAction = { displayMoreInfo = false },
                        onContactUsClickAction = { displayContactUs = true })
                }
            }
        }
        AnimatedVisibility(
            visible = displayContactUs,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState()),
            ) {
                Button(
                    onClick = { displayContactUs = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = modifier
                        .matchParentSize()
                        .padding(0.dp),
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .padding(30.dp, 20.dp),
                    )
                    {
                        DisplaySpecificPropertyContactCard(property = specificPropertyToDisplay)
                    }
                }
            }
        }
    }
}

fun search(
    searchString: String,
    propertiesToSearch: List<PropertyModelPackage>
): SnapshotStateList<PropertyModelPackage> {
    val propertiesFound = mutableStateListOf<PropertyModelPackage>()
    for (propertyOriginal in propertiesToSearch) {
        if (!propertiesFound.contains(propertyOriginal) and propertyOriginal.title.contains(
                searchString.filterNot { it.isWhitespace() },
                ignoreCase = true
            )
        ) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch) {
        if (!propertiesFound.contains(propertyOriginal) and propertyOriginal.shortdescription.contains(
                searchString.filterNot { it.isWhitespace() },
                ignoreCase = true
            )
        ) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch) {
        if (!propertiesFound.contains(propertyOriginal) and propertyOriginal.longdescription.contains(
                searchString.filterNot { it.isWhitespace() },
                ignoreCase = true
            )
        ) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch) {
        if (!propertiesFound.contains(propertyOriginal) and propertyOriginal.price.contains(
                searchString.filterNot { it.isWhitespace() },
                ignoreCase = true
            )
        ) {
            propertiesFound.add(propertyOriginal)
        }
    }
    return propertiesFound.ifEmpty { SnapshotStateList() }
}

@Composable
fun CreateSearchResultCard(
    property: PropertyModelPackage,
    onCardClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onCardClickAction() },
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp, 7.dp),
        border = BorderStroke(1.dp, LocalCustomColors.current.searchResultCardBorderStrokeColor),
        colors = CardDefaults.cardColors(
            containerColor = LocalCustomColors.current.searchResultCardBackground,
            contentColor = LocalCustomColors.current.searchResultCardTextColor
        )
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(property.image)
                    .crossfade(true)
                    .build(),
                contentDescription = property.title + " - " + property.shortdescription,
                modifier = modifier
                    .width(100.dp)
                    .height(70.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = modifier.width(8.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, top = 4.dp),
            ) {
                Text(
                    text = property.title,
                    maxLines = 1,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = property.shortdescription,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}


@Composable
fun DisplayNoPropertiesFound(displayThisPage: Boolean, modifier: Modifier = Modifier) {
    if (displayThisPage) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
        ) {
            Text(
                text = stringResource(id = R.string.no_properties_found),
                modifier = modifier.padding(20.dp, 0.dp),
                fontSize = 18.sp,
                color = LocalCustomColors.current.windowTextColor
            )
            Spacer(modifier = modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.try_searching_for_something_else),
                modifier = modifier.padding(20.dp, 0.dp),
                fontSize = 18.sp,
                color = LocalCustomColors.current.windowTextColor
            )
        }
    }
}