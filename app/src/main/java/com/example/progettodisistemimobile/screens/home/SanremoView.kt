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
    // Utilizziamo il colore Terziario per Sanremo
    val accentColor = if (isEvening4) Color(0xFFFFD700) else MaterialTheme.colorScheme.tertiary
    // Sfondo esterno meno saturato (tertiaryContainer)
    val externalBgColor = MaterialTheme.colorScheme.tertiaryContainer
    val cardContentColor = MaterialTheme.colorScheme.onSurface

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
                    val selectedBg = if (isSelected) (if (isSpecial4) Color(0xFFFFD700) else MaterialTheme.colorScheme.tertiary) else Color.Transparent
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground

                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 36.dp)
                            .zIndex(if (isSelected) 3f else 1f)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .offset(y = 1.dp)
                                        .background(color = selectedBg, shape = RectangleShape)
                                        .border(width = 1.dp, color = accentColor, shape = RectangleShape)
                                } else {
                                    Modifier
                                        .background(color = Color.Transparent, shape = RectangleShape)
                                        .border(width = 0.5.dp, color = MaterialTheme.colorScheme.outline, shape = RectangleShape)
                                }
                            )
                            .clickable { selectedEvening = evening },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = evening.toString(),
                            color = contentColor,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEvening4) "Serata Cover & Duetti ⭐" else "Classifica Serata $selectedEvening",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(cantantiSerata) { (cantante, posizioneReale) ->
                            CantanteRow(
                                position = posizioneReale,
                                cantante = cantante,
                                valoreDestra = "",
                                isSanremo = true,
                                isEvening4 = isEvening4,
                                overrideTextColor = cardContentColor
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp), 
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}
