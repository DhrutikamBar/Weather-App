package com.dhrutikambar.weatherapp.data.network

import com.dhrutikambar.weatherapp.data.MainRepositoryImpl
import com.dhrutikambar.weatherapp.data.network.retrofit.RetrofitHelper
import com.dhrutikambar.weatherapp.domain.repository.MainRepository

object NetworkUtils {

    fun getNetworkService(): NetworkService {
        return RetrofitHelper.getRetrofitClient()
    }


    fun getMainRepository(): MainRepository {
        return MainRepositoryImpl()
    }
}