package com.example.progettodisistemimobile.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

    val mainText = if (isSanremo) songTitle else singerName
    val subText = if (isSanremo) singerName else songTitle

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
                color = overrideTextColor?.copy(alpha = 0.7f) ?: Color.Gray,
                fontSize = 13.sp
            )
        }

        if (valoreDestra.isNotEmpty()) {
            Text(
                text = valoreDestra,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
