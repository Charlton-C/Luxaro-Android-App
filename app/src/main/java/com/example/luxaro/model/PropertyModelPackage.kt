package com.example.luxaro.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


data class PropertyModelPackage(
    var id: String = "",
    var image: String = "",
    var title: String = "",
    var shortdescription: String = "",
    var longdescription: String = "",
    var currencysymbol: String = "",
    var currencyname: String = "",
    var price: String = "",
    var phonenumber: String = "",
    var emailaddress: String = "",
    var textnumber: String = "",
    var whatsappnumber: String = "",
    var liked: MutableState<Boolean> = mutableStateOf(false),
)