package com.example.luxaro

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxaro.model.PropertyModelPackage
import com.example.luxaro.ui.home.DisplaySpecificPropertyContactCard
import com.example.luxaro.ui.home.DisplaySpecificPropertyDetailsCard

class Search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.navigationBarColor = resources.getColor(R.color.ateneo_blue);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                contentColor = Color.White,
                color = colorResource(id = R.color.ateneo_blue),
            ) {
                DisplaySearch()
            }
        }
    }
}

@Composable
fun DisplaySearch(modifier: Modifier = Modifier) {
    val localContext = (LocalContext.current as? Activity)
    val searchText = remember { mutableStateOf("") }
    val searchTextOld = remember { mutableStateOf("") }
    var displayNoPropertiesFound by remember { mutableStateOf(false) }
    var displayMoreInfo by remember { mutableStateOf(false) }
    var displayContactUs by remember { mutableStateOf(false) }
    var specificPropertyToDisplay by remember { mutableStateOf(PropertyModelPackage()) }
    val searchResults = remember { mutableStateOf(listOf<PropertyModelPackage>()) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 12.dp, top = 20.dp, end = 12.dp, bottom = 0.dp),
        ) {
            IconButton(
                modifier = modifier
                    .align(Alignment.End)
                    .padding(start = 0.dp, top = 0.dp, end = 5.dp, bottom = 12.dp),
                onClick = { localContext?.finish() },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                    contentDescription = stringResource(id = R.string.close_search),
                    modifier = modifier
                        .size(38.dp)
                        .padding(0.dp),
                )
            }
        }
    }
}

fun search(searchString: String, propertiesToSearch: List<PropertyModelPackage>, modifier: Modifier = Modifier): SnapshotStateList<PropertyModelPackage> {
    val propertiesFound = mutableStateListOf<PropertyModelPackage>()
    for (propertyOriginal in propertiesToSearch){
        if(propertyOriginal.title.contains(searchString.filterNot { it.isWhitespace() }, ignoreCase = true) and !propertiesFound.contains(propertyOriginal)) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch){
        if(propertyOriginal.shortdescription.contains(searchString.filterNot { it.isWhitespace() }, ignoreCase = true) and !propertiesFound.contains(propertyOriginal)) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch){
        if(propertyOriginal.longdescription.contains(searchString.filterNot { it.isWhitespace() }, ignoreCase = true and !propertiesFound.contains(propertyOriginal))) {
            propertiesFound.add(propertyOriginal)
        }
    }
    for (propertyOriginal in propertiesToSearch){
        if(propertyOriginal.price.contains(searchString.filterNot { it.isWhitespace() }, ignoreCase = true and !propertiesFound.contains(propertyOriginal))) {
            propertiesFound.add(propertyOriginal)
        }
    }
    return propertiesFound.ifEmpty {
        SnapshotStateList<PropertyModelPackage>()
    }
}

@Composable
fun CreateSearchResultCard(property: PropertyModelPackage, onCardClickAction: () -> Unit, modifier: Modifier = Modifier){
    Card(
        onClick = { onCardClickAction() },
        modifier = modifier
            .padding(6.dp, 7.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.medium_persian_blue_2),
            contentColor = Color.White
        )
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.andre_benz_cxu6tnxhub0_unsplash),
                contentDescription = null,
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
    if(displayThisPage) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(top = 50.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.no_properties_found),
                modifier = modifier
                    .padding(20.dp, 0.dp),
                fontSize = 18.sp,
            )
            Spacer(modifier = modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.try_searching_for_something_else),
                modifier = modifier
                    .padding(20.dp, 0.dp),
                fontSize = 18.sp,
            )
        }
    }
}