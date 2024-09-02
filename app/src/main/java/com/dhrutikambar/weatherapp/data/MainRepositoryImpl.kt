package com.dhrutikambar.weatherapp.data

import com.dhrutikambar.weatherapp.data.network.NetworkUtils
import com.dhrutikambar.weatherapp.data.network.retrofit.RetrofitHelper
import com.dhrutikambar.weatherapp.domain.repository.MainRepository
import com.dhrutikambar.weatherapp.presentation.utils.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainRepositoryImpl : MainRepository {

    private val networkService = NetworkUtils.getNetworkService()

    private val weatherDetailsResponse = MutableStateFlow<UIState>(UIState.Loading)
    private val weatherDetailsFlow: Flow<UIState> = weatherDetailsResponse.asStateFlow()


    override suspend fun getWeatherDetails(url: String): Flow<UIState> {
        try {
            weatherDetailsResponse.value = UIState.Loading
            val res = networkService.getWeatherDetails(
                url
            )
            if (res.isSuccessful) {
                val body = res.body()
                weatherDetailsResponse.value = UIState.Success(body)
            } else {
                val errorResponse = RetrofitHelper.parseFailResponse(
                    res.errorBody()?.charStream()?.readText()
                )
                weatherDetailsResponse.value = UIState.Error(
                    statusCode = errorResponse.statusCode.toString(),
                    message = errorResponse.message.toString(),
                    errorDescription = ""
                )
            }
        } catch (ex: Exception) {
            weatherDetailsResponse.value =
                UIState.Error(
                    "",
                    message = ex.localizedMessage.toString(),
                    errorDescription = ""
                )

        }
        return weatherDetailsFlow
    }


}