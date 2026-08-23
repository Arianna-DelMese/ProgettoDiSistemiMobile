package com.example.progettodisistemimobile.screens.leghe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.progettodisistemimobile.Screen
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun LegaDetailScreen(
    idLega: Int,
    nomeLega: String,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val currentUsername by viewModel.currentUser.collectAsState()
    val lega by viewModel.getLega(idLega).collectAsState(initial = null)
    val partecipazione by viewModel.getDatiPartecipazione(idLega, currentUsername).collectAsState(initial = null)
    val squadra by viewModel.getSquadra(idLega, currentUsername).collectAsState(initial = emptyList())
    val classifica by viewModel.getClassificaLegaConCapitano(idLega).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // --- HEADER E PUNTI ---
        LegaHeaderSection(
            lega = lega,
            nomeLegaFallback = nomeLega,
            partecipazione = partecipazione,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TOP PERFORMERS E BOTTONE MODIFICA ---
        TopPerformersSection(
            squadra = squadra,
            onModificaClick = { navController.navigate(Screen.ModificaFormazione.createRoute(idLega)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- CLASSIFICA UTENTI ---
        UserRankingSection(classifica = classifica)
    }
}
