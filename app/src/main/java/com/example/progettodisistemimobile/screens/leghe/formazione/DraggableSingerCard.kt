package com.example.progettodisistemimobile.screens.leghe.formazione

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.progettodisistemimobile.data.Cantante
import kotlin.math.roundToInt

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
                // Maniglia trascinamento (Meccanica di trascinamento)
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

                // Info Cantante (Visualizzazione)
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

                // Stella Capitano (Meccanica delle stelle)
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
