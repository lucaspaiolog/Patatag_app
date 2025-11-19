package br.edu.fatecpg.patatagapp.api

data class LoginRequest(
    val email: String,
    val password: String
)