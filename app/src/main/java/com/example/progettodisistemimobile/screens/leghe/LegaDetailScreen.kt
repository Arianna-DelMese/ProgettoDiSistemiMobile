package com.example.progettodisistemimobile.screens.leghe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun LegaDetailScreen(
    idLega: Int,
    nomeLega: String,
    onBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val currentUsername by viewModel.currentUser.collectAsState()
    val lega by viewModel.getLega(idLega).collectAsState(initial = null)
    val partecipazione by viewModel.getDatiPartecipazione(idLega, currentUsername).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // --- HEADER PERSONALIZZATO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsante Indietro
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }

            // Immagine Lega
            if (lega?.immagine != null) {
                AsyncImage(
                    model = lega?.immagine,
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (lega?.nome_lega ?: nomeLega).take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nome Lega - Permette l'andata a capo
            Text(
                text = lega?.nome_lega ?: nomeLega,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Bottoni Azione
            Row {
                TextButton(
                    onClick = { /* Azione Invita */ },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Invita", fontSize = 14.sp)
                }
                if (partecipazione?.stato == true) {
                    TextButton(
                        onClick = { /* Azione Modifica */ },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Modifica", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // --- SEZIONE PUNTI UTENTE ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "I miei punti: ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${partecipazione?.punti ?: 0} PT",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Dettagli squadra e classifiche della lega appariranno qui.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}