package com.example.agendav2.api

import com.example.agendav2.model.Appointment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AppointmentApiService {
    @GET("/citas")
    suspend fun getAppointment(): Response<List<Appointment>>

    @GET("/citas/{id}")
    suspend fun getAppointmentById(@Path("id") id: Int): Response<Appointment>

    @PUT("/citas/{id}")
    suspend fun updateAppointment(@Path("id") id: Int, @Body appointment: Appointment): Response<Appointment>

    @DELETE("/citas/{id}")
    suspend fun deleteAppointment(@Path("id") id: Int): Response<Unit>

    @POST("/citas")
    suspend fun createAppointment(@Body appointment: Appointment): Response<Appointment>

}