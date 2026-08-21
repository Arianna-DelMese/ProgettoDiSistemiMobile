package com.example.progettodisistemimobile.screens.leghe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.data.Cantante
import com.example.progettodisistemimobile.data.MainViewModel
import com.example.progettodisistemimobile.data.UtenteInLega

@OptIn(ExperimentalMaterial3Api::class)
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
    val squadra by viewModel.getSquadra(idLega, currentUsername).collectAsState(initial = emptyList())
    val classifica by viewModel.getClassificaLega(idLega).collectAsState(initial = emptyList())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Squadra", "Classifica")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
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
                    model = lega?.immagine,
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
                        text = (lega?.nome_lega ?: nomeLega).take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = lega?.nome_lega ?: nomeLega,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row {
                TextButton(onClick = { /* Azione Invita */ }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Invita", fontSize = 14.sp)
                }
                if (partecipazione?.stato == true) {
                    TextButton(onClick = { /* Azione Modifica */ }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Modifica", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // --- PUNTI UTENTE ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "I miei punti: ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${partecipazione?.punti ?: 0} PT",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CLASSIFICA ORIZZONTALE SQUADRA (Top Performers) ---
        if (squadra.isNotEmpty()) {
            HorizontalRankingRow(squadra)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- TABS ---
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CONTENUTO DINAMICO ---
        Box(modifier = Modifier.weight(1f)) {
            if (selectedTabIndex == 0) {
                TeamView(squadra)
            } else {
                RankingView(classifica)
            }
        }
    }
}

@Composable
fun HorizontalRankingRow(squadra: List<Cantante>) {
    // Il capitano è il primo (ruolo 0) per via dell'order by nel DAO
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
                    Text(
                        text = capitano.nome_cantante,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "${capitano.punti} PT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
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
                modifier = Modifier
                    .height(45.dp)
                    .padding(horizontal = 8.dp),
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = cantante.nome_cantante, // Mostriamo il nome completo
                            fontSize = 12.sp, // Ridotto leggermente per favorire l'andata a capo
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                            // maxLines rimosso per permettere andata a capo
                        )
                        Text(
                            text = "${cantante.punti} PT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Se mancano cantanti, mostriamo placeholder
                repeat(3 - top3Altri.size) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("-", fontSize = 12.sp, color = Color.LightGray)
                        Text("0 PT", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamView(squadra: List<Cantante>) {
    if (squadra.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Non hai ancora creato una squadra per questa lega.", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(squadra) { index, cantante ->
                val roleLabel = when (index) {
                    0 -> "CAPITANO"
                    in 1..4 -> "TITOLARE"
                    else -> "RISERVA"
                }
                val roleColor = when (index) {
                    0 -> Color(0xFFFFD700)
                    in 1..4 -> MaterialTheme.colorScheme.primary
                    else -> Color.Gray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = roleLabel, fontSize = 10.sp, fontWeight = FontWeight.Black, color = roleColor)
                            Text(
                                text = cantante.nome_cantante, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp
                            )
                            Text(text = cantante.canzone, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text(text = "${cantante.punti} PT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingView(classifica: List<UtenteInLega>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(classifica) { index, user ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${index + 1}°",
                    modifier = Modifier.width(40.dp),
                    fontWeight = FontWeight.Bold,
                    color = if (index == 0) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                )
                Text(text = user.nome_utente, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Text(text = "${user.punti} PT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}
