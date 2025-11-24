package br.edu.fatecpg.patatagapp.api

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    val name: String? = null,
    @SerializedName("profile_image")
    val profileImage: String? = null
)
