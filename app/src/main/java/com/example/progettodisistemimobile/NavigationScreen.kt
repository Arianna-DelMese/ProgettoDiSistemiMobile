package com.example.progettodisistemimobile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface Screen {
    val route: String
    val label: String
    val icon: ImageVector?

    data object Home : Screen {
        override val route = "home"
        override val label = "Home"
        override val icon = Icons.Default.Home
    }

    data object LeMieLeghe : Screen {
        override val route = "leghe"
        override val label = "Le mie leghe"
        override val icon = Icons.Default.Star
    }

    data object NuovaSquadra : Screen {
        override val route = "nuova_squadra"
        override val label = "Nuova squadra"
        override val icon = Icons.Default.AddBox
    }

    data object Shop : Screen {
        override val route = "shop"
        override val label = "Shop"
        override val icon = Icons.Default.ShoppingBasket
    }

    data object Profilo : Screen {
        override val route = "profilo"
        override val label = "Profilo"
        override val icon = Icons.Default.Person
    }

    // Pagine di dettaglio (non presenti nella bottom bar)
    data object DettaglioLega : Screen {
        override val route = "dettaglio_lega/{idLega}/{nomeLega}"
        override val label = "Dettaglio Lega"
        override val icon = null
        
        fun createRoute(idLega: Int, nomeLega: String) = "dettaglio_lega/$idLega/$nomeLega"
    }

    data object ModificaFormazione : Screen {
        override val route = "modifica_formazione/{idLega}"
        override val label = "Modifica Formazione"
        override val icon = null

        fun createRoute(idLega: Int) = "modifica_formazione/$idLega"
    }
}

val screensInBottomBar = listOf(
    Screen.Home,
    Screen.LeMieLeghe,
    Screen.NuovaSquadra,
    Screen.Shop,
    Screen.Profilo
)
