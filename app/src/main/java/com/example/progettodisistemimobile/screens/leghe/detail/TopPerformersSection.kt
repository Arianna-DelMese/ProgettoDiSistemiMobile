package com.example.progettodisistemimobile.screens.leghe.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettodisistemimobile.data.Cantante

@Composable
fun TopPerformersSection(
    squadra: List<Cantante>,
    onModificaClick: () -> Unit
) {
    Column {
        if (squadra.isNotEmpty()) {
            HorizontalRankingRow(squadra)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTTONE MODIFICA FORMAZIONE ---
        Button(
            onClick = onModificaClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(text = "Modifica formazione", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HorizontalRankingRow(squadra: List<Cantante>) {
    // Capitano (primo della lista ordinata per ruolo)
    val capitano = squadra.firstOrNull()
    // Top 3 tra gli ALTRI componenti
    val top3Altri = squadra.drop(1).sortedByDescending { it.punti }.take(3)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- CAPITANO (Sinistra) ---
            if (capitano != null) {
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val words = capitano.nome_cantante.split(" ")
                    val formattedName = if (words.size >= 2) "${words[0]}\n${words[1]}" else capitano.nome_cantante
                    
                    Text(
                        text = formattedName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "${capitano.punti} PT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "CAPITANO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Divisore verticale sottile
            VerticalDivider(
                modifier = Modifier.height(40.dp).padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // --- TOP 3 PUNTI (Destra) ---
            Row(
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                top3Altri.forEach { cantante ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val words = cantante.nome_cantante.split(" ")
                        val formattedName = if (words.size >= 2) "${words[0]}\n${words[1]}" else cantante.nome_cantante

                        Text(
                            text = formattedName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )
                        Text(
                            text = "${cantante.punti} PT",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
