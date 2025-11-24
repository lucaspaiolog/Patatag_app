package br.edu.fatecpg.patatagapp.api

import retrofit2.Call
import retrofit2.http.GET

interface AlertsApi {
    @GET("/api/alerts")
    fun getAlerts(): Call<AlertsResponse>
}