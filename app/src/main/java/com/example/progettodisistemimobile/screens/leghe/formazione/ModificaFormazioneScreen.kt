package com.example.progettodisistemimobile.screens.leghe.formazione

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun ModificaFormazioneScreen(
    idLega: Int,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val currentUsername by viewModel.currentUser.collectAsState()
    val lega by viewModel.getLega(idLega).collectAsState(initial = null)
    val squadra by viewModel.getSquadra(idLega, currentUsername).collectAsState(initial = emptyList())

    // Stati per la gestione del Drag and Drop
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var hoveredIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffsetY by remember { mutableStateOf(0f) }

    // Mappa coordinate per calcolo hover preciso
    val itemCoords = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- HEADER (da FormazioneHeader.kt) ---
        FormazioneHeader(
            nomeLega = lega?.nome_lega ?: "",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- TITOLARI ---
            item {
                Text(
                    text = "Titolari",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            val titolari = squadra.filterIndexed { index, _ -> index < 5 }
            itemsIndexed(titolari) { index, cantante ->
                DraggableSingerCard(
                    index = index,
                    cantante = cantante,
                    isCaptain = index == 0,
                    canBeCaptain = true,
                    isDragged = draggedIndex == index,
                    isHovered = hoveredIndex == index,
                    offsetY = if (draggedIndex == index) draggingOffsetY else 0f,
                    onDragStart = { draggedIndex = index },
                    onDrag = { deltaY ->
                        draggingOffsetY += deltaY
                        val bounds = itemCoords[index] ?: return@DraggableSingerCard
                        val currentCenterY = (bounds.first + bounds.second) / 2 + draggingOffsetY
                        hoveredIndex = itemCoords.entries.find {
                            it.key != index && currentCenterY > it.value.first && currentCenterY < it.value.second
                        }?.key
                    },
                    onDragEnd = {
                        hoveredIndex?.let { target ->
                            viewModel.scambiaRuoli(idLega, currentUsername, squadra[index], index, squadra[target], target)
                        }
                        draggedIndex = null
                        hoveredIndex = null
                        draggingOffsetY = 0f
                    },
                    onCaptainClick = { viewModel.impostaCapitano(idLega, currentUsername, cantante, squadra) },
                    onPositioned = { top, bottom -> if (draggedIndex == null) itemCoords[index] = top to bottom }
                )
            }

            // --- RISERVE ---
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Riserve",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val riserve = squadra.filterIndexed { index, _ -> index >= 5 }
            itemsIndexed(riserve) { i, cantante ->
                val actualIndex = i + 5
                DraggableSingerCard(
                    index = actualIndex,
                    cantante = cantante,
                    isCaptain = false,
                    canBeCaptain = false,
                    isDragged = draggedIndex == actualIndex,
                    isHovered = hoveredIndex == actualIndex,
                    offsetY = if (draggedIndex == actualIndex) draggingOffsetY else 0f,
                    onDragStart = { draggedIndex = actualIndex },
                    onDrag = { deltaY ->
                        draggingOffsetY += deltaY
                        val bounds = itemCoords[actualIndex] ?: return@DraggableSingerCard
                        val currentCenterY = (bounds.first + bounds.second) / 2 + draggingOffsetY
                        hoveredIndex = itemCoords.entries.find {
                            it.key != actualIndex && currentCenterY > it.value.first && currentCenterY < it.value.second
                        }?.key
                    },
                    onDragEnd = {
                        hoveredIndex?.let { target ->
                            viewModel.scambiaRuoli(idLega, currentUsername, squadra[actualIndex], actualIndex, squadra[target], target)
                        }
                        draggedIndex = null
                        hoveredIndex = null
                        draggingOffsetY = 0f
                    },
                    onCaptainClick = {},
                    onPositioned = { top, bottom -> if (draggedIndex == null) itemCoords[actualIndex] = top to bottom }
                )
            }
        }
    }
}
