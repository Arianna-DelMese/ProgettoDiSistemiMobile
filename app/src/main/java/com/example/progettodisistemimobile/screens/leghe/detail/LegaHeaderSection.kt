package com.example.progettodisistemimobile.screens.leghe.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onModificaLega: () -> Unit,
    onAbbandonaLega: () -> Unit,
    onBack: () -> Unit
    ) {
    val context = LocalContext.current

    Column {
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        val leagueId = lega?.id_lega ?: 0
                        // Il link tecnico che verrà riconosciuto dal Manifest
                        val deepLink = "fantasanremo://join/$leagueId"
                        val shareText = "Entra nella mia lega '${lega?.nome_lega ?: nomeLegaFallback}'! Clicca qui per unirti: $deepLink"

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Condividi Lega"))
                    },
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Link, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invita", fontSize = 14.sp)
                }

                if (partecipazione?.stato == true) {
                    TextButton(
                        onClick = onModificaLega,
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Modifica", fontSize = 14.sp)
                    }
                } else if (partecipazione != null) {
                    TextButton(
                        onClick = onAbbandonaLega,
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Abbandona", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

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
