package br.edu.fatecpg.patatagapp.api

data class HistoryResponse(
    val locations: List<LocationDto>,
    val total: Int,
    val pages: Int
)