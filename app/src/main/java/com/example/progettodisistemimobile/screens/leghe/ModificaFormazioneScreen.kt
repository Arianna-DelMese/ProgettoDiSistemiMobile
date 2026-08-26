package com.example.progettodisistemimobile.screens.leghe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.progettodisistemimobile.data.Cantante
import com.example.progettodisistemimobile.data.MainViewModel
import kotlin.math.roundToInt

@Composable
fun ModificaFormazioneScreen(
    idLega: Int,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val currentUsername by viewModel.currentUser.collectAsState()
    val lega by viewModel.getLega(idLega).collectAsState(initial = null)
    val squadra by viewModel.getSquadra(idLega, currentUsername).collectAsState(initial = emptyList())

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var hoveredIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffsetY by remember { mutableStateOf(0f) }
    
    // Mappa coordinate statiche degli item
    val itemCoords = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- HEADER COMPATTO ---
        Surface(shadowElevation = 2.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "La mia formazione",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lega?.nome_lega ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

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
                    fontWeight = FontWeight.ExtraBold,
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
                        
                        // CALCOLO HOVER CALIBRATO AL CENTRO
                        val bounds = itemCoords[index] ?: return@DraggableSingerCard
                        val currentCenterY = (bounds.first + bounds.second) / 2 + draggingOffsetY
                        
                        hoveredIndex = itemCoords.entries.find { entry ->
                            entry.key != index && 
                            currentCenterY > entry.value.first && 
                            currentCenterY < entry.value.second
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
                    onPositioned = { top, bottom -> 
                        // Salviamo le coordinate solo se non stiamo trascinando l'item stesso
                        if (draggedIndex == null) itemCoords[index] = top to bottom 
                    }
                )
            }

            // --- RISERVE ---
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Riserve",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraBold,
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
                        
                        hoveredIndex = itemCoords.entries.find { entry ->
                            entry.key != actualIndex && 
                            currentCenterY > entry.value.first && 
                            currentCenterY < entry.value.second
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
                    onPositioned = { top, bottom -> 
                        if (draggedIndex == null) itemCoords[actualIndex] = top to bottom 
                    }
                )
            }
        }
    }
}

@Composable
fun DraggableSingerCard(
    index: Int,
    cantante: Cantante,
    isCaptain: Boolean,
    canBeCaptain: Boolean,
    isDragged: Boolean,
    isHovered: Boolean,
    offsetY: Float,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onCaptainClick: () -> Unit,
    onPositioned: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .zIndex(if (isDragged) 10f else 1f)
            .onGloballyPositioned { layout ->
                // Comunichiamo la posizione solo se l'item è fermo
                if (!isDragged) {
                    val top = layout.positionInRoot().y
                    onPositioned(top, top + layout.size.height)
                }
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isHovered) 2.5.dp else 1.dp,
                    color = if (isHovered) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .shadow(if (isDragged) 12.dp else 0.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isDragged) MaterialTheme.colorScheme.surfaceVariant 
                                 else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Muovi",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .pointerInput(cantante.nome_cantante) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDrag = { change, amount -> 
                                    change.consume()
                                    onDrag(amount.y) 
                                },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            )
                        }
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cantante.nome_cantante,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${cantante.punti} PT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = cantante.canzone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (canBeCaptain) {
                    IconButton(onClick = onCaptainClick) {
                        Icon(
                            imageVector = if (isCaptain) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Capitano",
                            tint = if (isCaptain) Color(0xFFFFD700) else Color.LightGray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
            }
        }
    }
}
