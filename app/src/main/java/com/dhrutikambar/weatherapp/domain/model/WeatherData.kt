package com.dhrutikambar.weatherapp.domain.model

data class WeatherData(
    val country: String?="",
    val city: String?="",
    val temp: String?="",
    val weather: String?="",
    val windSpeed: String?="",
    val humidity: String?="",
) {
}