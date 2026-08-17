package com.example.progettodisistemimobile.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun SanremoView(viewModel: MainViewModel) {
    var selectedEvening by remember { mutableIntStateOf(1) }
    val cantanti by viewModel.tuttiICantantiPerPunti.collectAsState(initial = emptyList())

    val isEvening4 = selectedEvening == 4
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
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .zIndex(2f),
                horizontalArrangement = Arrangement.Start
            ) {
                (1..5).forEach { evening ->
                    val isSelected = selectedEvening == evening
                    val isSpecial4 = evening == 4
                    val selectedColor = if (isSpecial4) Color(0xFFFFD700) else Color.White
                    val baseBorderColor = if (isEvening4) Color.DarkGray else Color.Gray

                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 36.dp)
                            .zIndex(if (isSelected) 3f else 1f)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .offset(y = 1.dp)
                                        .background(color = selectedColor, shape = RectangleShape)
                                        .border(width = 1.dp, color = accentColor, shape = RectangleShape)
                                } else {
                                    Modifier
                                        .background(color = Color.Transparent, shape = RectangleShape)
                                        .border(width = 0.5.dp, color = baseBorderColor, shape = RectangleShape)
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.98f)
                    .zIndex(1f)
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
                        text = if (isEvening4) "Serata Cover & Duetti ⭐" else "Classifica Serata $selectedEvening",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEvening4) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(cantantiSerata) { (cantante, posizioneReale) ->
                            CantanteRow(
                                position = posizioneReale, // Prende la posizione reale dal DB
                                cantante = cantante,
                                valoreDestra = "",
                                isSanremo = true,
                                isEvening4 = isEvening4,
                                overrideTextColor = cardContentColor
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp), 
                                color = (if (isEvening4) Color.Gray else Color.LightGray).copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
