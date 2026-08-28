package com.example.progettodisistemimobile.screens.leghe.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    // Conferma prima di abbandonare: l'operazione è irreversibile
    var mostraConferma by remember { mutableStateOf(false) }

    if (mostraConferma) {
        AlertDialog(
            onDismissRequest = { mostraConferma = false },
            title = { Text("Abbandonare la lega?") },
            text = { Text("La tua squadra verrà eliminata e i token spesi non saranno restituiti.") },
            confirmButton = {
                TextButton(onClick = {
                    mostraConferma = false
                    viewModel.abbandonaLega(idLega) { onBack() }
                }) {
                    Text("Abbandona", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostraConferma = false }) { Text("Annulla") }
            }
        )
    }

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
            onBack = onBack,
            onModificaLega = { navController.navigate("modifica_lega/$idLega") },
            onAbbandonaLega = { mostraConferma = true }
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