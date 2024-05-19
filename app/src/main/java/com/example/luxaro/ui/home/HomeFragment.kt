package com.example.luxaro.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.luxaro.R
import com.example.luxaro.areThereAnyPropertiesToShow
import com.example.luxaro.likeProperty
import com.example.luxaro.model.PropertyModelPackage
import com.example.luxaro.propertiesAvailable
import com.example.luxaro.unlikeProperty
import java.util.Locale


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                when (areThereAnyPropertiesToShow.value){
                    "true" -> {
                        DisplayProperties(properties = propertiesAvailable)
                    }
                    "false" -> {
                        DisplayNoProperties(R.string.no_properties_to_show, R.string.try_checking_again_later)
                    }
                    "" -> {
                        DisplayFullPageLoading()
                    }
                }
            }
        }
    }
}


@Composable
fun DisplayProperties(properties: List<PropertyModelPackage>, modifier: Modifier = Modifier) {
    var displayMoreInfo by remember { mutableStateOf(false) }
    var displayContactUs by remember { mutableStateOf(false) }
    var specificPropertyToDisplay by remember { mutableStateOf(PropertyModelPackage()) }

    // To handel back button clicks
    BackHandler (
        enabled = (displayContactUs or displayMoreInfo)
    ) {
        if (displayContactUs){
            displayContactUs = false
        }
        else if (displayMoreInfo){
            displayMoreInfo = false
        }
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 58.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        )
        {
            for (property in properties) {
                CreatePropertyCard(property = property, onCardClickAction = {
                    specificPropertyToDisplay = property
                    displayMoreInfo = true
                })
            }
        }
        AnimatedVisibility(
            visible = displayMoreInfo,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Box(
                modifier = modifier
                    .padding(0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState()),
            ){
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = modifier
                        .padding(0.dp)
                        .matchParentSize(),
                    onClick = {
                        displayMoreInfo = false
                    },
                ) {}
                Column(modifier = modifier
                    .padding(14.dp, 20.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center),
                )
                {
                    DisplaySpecificPropertyDetailsCard(
                        property = specificPropertyToDisplay,
                        onCardClickAction = {
                            displayMoreInfo = false
                        },
                        onContactUsClickAction = {
                            displayContactUs = true
                        })
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = modifier
                        .padding(0.dp)
                        .matchParentSize(),
                    onClick = {
                        displayContactUs = false
                    }
                ) {}
                Column(modifier = modifier
                    .padding(30.dp, 20.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center),
                )
                {
                    DisplaySpecificPropertyContactCard(property = specificPropertyToDisplay)
                }
            }
        }
    }
}

@Composable
fun CreatePropertyCard(property: PropertyModelPackage, onCardClickAction: () -> Unit, modifier: Modifier = Modifier) {
    val likedText = remember { mutableStateOf(R.string.like) }
    val favoriteIcon = remember { mutableStateOf(R.drawable.baseline_favorite_border_24) }
    if(property.liked.value){
        likedText.value = R.string.unlike
        favoriteIcon.value = R.drawable.baseline_favorite_24
    }
    else{
        likedText.value = R.string.like
        favoriteIcon.value = R.drawable.baseline_favorite_border_24
    }
    Card(modifier = modifier
        .padding(20.dp, 12.dp)
        .fillMaxWidth()
        .clickable { onCardClickAction() },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.medium_persian_blue_1),
            contentColor = Color.White
        )) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(property.image)
                .crossfade(true)
                .build(),
            contentDescription = property.title + " - " + property.shortdescription,
            modifier = modifier
                .fillMaxWidth()
                .height(195.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = modifier.height(9.dp))
        Row(
            modifier = modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Column(modifier = modifier
                .fillMaxWidth(.85f)
                .fillMaxHeight()
            ) {
                Text(
                    text = property.title,
                    fontSize = 24.sp,
                )
                Spacer(modifier = modifier.height(5.dp))
                Text(
                    text = property.shortdescription,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = modifier.width(7.dp))
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(0.dp)
                    .align(Alignment.CenterVertically),
            ) {
                IconButton(
                    onClick = {
                        property.liked.value = !property.liked.value
                        if(property.liked.value){
                            likeProperty(property = property)
                        }
                        else{
                            unlikeProperty(property = property)
                        }
                    },
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(0.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = favoriteIcon.value),
                        contentDescription = stringResource(likedText.value),
                        modifier = modifier
                            .size(35.dp)
                            .fillMaxHeight()
                            .padding(0.dp),
                    )
                }
            }
        }
        Spacer(modifier = modifier.height(20.dp))
    }
}

