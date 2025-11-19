package br.edu.fatecpg.patatagapp.model

// Classe de dados simples para representar um Pet
data class Pet(
    val id: String,
    val name: String,
    val status: String,
    val imageUrl: String? = null // URL da foto do pet
)
