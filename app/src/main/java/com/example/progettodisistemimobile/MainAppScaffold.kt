package com.example.progettodisistemimobile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.progettodisistemimobile.data.MainViewModel
import com.example.progettodisistemimobile.screens.*
import com.example.progettodisistemimobile.screens.home.HomeScreen
import com.example.progettodisistemimobile.screens.leghe.LegaDetailScreen
import com.example.progettodisistemimobile.screens.leghe.LeMieLegheScreen
import com.example.progettodisistemimobile.screens.leghe.ModificaFormazioneScreen
import com.example.progettodisistemimobile.screens.profilo.ProfiloScreen

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(mainViewModel)
            }
            composable(Screen.LeMieLeghe.route) {
                LeMieLegheScreen(navController, mainViewModel)
            }
            composable(Screen.NuovaSquadra.route) {
                NuovaSquadraScreen(
                    viewModel = mainViewModel,
                    onCreaNuovaLega = { navController.navigate("crea_lega") },
                    onAggiungiALegaEsistente = { /* TODO: schermata Aggiungi a Lega */ }
                )
            }
            composable(Screen.Shop.route) {
                ShopScreen()
            }
            composable(Screen.Profilo.route) {
                ProfiloScreen(mainViewModel)
            }

            composable(
                route = Screen.DettaglioLega.route,
                arguments = listOf(
                    navArgument("idLega") { type = NavType.IntType },
                    navArgument("nomeLega") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val idLega = backStackEntry.arguments?.getInt("idLega") ?: 0
                val nomeLega = backStackEntry.arguments?.getString("nomeLega") ?: ""
                LegaDetailScreen(
                    idLega = idLega,
                    nomeLega = nomeLega,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    viewModel = mainViewModel
                )
            }

            composable("crea_lega") {
                CreaLegaScreen(
                    viewModel = mainViewModel,
                    onBack = { navController.popBackStack() },
                    onLegaCreata = { _ ->
                        navController.navigate(Screen.LeMieLeghe.route) {
                            popUpTo(Screen.NuovaSquadra.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ModificaFormazione.route,
                arguments = listOf(
                    navArgument("idLega") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val idLega = backStackEntry.arguments?.getInt("idLega") ?: 0
                ModificaFormazioneScreen(
                    idLega = idLega,
                    onBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screensInBottomBar.forEach { screen ->
            // Il tab è considerato selezionato se la rotta corrisponde esattamente
            // OPPURE se siamo in una sotto-pagina specifica di quel tab
            val isSelected = when(screen) {
                Screen.LeMieLeghe -> currentRoute == Screen.LeMieLeghe.route ||
                        currentRoute?.startsWith("dettaglio_lega") == true ||
                        currentRoute?.startsWith("modifica_formazione") == true
                Screen.NuovaSquadra -> currentRoute == Screen.NuovaSquadra.route ||
                        currentRoute == "crea_lega"
                else -> currentRoute == screen.route
            }

            NavigationBarItem(
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    // Navighiamo alla root del tab solo se non siamo già sulla root esatta.
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            // Ripristiniamo lo stato solo se stiamo cambiando tab.
                            // Se eravamo già in questo tab (isSelected == true) ma in una sotto-pagina,
                            // evitiamo restoreState per tornare alla root pulita (LeMieLegheScreen).
                            restoreState = !isSelected
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 2,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else null
                    )
                },
                icon = { screen.icon?.let { Icon(it, contentDescription = screen.label) } }
            )
        }
    }
}