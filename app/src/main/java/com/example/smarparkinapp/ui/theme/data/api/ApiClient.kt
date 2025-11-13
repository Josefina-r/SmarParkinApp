package com.example.smarparkinapp.ui.theme.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var token: String? = null
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Interceptor que agrega el token si existe
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        token?.let {
            builder.header("Authorization", it)
        }

        chain.proceed(builder.build())
    }

    // Creamos un cliente base (se reconstruirá si cambia el token)
    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    // Retrofit dinámico (se puede reiniciar)
    private var _retrofit: Retrofit? = null

    val retrofit: Retrofit
        get() {
            if (_retrofit == null) {
                _retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createClient())
                    .build()
            }
            return _retrofit!!
        }

    // ✅ Guarda el token y reconstruye Retrofit con el nuevo cliente
    fun setToken(authToken: String) {
        token = "Bearer $authToken"
        _retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient())
            .build()
    }

    fun clearToken() {
        token = null
        _retrofit = null
    }
}
