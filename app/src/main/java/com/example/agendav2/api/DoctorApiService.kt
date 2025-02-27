package com.example.agendav2.api

import com.example.agendav2.model.Doctor
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DoctorApiService {
    @GET("/medicos")
    suspend fun getDoctors(): Response<List<Doctor>>

    @GET("/medicos/{id}")
    suspend fun getDoctorById(@Path("id") id: Int): Response<Doctor>

    @PUT("/medicos/{id}")
    suspend fun updateDoctor(@Path("id") id: Int, @Body doctor: Doctor): Response<Doctor>

    @DELETE("/medicos/{id}")
    suspend fun deleteDoctor(@Path("id") id: Int): Response<Unit>

    @POST("/medicos")
    suspend fun createDoctor(@Body doctor: Doctor): Response<Doctor>

}