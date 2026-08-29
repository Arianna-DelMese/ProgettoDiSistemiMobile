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
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

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

    // 0 = lista, 1 = mappa
    var vista by rememberSaveable { mutableIntStateOf(0) }

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

            // La ricerca per nome serve solo nella vista a lista
            if (vista == 0) {
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
            }


            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dal nome",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (vista == 0) FontWeight.Bold else FontWeight.Normal,
                    textDecoration = TextDecoration.Underline,
                    color = if (vista == 0) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { vista = 0 }
                        .padding(horizontal = 8.dp)
                )

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))

                Text(
                    text = "Dalla posizione",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (vista == 1) FontWeight.Bold else FontWeight.Normal,
                    textDecoration = TextDecoration.Underline,
                    color = if (vista == 1) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            vista = 1
                            ricerca = ""   // sulla mappa mostro tutte le leghe
                        }
                        .padding(horizontal = 8.dp)
                )
            }

            if (vista == 1) {
                MappaLeghe(
                    leghe = leghe,
                    onLegaSelezionata = { legaSelezionata = it },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )

                Text(
                    text = "© OpenStreetMap contributors",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                legaSelezionata?.let {
                    Text(
                        text = "Selezionata: ${it.nome_lega}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else if (leghe.isEmpty()) {
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
            containerColor = if (isSelezionata)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelezionata, onClick = { onClick() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
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

            // Segnala le leghe che compaiono sulla mappa
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