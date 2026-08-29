package com.example.progettodisistemimobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.progettodisistemimobile.data.MainViewModel
import com.example.progettodisistemimobile.screens.*
import com.example.progettodisistemimobile.screens.home.HomeScreen
import com.example.progettodisistemimobile.screens.leghe.LeMieLegheScreen
import com.example.progettodisistemimobile.screens.leghe.detail.LegaDetailScreen
import com.example.progettodisistemimobile.screens.leghe.formazione.ModificaFormazioneScreen
import com.example.progettodisistemimobile.screens.profilo.ProfiloScreen
import com.example.progettodisistemimobile.screens.shop.ShopScreen
import androidx.navigation.navDeepLink

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
                    onAggiungiALegaEsistente = { navController.navigate("aggiungi_lega") },
                    onIscrizioneDaInvito = {
                        navController.navigate(Screen.LeMieLeghe.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Shop.route) {
                ShopScreen(mainViewModel)
            }
            composable(Screen.Profilo.route) {
                ProfiloScreen(mainViewModel)
            }

            // --- GESTIONE DEEP LINK DI INVITO ---
            composable(
                route = "join_lega/{idLega}",
                arguments = listOf(navArgument("idLega") { type = NavType.IntType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "fantasanremo://join/{idLega}" }
                )
            ) { backStackEntry ->
                val idLega = backStackEntry.arguments?.getInt("idLega") ?: 0
                LaunchedEffect(idLega) {
                    // Memorizzo l'invito e mando a comporre la squadra:
                    // non si entra in una lega senza squadra
                    mainViewModel.impostaLegaDaInvito(idLega)
                    navController.navigate(Screen.NuovaSquadra.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
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
                route = "modifica_lega/{idLega}",
                arguments = listOf(navArgument("idLega") { type = NavType.IntType })
            ) { backStackEntry ->
                val idLega = backStackEntry.arguments?.getInt("idLega") ?: 0
                CreaLegaScreen(
                    viewModel = mainViewModel,
                    onBack = { navController.popBackStack() },
                    onLegaCreata = { },
                    idLegaDaModificare = idLega
                )
            }

            composable("aggiungi_lega") {
                AggiungiALegaScreen(
                    viewModel = mainViewModel,
                    onBack = { navController.popBackStack() },
                    onIscrizioneCompletata = {
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
            val isSelected = when(screen) {
                Screen.LeMieLeghe -> currentRoute == Screen.LeMieLeghe.route ||
                        currentRoute?.startsWith("dettaglio_lega") == true ||
                        currentRoute?.startsWith("modifica_formazione") == true ||
                        currentRoute?.startsWith("join_lega") == true
                Screen.NuovaSquadra -> currentRoute == Screen.NuovaSquadra.route ||
                        currentRoute == "crea_lega" ||
                        currentRoute == "aggiungi_lega"
                else -> currentRoute == screen.route
            }

            NavigationBarItem(
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
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
