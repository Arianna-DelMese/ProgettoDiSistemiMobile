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
                NuovaSquadraScreen()
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

            composable(
                route = Screen.ModificaFormazione.route,
                arguments = listOf(
                    navArgument("idLega") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val idLega = backStackEntry.arguments?.getInt("idLega") ?: 0
                ModificaFormazioneScreen(
                    idLega = idLega,
                    onBack = { navController.popBackStack() }
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
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
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
