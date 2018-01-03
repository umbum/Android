package com.example.umbum.openweatherexp.data

import com.google.gson.annotations.SerializedName

data class CityArray(val city: ArrayList<CityData>)
data class CityData(@SerializedName("id") val api_id: String, val name: String)
