package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class CreatePetResponse(
    val message: String,
    val pet: PetDto?,

    // Estes campos só vêm quando cria o pet
    @SerializedName("api_key")
    val apiKey: String?,

    @SerializedName("device_id")
    val deviceId: String?
)