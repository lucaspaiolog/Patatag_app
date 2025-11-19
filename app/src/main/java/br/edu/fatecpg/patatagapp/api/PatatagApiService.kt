package br.edu.fatecpg.patatagapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PatatagApiService {

    // Autenticação
    @POST("/api/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("/api/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("/api/pets")
    fun createPet(@Body request: CreatePetRequest): Call<CreatePetResponse>

    @POST("/api/logout")
    fun logout(): Call<AuthResponse>

    @GET("/api/pets")
    fun getPets(): Call<PetsResponse>

    @GET("/api/pets/{id}")
    fun getPetDetails(@Path("id") id: Int): Call<PetDto>

    // Localização
    @GET("/api/pets/{id}/location")
    fun getPetLocation(@Path("id") id: Int): Call<LocationDto>

}