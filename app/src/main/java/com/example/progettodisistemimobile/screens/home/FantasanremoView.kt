package com.example.progettodisistemimobile.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.MainViewModel

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
