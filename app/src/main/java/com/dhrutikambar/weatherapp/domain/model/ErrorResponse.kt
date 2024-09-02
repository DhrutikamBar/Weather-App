package com.dhrutikambar.weatherapp.domain.model


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("uri") var uri: String? = null,
    @SerializedName("data") val data: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("errorCode") var errorCode: String? = null,
    @SerializedName("statusCode") var statusCode: String? = null,
    @SerializedName("errorResponse") var errorResponse: String? = null,
    @SerializedName("errorDescription") var errorDescription: String? = null,
    @SerializedName("transactionId") var transactionId: String? = null,
    @SerializedName("informationLink") var informationLink: String? = null,


    )

