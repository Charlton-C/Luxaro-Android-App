package com.example.luxaro.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.luxaro.R
import com.example.luxaro.areThereAnyLikedPropertiesToShow
import com.example.luxaro.propertiesLikedByUser
import com.example.luxaro.ui.home.DisplayFullPageLoading
import com.example.luxaro.ui.home.DisplayNoProperties
import com.example.luxaro.ui.home.DisplayProperties

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (areThereAnyLikedPropertiesToShow.value == "true") {
                    DisplayProperties(properties = propertiesLikedByUser)
                }
                else if (areThereAnyLikedPropertiesToShow.value == "false"){
                    DisplayNoProperties(R.string.no_properties_to_show, R.string.try_liking_one)
                }
                else if (areThereAnyLikedPropertiesToShow.value == ""){
                    DisplayFullPageLoading()
                }
            }
        }
    }
}