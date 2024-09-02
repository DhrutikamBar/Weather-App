package com.dhrutikambar.weatherapp.domain.repository

import com.dhrutikambar.weatherapp.presentation.utils.UIState
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getWeatherDetails(url:String): Flow<UIState>
}