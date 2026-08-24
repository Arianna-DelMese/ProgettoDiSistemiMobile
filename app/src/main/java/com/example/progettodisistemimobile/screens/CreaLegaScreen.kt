package com.example.progettodisistemimobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreaLegaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLegaCreata: (Int) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var descrizione by rememberSaveable { mutableStateOf("") }
    var pubblica by rememberSaveable { mutableStateOf(true) }
    var salvataggioInCorso by rememberSaveable { mutableStateOf(false) }

    val nomeValido = nome.trim().length >= 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crea una lega") },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome della lega") },
                supportingText = { Text("Almeno 3 caratteri") },
                singleLine = true,
                enabled = !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it },
                label = { Text("Descrizione") },
                minLines = 3,
                maxLines = 5,
                enabled = !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text("Stato", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !pubblica,
                    onClick = { pubblica = false },
                    enabled = !salvataggioInCorso,
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Privata")
                }
                SegmentedButton(
                    selected = pubblica,
                    onClick = { pubblica = true },
                    enabled = !salvataggioInCorso,
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Pubblica")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (pubblica) "Chiunque potrà trovare la lega e iscriversi."
                else "Solo chi ha l'invito potrà entrare.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    salvataggioInCorso = true
                    viewModel.creaLegaConSquadra(
                        nomeLega = nome,
                        descrizione = descrizione,
                        pubblica = pubblica,
                        immagine = null,     // blocco 2
                        latitudine = null,   // blocco 3
                        longitudine = null,
                        onFatto = { idLega -> onLegaCreata(idLega) }
                    )
                },
                enabled = nomeValido && !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Conferma")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}