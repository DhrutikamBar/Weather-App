package com.dhrutikambar.weatherapp.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dhrutikambar.weatherapp.R
import com.dhrutikambar.weatherapp.presentation.ui.ui.theme.WeatherAppTheme


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SplashScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(modifier: Modifier) {
    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val isPermissionGranted = remember {
        mutableStateOf(isAllPermissionGranted(context = context, permissions = permissions))
    }

    val openDialog = remember {
        mutableStateOf(false)
    }


    LaunchedEffect(key1 = isPermissionGranted.value) {
        if (isPermissionGranted.value) {
            goToMainActivity(context)
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            permissionResult.forEach { permission, isGranted ->
                isPermissionGranted.value = isGranted
            }

        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .absolutePadding(top = 30.dp, bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (isPermissionGranted.value){
           SplashUI()
        }else{
            PermissionUI(permissionLauncher, permissions)
        }

    }
}

@Composable
fun SplashUI() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(top = 250.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "", modifier = Modifier.size(100.dp)
        )
        Text(text = "Weather App", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(20.dp))


    }
}

@Composable
fun PermissionUI(
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    permissions: Array<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(top = 250.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "", modifier = Modifier.size(100.dp)
        )
        Text(text = "Weather App", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "We need your location to provide personalized services and improve your experience. Please grant access to continue",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(left = 15.dp, right = 15.dp),
            textAlign = TextAlign.Center
        )
    }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(left = 15.dp, right = 15.dp),
            onClick = {
                permissionLauncher.launch(permissions)
            }) {
            Text(text = "ALLOW")
        }
    }
}


fun goToMainActivity(context: Context) {
    val activity = context as Activity
    activity.startActivity(Intent(activity, MainActivity::class.java))
    activity.finish()
}

fun isAllPermissionGranted(context: Context?, permissions: Array<String>): Boolean {
    var isAllGranted = true
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context!!, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isAllGranted = false
        }
    }
    return isAllGranted
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    WeatherAppTheme {
        SplashScreen(modifier = Modifier)
    }
}