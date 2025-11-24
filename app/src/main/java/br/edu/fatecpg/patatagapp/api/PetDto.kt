package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class PetDto(
    val id: Int,
    val name: String,
    val species: String?,
    val breed: String?,
    @SerializedName("photo_url")
    val photoUrl: String?,
    @SerializedName("device_id")
    val deviceId: String?,
    @SerializedName("is_online")
    val isOnline: Boolean,
    @SerializedName("battery_level")
    val batteryLevel: Int,
    @SerializedName("last_seen")
    val lastSeen: String?,
    @SerializedName("last_location")
    val lastLocation: LocationDto?
)