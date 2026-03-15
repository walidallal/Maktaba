package com.ElOuedUniv.maktaba

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ElOuedUniv.maktaba.ui.navigation.MaktabaNavGraph
import com.ElOuedUniv.maktaba.ui.theme.MaktabaTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MaktabaApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaktabaTheme {
                MaktabaNavGraph()
            }
        }
    }
}
