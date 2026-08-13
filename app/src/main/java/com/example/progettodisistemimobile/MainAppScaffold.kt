package com.example.progettodisistemimobile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.progettodisistemimobile.screens.*
import androidx.compose.ui.graphics.Color

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        // Qui definiamo quali pagine caricare
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.LeMieLeghe.route) {
                LeMieLegheScreen()
            }
            composable(Screen.NuovaSquadra.route) {
                NuovaSquadraScreen()
            }
            composable(Screen.Shop.route) {
                ShopScreen()
            }
            composable(Screen.Profilo.route) {
                ProfiloScreen()
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
                alwaysShowLabel = true, // Forza il testo a restare sempre visibile
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
                    // Colore dell'icona quando selezionata
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    // Colore del cerchio/pillola dietro l'icona
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    // Colore del testo quando selezionato
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    // Colore quando NON è selezionato
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 2,
                        // Se il testo è selezionato, lo rendiamo in grassetto
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else null
                    )
                },
                icon = { Icon(screen.icon, contentDescription = screen.label) }
            )
        }
    }
}