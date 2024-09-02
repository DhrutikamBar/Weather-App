package com.dhrutikambar.weatherapp.data.network

import com.dhrutikambar.weatherapp.domain.model.WeatherDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NetworkService {

    @GET
    suspend fun getWeatherDetails(
        @Url url: String
    ): Response<WeatherDetailsResponse>

}