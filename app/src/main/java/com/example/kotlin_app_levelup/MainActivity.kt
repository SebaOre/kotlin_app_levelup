package com.example.kotlin_app_levelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kotlin_app_levelup.ui.screens.home.HomeScreen
import com.example.kotlin_app_levelup.ui.theme.Kotlin_app_levelupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin_app_levelupTheme {
                HomeScreen()
            }
        }
    }
}