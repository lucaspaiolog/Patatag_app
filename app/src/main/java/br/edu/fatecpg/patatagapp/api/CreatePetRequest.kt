package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class CreatePetRequest(
    val name: String,
    val species: String,
    val breed: String = "",
    @SerializedName("photo_url")
    val photoUrl: String = ""
)