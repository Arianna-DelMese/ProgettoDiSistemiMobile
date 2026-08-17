package com.example.progettodisistemimobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.Cantante
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel = viewModel()) {
    // 0 = Sanremo, 1 = Fantasanremo
    var selectedTab by remember { mutableIntStateOf(1) } 

    Column(modifier = Modifier.fillMaxSize()) {
        // --- TOP NAVIGATION BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sanremo",
                fontSize = 22.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { selectedTab = 0 }
            )
            
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))

            Text(
                text = "Fantasanremo",
                fontSize = 22.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { selectedTab = 1 }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 1) {
                FantasanremoView(viewModel)
            } else {
                SanremoView(viewModel)
            }
        }
    }
}

@Composable
fun FantasanremoView(viewModel: MainViewModel) {
    val cantanti by viewModel.tuttiICantantiPerPunti.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 0.dp)
    ) {
        Text(
            text = "Classifica Fantasanremo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(cantanti) { index, cantante ->
                CantanteRow(
                    position = index + 1, 
                    cantante = cantante, 
                    valoreDestra = cantante.punti.toString(),
                    isSanremo = false
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun SanremoView(viewModel: MainViewModel) {
    var selectedEvening by remember { mutableIntStateOf(1) }
    val cantanti by viewModel.tuttiICantantiPerPunti.collectAsState(initial = emptyList())

    val isEvening4 = selectedEvening == 4
    // Colore di accento e sfondo per la serata 4
    val externalBgColor = if (isEvening4) Color(0xFF121212) else MaterialTheme.colorScheme.secondaryContainer
    val accentColor = if (isEvening4) Color(0xFFFFD700) else Color.Gray
    val cardContentColor = if (isEvening4) Color.White else Color.Black

    val cantantiSerata = remember(cantanti, selectedEvening) {
        cantanti.mapNotNull { cantante ->
            val pos = when(selectedEvening) {
                1 -> cantante.pos_serata_1
                2 -> cantante.pos_serata_2
                3 -> cantante.pos_serata_3
                4 -> cantante.pos_serata_4
                5 -> cantante.pos_serata_5
                else -> null
            }
            if (pos != null) cantante to pos else null
        }.sortedBy { it.second }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(externalBgColor)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column {
            // Selettore serate con effetto tab/collinetta e bordi sempre visibili
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .zIndex(1f), // Per stare sopra il bordo della card
                horizontalArrangement = Arrangement.Start
            ) {
                (1..5).forEach { evening ->
                    val isSelected = selectedEvening == evening
                    val isSpecial4 = evening == 4
                    
                    // Colore quadratino selezionato
                    val selectedColor = if (isSpecial4) Color(0xFFFFD700) else Color.White
                    val baseBorderColor = if (isEvening4) Color.DarkGray else Color.Gray

                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 36.dp)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .offset(y = 1.dp) // Si fonde col bordo card
                                        .background(color = selectedColor)
                                        .border(width = 1.dp, color = accentColor)
                                } else {
                                    Modifier
                                        .background(color = Color.Transparent)
                                        .border(width = 0.5.dp, color = baseBorderColor)
                                }
                            )
                            .clickable { selectedEvening = evening },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = evening.toString(),
                            color = if (isSelected) Color.Black else (if (isEvening4) Color.LightGray else Color.DarkGray),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Riquadro Classifica
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.98f)
                    .border(
                        width = if (isEvening4) 2.dp else 1.dp,
                        color = accentColor,
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp, topEnd = 12.dp)
                    ),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp, topEnd = 12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isEvening4) Color(0xFF1E1E1E) else Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEvening4) "Serata Cover & Duetti" else "Classifica Serata $selectedEvening",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEvening4) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(cantantiSerata) { index, pair ->
                            val cantante = pair.first
                            CantanteRow(
                                position = index + 1,
                                cantante = cantante,
                                valoreDestra = "",
                                isSanremo = true,
                                isEvening4 = isEvening4,
                                overrideTextColor = cardContentColor
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp), 
                                color = (if (isEvening4) Color.Gray else Color.LightGray).copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CantanteRow(
    position: Int, 
    cantante: Cantante, 
    valoreDestra: String,
    isSanremo: Boolean,
    isEvening4: Boolean = false,
    overrideTextColor: Color? = null
) {
    val singerName = if (isEvening4 && !cantante.ospite.isNullOrBlank()) {
        "${cantante.nome_cantante} con ${cantante.ospite}"
    } else {
        cantante.nome_cantante
    }

    val songTitle = if (isEvening4 && !cantante.cover.isNullOrBlank()) {
        cantante.cover
    } else {
        cantante.canzone
    }

    // Per Sanremo invertiamo: Canzone sopra (Bold), Cantante sotto
    val mainText = if (isSanremo) songTitle ?: "" else singerName
    val subText = if (isSanremo) singerName else songTitle ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val posColor = when(position) {
            1 -> Color(0xFFFFD700)
            2 -> Color(0xFFC0C0C0)
            3 -> Color(0xFFCD7F32)
            else -> overrideTextColor ?: MaterialTheme.colorScheme.onSurface
        }

        Text(
            text = position.toString(), 
            modifier = Modifier.width(36.dp), 
            fontWeight = if (position <= 3) FontWeight.Bold else FontWeight.Normal,
            color = posColor,
            fontSize = 16.sp,
            textAlign = TextAlign.Start
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mainText, 
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = overrideTextColor ?: Color.Unspecified
            )
            Text(
                text = subText, 
                style = MaterialTheme.typography.bodySmall,
                color = if (overrideTextColor != null) overrideTextColor.copy(alpha = 0.7f) else Color.Gray,
                fontSize = 13.sp
            )
        }
        
        if (valoreDestra.isNotEmpty()) {
            Text(
                text = valoreDestra, 
                modifier = Modifier.width(60.dp), 
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
