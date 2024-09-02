package com.dhrutikambar.weatherapp.presentation.utils

sealed class UIState {
    object Loading : UIState()
    data class Success<T>(val data: T) : UIState()
    data class Error(val statusCode: String, val message: String, val errorDescription: String) :
        UIState()
}



