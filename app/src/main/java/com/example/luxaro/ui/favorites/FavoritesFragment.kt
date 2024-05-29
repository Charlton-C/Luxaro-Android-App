package com.example.luxaro.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.luxaro.areThereAnyLikedPropertiesToShow
import com.example.luxaro.propertiesLikedByUser
import com.example.luxaro.ui.home.DisplayFullPageLoading
import com.example.luxaro.ui.home.DisplayProperties
import com.example.luxaro.ui.theme.LuxaroTheme

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LuxaroTheme {
                    if (areThereAnyLikedPropertiesToShow.value == "true") {
                        DisplayProperties(properties = propertiesLikedByUser)
                    }
                    else if (areThereAnyLikedPropertiesToShow.value == "false"){
                        DisplayNoLikedProperties()
                    }
                    else if (areThereAnyLikedPropertiesToShow.value == ""){
                        DisplayFullPageLoading()
                    }
                }
            }
        }
    }
}


@Composable
fun DisplayNoLikedProperties(modifier: Modifier = Modifier) {
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
            text = stringResource(id = R.string.try_liking_one),
            modifier = modifier
                .padding(20.dp, 0.dp),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}