package com.example.progettodisistemimobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.AuthViewModel
import com.example.progettodisistemimobile.data.MainViewModel
import com.example.progettodisistemimobile.screens.auth.AuthNavHost
import com.example.progettodisistemimobile.ui.theme.ProgettoDiSistemiMobileTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()

            val themeMode by mainViewModel.themeMode.collectAsState(initial = "Sistema")
            val sessione by authViewModel.sessione.collectAsState()

            ProgettoDiSistemiMobileTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        // Sta ancora leggendo il DataStore
                        sessione == null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        // Nessun utente loggato
                        sessione!!.isEmpty() -> AuthNavHost(authViewModel)
                        // Utente loggato
                        else -> MainAppScaffold()
                    }
                }
            }
        }
    }
}
