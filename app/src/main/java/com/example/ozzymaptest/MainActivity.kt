package com.example.ozzymaptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ozzymaptest.navigation.AppNavHost
import com.example.ozzymaptest.ui.theme.OzzyMapTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OzzyMapTestTheme {
                AppNavHost()
            }
        }
    }
}
