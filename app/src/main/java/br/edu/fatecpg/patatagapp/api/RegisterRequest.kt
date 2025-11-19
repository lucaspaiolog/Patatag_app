package br.edu.fatecpg.patatagapp.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)