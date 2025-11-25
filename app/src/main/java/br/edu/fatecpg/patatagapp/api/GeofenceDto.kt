package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class GeofenceDto(
    val id: Int,
    val name: String, @SerializedName("center_lat")
    val centerLat: Double, @SerializedName("center_lng")
    val centerLng: Double, @SerializedName("radius_meters")
    val radius: Double
)
