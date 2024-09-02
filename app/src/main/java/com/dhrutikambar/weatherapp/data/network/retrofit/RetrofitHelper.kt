package com.dhrutikambar.weatherapp.data.network.retrofit

import com.dhrutikambar.weatherapp.data.network.NetworkService
import com.dhrutikambar.weatherapp.domain.model.ErrorResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    private const val NETWORK_REQUEST_TIMEOUT_SECONDS = 45L

    fun getRetrofitClient(): NetworkService =
        Retrofit.Builder().client(getOkHttpClient()).baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(NetworkService::class.java)

    private fun getOkHttpClient() =
        OkHttpClient.Builder()
            .connectTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    fun parseFailResponse(readText: String?): ErrorResponse {
        if (!readText.isNullOrEmpty()) {
            return Gson().fromJson(readText, ErrorResponse::class.java)
        }
        return ErrorResponse()
    }
}