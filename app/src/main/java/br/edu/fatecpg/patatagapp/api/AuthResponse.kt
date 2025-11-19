package br.edu.fatecpg.patatagapp.api

data class AuthResponse(
    val message: String,
    val user: UserDto?,
    val error: String?
)
