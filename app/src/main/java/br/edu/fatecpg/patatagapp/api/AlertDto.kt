package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class AlertDto(
    val id: Int,
    val message: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("alert_type") val type: String
)