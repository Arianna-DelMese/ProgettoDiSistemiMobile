package com.example.progettodisistemimobile.screens.leghe.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.progettodisistemimobile.Screen
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun LegaDetailScreen(
    idLega: Int,
    nomeLega: String,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: MainViewModel
) {
    val currentUsername by viewModel.currentUser.collectAsState()
    val lega by viewModel.getLega(idLega).collectAsState(initial = null)
    val partecipazione by viewModel.getDatiPartecipazione(idLega, currentUsername).collectAsState(initial = null)
    val squadra by viewModel.getSquadra(idLega, currentUsername).collectAsState(initial = emptyList())
    val classifica by viewModel.getClassificaLegaConCapitano(idLega).collectAsState(initial = emptyList())

    val puntiTitolari = remember(squadra) {
        squadra.take(5).mapIndexed { index, cantante ->
            if (index == 0) cantante.punti * 2 else cantante.punti
        }.sum()
    }

    var mostraConfermaAbbandono by rememberSaveable { mutableStateOf(false) }

    if (mostraConfermaAbbandono) {
        AlertDialog(
            onDismissRequest = { mostraConfermaAbbandono = false },
            title = { Text("Abbandonare la lega?") },
            text = {
                Text(
                    "Sei sicuro? La tua squadra verrà eliminata e non potrai recuperare i token spesi.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostraConfermaAbbandono = false
                    viewModel.abbandonaLega(idLega) { onBack() }
                }) {
                    Text("Conferma", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostraConfermaAbbandono = false }) {
                    Text("Annulla", color = MaterialTheme.colorScheme.outline)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        LegaHeaderSection(
            lega = lega,
            nomeLegaFallback = nomeLega,
            partecipazione = partecipazione,
            puntiTitolari = puntiTitolari,
            onBack = onBack,
            onModificaLega = { navController.navigate("modifica_lega/$idLega") },
            onAbbandonaLega = { mostraConfermaAbbandono = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TopPerformersSection(
                squadra = squadra,
                onModificaClick = { navController.navigate(Screen.ModificaFormazione.createRoute(idLega)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            UserRankingSection(classifica = classifica)
        }
    }
}