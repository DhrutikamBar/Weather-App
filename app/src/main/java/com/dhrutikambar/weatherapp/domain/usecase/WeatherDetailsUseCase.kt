package com.dhrutikambar.weatherapp.domain.usecase

import com.dhrutikambar.weatherapp.domain.repository.MainRepository
import com.dhrutikambar.weatherapp.presentation.utils.UIState
import kotlinx.coroutines.flow.Flow

class WeatherDetailsUseCase(private val mainRepository: MainRepository) {
    suspend fun getWeatherDetails(url: String): Flow<UIState> {
        return mainRepository.getWeatherDetails(url)
    }
}