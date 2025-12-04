package com.example.smarparkinapp.ui.theme.data.api

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.adapter.ReservationResponseAdapter
import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configuración GSON con adaptadores personalizados
    private fun createGsonConverter(): GsonConverterFactory {
        val gsonBuilder = GsonBuilder()
            .registerTypeAdapter(ReservationResponse::class.java, ReservationResponseAdapter())
        // Puedes registrar más adaptadores aquí si los necesitas

        return GsonConverterFactory.create(gsonBuilder.create())
    }

    // Cliente sin autenticación (para login/register) - CON ADAPTADOR
    private val basicClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Cliente con autenticación - CON ADAPTADOR
    private fun getAuthenticatedClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()

                token?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Servicio básico (sin auth) - CON ADAPTADOR
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(basicClient)
            .addConverterFactory(createGsonConverter()) // <-- USAMOS CONVERSOR CON ADAPTADOR
            .build()
            .create(ApiService::class.java)
    }

    // Servicio autenticado (con token) - CON ADAPTADOR
    fun getAuthenticatedApiService(context: Context): ApiService {
        val authManager = AuthManager(context)
        val token = authManager.getAuthToken()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getAuthenticatedClient(token))
            .addConverterFactory(createGsonConverter()) // <-- USAMOS CONVERSOR CON ADAPTADOR
            .build()
            .create(ApiService::class.java)
    }
}