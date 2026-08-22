package com.example.progettodisistemimobile.screens.leghe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.data.Lega
import com.example.progettodisistemimobile.data.UtenteInLega

@Composable
fun LegaHeaderSection(
    lega: Lega?,
    nomeLegaFallback: String,
    partecipazione: UtenteInLega?,
    onBack: () -> Unit
) {
    Column {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }

            if (lega?.immagine != null) {
                AsyncImage(
                    model = lega.immagine,
                    contentDescription = null,
                    modifier = Modifier.size(45.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(45.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (lega?.nome_lega ?: nomeLegaFallback).take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = lega?.nome_lega ?: nomeLegaFallback,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row {
                TextButton(onClick = { /* Invita */ }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Invita", fontSize = 14.sp)
                }
                if (partecipazione?.stato == true) {
                    TextButton(onClick = { /* Modifica */ }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Modifica", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // --- PUNTI ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "I miei punti: ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${partecipazione?.punti ?: 0} PT",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
