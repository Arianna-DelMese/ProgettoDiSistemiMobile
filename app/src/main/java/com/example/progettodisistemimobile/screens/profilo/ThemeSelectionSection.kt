package com.example.progettodisistemimobile.screens.profilo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThemeSelectionSection(
    themeMode: String,
    onThemeChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val themes = listOf("Chiaro", "Scuro", "Sistema")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Scegli tema: ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(16.dp))
        Box {
            OutlinedCard(onClick = { expanded = true }, modifier = Modifier.width(150.dp)) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = themeMode, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                themes.forEach { t ->
                    DropdownMenuItem(text = { Text(t) }, onClick = { onThemeChange(t); expanded = false })
                }
            }
        }
    }
}
