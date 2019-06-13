package com.cry.buttonbase.models

data class IpData (

    val ip : String,
    val ip_decimal : Long,
    val country : String,
    val country_eu : Boolean,
    val country_iso : String,
    val city : String,
    val hostname : String,
    val latitude : Double,
    val longitude : Double
)