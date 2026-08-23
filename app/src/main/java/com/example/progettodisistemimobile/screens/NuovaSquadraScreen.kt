package com.example.progettodisistemimobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.Cantante
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun NuovaSquadraScreen(viewModel: MainViewModel) {
    val username by viewModel.currentUser.collectAsState()
    val tokenDisponibili by viewModel.getTokens(username).collectAsState(initial = 0)
    val cantanti by viewModel.tuttiICantantiPerPunti.collectAsState(initial = emptyList())
    val selezionati by viewModel.cantantiSelezionati.collectAsState()
    val capitano by viewModel.capitano.collectAsState()

    var ricerca by rememberSaveable { mutableStateOf("") }

    // Costo totale: sommo i prezzi dei cantanti selezionati
    val costoTotale = cantanti
        .filter { it.nome_cantante in selezionati }
        .sumOf { it.prezzo }

    // Lista filtrata dalla ricerca (per nome cantante o titolo canzone)
    val cantantiFiltrati = cantanti.filter {
        it.nome_cantante.contains(ricerca, ignoreCase = true) ||
                it.canzone.contains(ricerca, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        Spacer(Modifier.height(16.dp))

        Text(
            text = "I miei token: $tokenDisponibili",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ricerca,
            onValueChange = { ricerca = it },
            label = { Text("Cerca un cantante") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cantantiFiltrati, key = { it.nome_cantante }) { cantante ->
                val isSelezionato = cantante.nome_cantante in selezionati
                // Posso ancora permettermelo? (se è già selezionato, posso sempre toglierlo)
                val selezionabile = isSelezionato ||
                        (selezionati.size < MainViewModel.CANTANTI_PER_SQUADRA &&
                                costoTotale + cantante.prezzo <= tokenDisponibili)

                CantanteSelezionabileRow(
                    cantante = cantante,
                    isSelezionato = isSelezionato,
                    isCapitano = capitano == cantante.nome_cantante,
                    abilitato = selezionabile,
                    onToggle = { viewModel.toggleCantante(cantante.nome_cantante) },
                    onCapitanoClick = { viewModel.impostaCapitanoSelezione(cantante.nome_cantante) }
                )
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Selezionati: ${selezionati.size}/${MainViewModel.CANTANTI_PER_SQUADRA}")
            Text(
                text = "Costo totale: $costoTotale",
                fontWeight = FontWeight.Bold,
                color = if (costoTotale > tokenDisponibili) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CantanteSelezionabileRow(
    cantante: Cantante,
    isSelezionato: Boolean,
    isCapitano: Boolean,
    abilitato: Boolean,
    onToggle: () -> Unit,
    onCapitanoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelezionato) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelezionato,
                onCheckedChange = { onToggle() },
                enabled = abilitato
            )

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = cantante.nome_cantante,
                    fontWeight = FontWeight.Bold,
                    color = if (abilitato) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
                Text(
                    text = cantante.canzone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = "${cantante.prezzo}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // La stella del capitano appare solo sui cantanti selezionati
            if (isSelezionato) {
                IconButton(onClick = onCapitanoClick) {
                    Icon(
                        imageVector = if (isCapitano) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Capitano",
                        tint = if (isCapitano) Color(0xFFFFD700) else Color.LightGray
                    )
                }
            } else {
                Spacer(Modifier.size(48.dp))
            }
        }
    }
}