package com.example.progettodisistemimobile.screens.leghe.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettodisistemimobile.data.UserRankingItem

@Composable
fun UserRankingSection(classifica: List<UserRankingItem>) {
    Column {
        Text(
            text = "Classifica Lega",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (classifica.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nessun utente presente.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(classifica) { index, item ->
                        UserRankingRow(position = index + 1, item = item)
                        if (index < classifica.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRankingRow(position: Int, item: UserRankingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position.toString(),
            modifier = Modifier.width(36.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (position <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.utenteInLega.nome_utente,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Capitano: ${item.nomeCapitano ?: "-"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Text(
            text = "${item.utenteInLega.punti} PT",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
