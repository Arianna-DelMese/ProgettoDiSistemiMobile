package com.example.progettodisistemimobile.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.Lega
import com.example.progettodisistemimobile.data.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AggiungiALegaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onIscrizioneCompletata: () -> Unit
) {
    var ricerca by rememberSaveable { mutableStateOf("") }
    var legaSelezionata by remember { mutableStateOf<Lega?>(null) }
    var salvataggioInCorso by rememberSaveable { mutableStateOf(false) }

    val leghe by viewModel.cercaLeghe(ricerca).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aggiungi a lega esistente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ricerca,
                onValueChange = { ricerca = it },
                label = { Text("Cerca dal nome della lega") },
                singleLine = true,
                enabled = !salvataggioInCorso,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (leghe.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (ricerca.isBlank())
                            "Non ci sono leghe pubbliche a cui iscriverti."
                        else
                            "Nessuna lega trovata con questo nome.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(leghe, key = { it.id_lega }) { lega ->
                        LegaSelezionabileRow(
                            lega = lega,
                            isSelezionata = legaSelezionata?.id_lega == lega.id_lega,
                            onClick = { legaSelezionata = lega }
                        )
                    }
                }
            }

            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    legaSelezionata?.let { lega ->
                        salvataggioInCorso = true
                        viewModel.uniscitiALegaConSquadra(lega.id_lega) {
                            onIscrizioneCompletata()
                        }
                    }
                },
                enabled = legaSelezionata != null && !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (legaSelezionata == null) "Seleziona una lega"
                    else "Entra in ${legaSelezionata?.nome_lega}"
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LegaSelezionabileRow(
    lega: Lega,
    isSelezionata: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelezionata) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelezionata, onClick = { onClick() })

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(lega.nome_lega, fontWeight = FontWeight.Bold)
                lega.descrizione?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }

            // Segnala le leghe che hanno una posizione: serviranno per la mappa
            if (lega.latitudine != null) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Ha una posizione",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}