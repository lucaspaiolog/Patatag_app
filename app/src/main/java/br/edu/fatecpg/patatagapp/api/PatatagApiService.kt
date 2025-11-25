package br.edu.fatecpg.patatagapp.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PatatagApiService {
    // Auth
    @POST("/api/login") fun login(@Body request: LoginRequest): Call<AuthResponse>
    @POST("/api/register") fun register(@Body request: RegisterRequest): Call<AuthResponse>
    @POST("/api/logout") fun logout(): Call<AuthResponse>
    @PUT("/api/user") fun updateUser(@Body request: UpdateUserRequest): Call<AuthResponse>

    // Upload
    @Multipart @POST("/api/upload") fun uploadImage(@Part file: MultipartBody.Part): Call<UploadResponse>

    // Pets
    @GET("/api/pets") fun getPets(): Call<PetsResponse>
    @GET("/api/pets/{id}") fun getPetDetails(@Path("id") id: Int): Call<PetDto>
    @POST("/api/pets") fun createPet(@Body request: CreatePetRequest): Call<CreatePetResponse>
    @GET("/api/pets/{id}/location") fun getPetLocation(@Path("id") id: Int): Call<LocationDto>

    // Geofence (NOVO)
    @GET("/api/pets/{id}/geofence") fun getGeofences(@Path("id") id: Int): Call<GeofenceResponse>
    @POST("/api/pets/{id}/geofence") fun createGeofence(@Path("id") id: Int, @Body request: CreateGeofenceRequest): Call<CreateGeofenceResponse>
    @DELETE("/api/geofence/{id}") fun deleteGeofence(@Path("id") id: Int): Call<DeleteGeofenceResponse>

    // Alertas
    @GET("/api/alerts") fun getAlerts(): Call<AlertsResponse>

    // Histórico

    @GET("/api/pets/{id}/history")
    fun getPetHistory(@Path("id") id: Int): Call<HistoryResponse>
}