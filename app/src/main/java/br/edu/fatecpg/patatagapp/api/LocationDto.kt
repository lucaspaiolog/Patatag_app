package br.edu.fatecpg.patatagapp.api

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speed: Double?,
    val timestamp: String?
)