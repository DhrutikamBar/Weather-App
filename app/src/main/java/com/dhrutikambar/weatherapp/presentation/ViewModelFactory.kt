package com.dhrutikambar.weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<U : Any, VM : ViewModel>(
    private val viewModelClass: Class<VM>,
    private val useCase: U
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            try {
                val constructor = viewModelClass.getConstructor(useCase::class.java)
                return constructor.newInstance(useCase) as T
            } catch (e: NoSuchMethodException) {
                throw IllegalArgumentException(
                    "Unknown ViewModel class $viewModelClass or UseCase $useCase",
                    e
                )
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }

}