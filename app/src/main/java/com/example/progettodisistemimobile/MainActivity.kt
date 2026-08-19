package com.example.progettodisistemimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.MainViewModel
import com.example.progettodisistemimobile.ui.theme.ProgettoDiSistemiMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")
            
            ProgettoDiSistemiMobileTheme(themeMode = themeMode) {
                MainAppScaffold()
            }
        }
    }
}
