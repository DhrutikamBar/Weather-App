package com.dhrutikambar.weatherapp.presentation.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhrutikambar.weatherapp.domain.usecase.WeatherDetailsUseCase
import com.dhrutikambar.weatherapp.presentation.utils.UIState
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val weatherDetailsUseCase: WeatherDetailsUseCase) : ViewModel() {

    suspend fun getWeatherDetails(
        url: String
    ): Flow<UIState> {
        return weatherDetailsUseCase.getWeatherDetails(url)
    }
}