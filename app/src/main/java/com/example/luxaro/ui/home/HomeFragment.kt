package com.example.luxaro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.luxaro.R
import com.example.luxaro.model.PropertyModelPackage
import com.example.luxaro.propertiesAvailable


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (propertiesAvailable.isNotEmpty()) {
                    DisplayProperties(properties = propertiesAvailable)
                }
                else{
                    DisplayNoProperties()
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
            for (property in properties) {}
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
                {}
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
                {}
            }
        }
    }
}


@Composable
fun DisplayNoProperties(modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 58.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()))
    {
        Text(
            text = stringResource(id = R.string.no_properties_to_display),
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 24.sp,
            color = Color.White,
        )
        Spacer(modifier = modifier.height(15.dp))
        Text(
            text = stringResource(id = R.string.try_checking_again_later),
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}