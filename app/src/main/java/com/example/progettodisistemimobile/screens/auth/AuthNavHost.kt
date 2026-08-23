package com.example.progettodisistemimobile.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.progettodisistemimobile.data.AuthViewModel

private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTRAZIONE = "registrazione"

/**
 * Grafo di navigazione mostrato quando nessun utente è loggato.
 * L'AuthViewModel viene passato dall'esterno così le due schermate
 * condividono lo stesso stato (errori, loading).
 */
@Composable
fun AuthNavHost(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onNavigateToRegistrazione = { navController.navigate(ROUTE_REGISTRAZIONE) },
                viewModel = viewModel
            )
        }
        composable(ROUTE_REGISTRAZIONE) {
            RegistrazioneScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}