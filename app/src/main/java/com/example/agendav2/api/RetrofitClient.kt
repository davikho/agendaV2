package com.example.agendav2.api

import com.example.agendav2.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // Función para crear instancias de cualquier ApiService
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    // Instancia de ApiService
    val instance: ApiService by lazy {
        createService(ApiService::class.java)
    }

    // Instancia de DoctorApiService
    val doctorInstance: DoctorApiService by lazy {
        createService(DoctorApiService::class.java)
    }

    // Instancia de AppointmentApiService
    val AppointmentInstance: AppointmentApiService by lazy {
        createService(AppointmentApiService::class.java)
    }
}