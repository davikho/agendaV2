package com.example.agendav2.api

import com.example.agendav2.model.LoginRequest
import com.example.agendav2.model.LoginResponse
import com.example.agendav2.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("usuarios")
    suspend fun createUser(@Body user: User): Response<User>

    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuarios")
    suspend fun getUsers(): Response<List<User>>

    @GET("/usuarios/{id}")
    suspend fun getUsuariosById(@Path("id") userId: Int): Response<User>

    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Void>

    @PUT("usuarios/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body user: User): Response<User>
}