@Composable
fun DisplaySpecificPropertyDetailsCard(property: PropertyModelPackage, onCardClickAction: () -> Unit, onContactUsClickAction: () -> Unit, modifier: Modifier = Modifier){
    val likedText = remember { mutableStateOf(R.string.like) }
    val favoriteIcon = remember { mutableStateOf(R.drawable.baseline_favorite_border_24) }
    if(property.liked.value){
        likedText.value = R.string.unlike
        favoriteIcon.value = R.drawable.baseline_favorite_24
    }
    else{
        likedText.value = R.string.like
        favoriteIcon.value = R.drawable.baseline_favorite_border_24
    }
    Card(
        modifier = modifier
            .padding(bottom = 62.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.medium_persian_blue_1),
            contentColor = Color.White
        )
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(property.image)
                    .crossfade(true)
                    .build(),
                contentDescription = property.title + " - " + property.shortdescription,
                modifier = modifier
                    .fillMaxWidth()
                    .height(195.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                modifier = modifier
                    .align(Alignment.TopEnd),
                onClick = { onCardClickAction() },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = modifier
                        .size(36.dp),
                )
            }
        }
        Spacer(modifier = modifier.height(9.dp))
        Row(
            modifier = modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = property.title,
                modifier = modifier
                    .fillMaxWidth(.83f),
                fontSize = 24.sp,
            )
            IconButton(
                modifier = modifier
                    .padding(start = 3.dp)
                    .align(Alignment.CenterVertically),
                onClick = {
                    property.liked.value = !property.liked.value
                    if(property.liked.value){
                        likeProperty(property = property)
                    }
                    else{
                        unlikeProperty(property = property)
                    }
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = favoriteIcon.value),
                    contentDescription = stringResource(likedText.value),
                    modifier = modifier
                        .size(45.dp)
                        .padding(0.dp),
                )
            }
        }
        Spacer(modifier = modifier.height(6.dp))
        Text(
            text = property.shortdescription,
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 20.sp,
        )
        Spacer(modifier = modifier.height(10.dp))
        Text(
            text = property.longdescription,
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 20.sp,
        )
        Spacer(modifier = modifier.height(15.dp))
        Text(
            text = property.currencysymbol+String.format(Locale.getDefault(), "%,.2f", property.price.toFloat()),
            modifier = modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(20.dp, 0.dp),
            fontSize = 22.sp,
        )
        Spacer(modifier = modifier.height(20.dp))
        Button(
            onClick = { onContactUsClickAction() },
            modifier = modifier
                .align(alignment = Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.persian_green))
        ) {
            Text(
                text = stringResource(id = R.string.contact_us),
                modifier = modifier
                    .background(Color.Transparent),
                fontSize = 21.sp,
            )
        }
        Spacer(modifier = modifier.height(32.dp))
    }
}

@Composable
fun DisplaySpecificPropertyContactCard(property: PropertyModelPackage, modifier: Modifier = Modifier){
    val localContext = LocalContext.current
    val inquiryOnText = stringResource(id = R.string.inquiry_on)
    Card(modifier = modifier
            .padding(bottom = 62.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.ocean_boat_blue_2),
            contentColor = Color.White
        )) {
        Spacer(modifier = modifier.height(45.dp))
        Column(modifier = modifier.align(Alignment.CenterHorizontally)) {
            Button(
                onClick = {
                    localContext.startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + property.phonenumber)
                        )
                    )
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.vivid_cerulean)),
                modifier = modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(25.dp, 0.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.call_us),
                    modifier = modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = modifier.height(35.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, arrayOf(property.emailaddress))
                        .putExtra(Intent.EXTRA_SUBJECT, inquiryOnText + " " + property.title)
                        .putExtra(Intent.EXTRA_TEXT, inquiryOnText + " " + property.title)
                    localContext.startActivity(Intent.createChooser(intent, localContext.getString(R.string.choose_an_email_client)))
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.vivid_cerulean)),
                modifier = modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(25.dp, 0.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.email_us),
                    modifier = modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = modifier.height(35.dp))
            Button(
                onClick = {
                    localContext.startActivity(
                        Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("smsto:" + property.phonenumber)
                        ).putExtra("sms_body", inquiryOnText + " " + property.title)
                    )
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.vivid_cerulean)),
                modifier = modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(25.dp, 0.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.text_us),
                    modifier = modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = modifier.height(35.dp))
            Button(
                onClick = {
                    localContext.startActivity(Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                property.phonenumber, inquiryOnText + " " + property.title)
                        )))
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.lime_green)),
                modifier = modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(25.dp, 0.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.whatsapp),
                    modifier = modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(modifier = modifier.height(45.dp))
    }
}


@Composable
fun DisplayNoProperties(messegeOneResource: Int, messegeTwoResource: Int, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 58.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()))
    {
        Text(
            text = stringResource(id = messegeOneResource),
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 24.sp,
            color = Color.White,
        )
        Spacer(modifier = modifier.height(15.dp))
        Text(
            text = stringResource(id = messegeTwoResource),
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}

@Composable
fun DisplayFullPageLoading(modifier: Modifier = Modifier){
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 58.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()))
    {
        CircularProgressIndicator(color = colorResource(id = R.color.rich_electric_blue))
    }
}