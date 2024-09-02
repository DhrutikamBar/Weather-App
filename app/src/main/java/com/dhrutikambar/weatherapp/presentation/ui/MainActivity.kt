package com.dhrutikambar.weatherapp.presentation.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dhrutikambar.weatherapp.R
import com.dhrutikambar.weatherapp.data.network.NetworkUtils
import com.dhrutikambar.weatherapp.domain.model.WeatherData
import com.dhrutikambar.weatherapp.domain.model.WeatherDetailsResponse
import com.dhrutikambar.weatherapp.domain.usecase.WeatherDetailsUseCase
import com.dhrutikambar.weatherapp.presentation.ViewModelFactory
import com.dhrutikambar.weatherapp.presentation.utils.UIState
import com.dhrutikambar.weatherapp.presentation.viewModel.MainViewModel
import com.dhrutikambar.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create ViewModelFactory with the use case
        val factory = ViewModelFactory(
            viewModelClass = MainViewModel::class.java,
            useCase = WeatherDetailsUseCase(NetworkUtils.getMainRepository())
        )
        // Get ViewModel instance
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding), viewModel
                    )
                }
            }
        }
    }
}

private fun getLocation(
    context: Context, fusedLocationClient: FusedLocationProviderClient,
    onLocationResult: (Double, Double) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        backToSplashActivity(context)
    } else {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                Log.d("LOCATION_FETCH","CALLED")
                onLocationResult(it.latitude, it.longitude)
            }
        }
    }

}

fun backToSplashActivity(context: Context) {

    val activity = context as Activity
    activity.startActivity(Intent(activity, SplashActivity::class.java))
    activity.finish()

}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val latitude = remember { mutableStateOf<Double?>(null) }
    val longitude = remember { mutableStateOf<Double?>(null) }
    val callAPIFlag = remember {
        mutableStateOf(false)
    }
    val weatherData = remember {
        mutableStateOf(WeatherData())
    }
    val isLoading = remember {
        mutableStateOf(false)
    }

    val isAllPermissionGranted = remember {
        mutableStateOf(
            isAllPermissionGranted(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        )
    }


    if (isAllPermissionGranted.value
    ) {
        getLocation(context, fusedLocationClient) { lat, long ->
            latitude.value = lat
            longitude.value = long
        }
    }

    LaunchedEffect(key1 = latitude.value) {
        latitude.value?.let {
            callAPIFlag.value = true
        }
    }

    LaunchedEffect(key1 = callAPIFlag.value) {
        if (callAPIFlag.value) {
            isLoading.value = true
            getLocation(context, fusedLocationClient) { lat, long ->
                latitude.value = lat
                longitude.value = long
            }
            val url =
                "https://api.openweathermap.org/data/2.5/weather?lat=${latitude.value}&lon=${longitude.value}&units=metric&appid=bfbc6456c4cad0e639f1cdf7a50fa7e7"
            viewModel.getWeatherDetails(url).collect {
                when (it) {
                    is UIState.Loading -> {
                        Log.d("UI_STATE", "LOADING")

                    }

                    is UIState.Success<*> -> {
                        val response = it.data as WeatherDetailsResponse
                        Log.d("UI_STATE", "SUCCESS__response")
                        callAPIFlag.value = false
                        isLoading.value = false
                        val city = response.name
                        val country = response.sys.country
                        val temp = response.main.temp.roundToInt()
                        val humidity = response.main.humidity
                        val weather = response.weather[0].main
                        val windSpeed = response.wind.speed
                        val data = WeatherData(
                            country = country,
                            city = city,
                            temp = temp.toString(),
                            weather = weather,
                            windSpeed = windSpeed.toString(),
                            humidity = humidity.toString()
                        )
                        weatherData.value = data
                    }

                    is UIState.Error -> {
                        isLoading.value = false
                        val data = it
                        Log.d("UI_STATE", "ERROR__${data.message}")
                        Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()

                    }

                }
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading.value) {
            LoadingUI()
        } else {
            MainUI(weatherData, latitude, longitude, callAPIFlag)
        }

    }
}

@Composable
fun MainUI(
    weatherData: MutableState<WeatherData>,
    latitude: MutableState<Double?>,
    longitude: MutableState<Double?>,
    callAPIFlag: MutableState<Boolean>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Column(
            modifier = Modifier
                .size(200.dp)
                .background(color = Color.Cyan, shape = RoundedCornerShape(50)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${weatherData.value.temp}°",
                color = Color.Black,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(0.dp))
            Text(
                text = "${weatherData.value.weather}",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Humidity: ${
                weatherData.value.humidity
            }",
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Wind Speed: ${
                weatherData.value.windSpeed
            }",
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "${latitude.value.toString().formatNullString()},${
                longitude.value.toString().formatNullString()
            }",
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${weatherData.value.city},${weatherData.value.country}",
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_refresh),
            contentDescription = "",
            modifier = Modifier
                .size(35.dp)
                .clickable {
                    callAPIFlag.value = true
                }, colorFilter = ColorFilter.tint(color = Color.Black)
        )
    }

}


@Composable
fun LoadingUI() {
    CircularProgressIndicator(strokeWidth = 3.dp, color = Color.Black)
}

fun String.formatNullString(): String {
    return if (this.equals("null", true)) {
        "-"
    } else {
        this
    }

}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        MainScreen(modifier = Modifier, viewModel = viewModel)
    }
}*/
