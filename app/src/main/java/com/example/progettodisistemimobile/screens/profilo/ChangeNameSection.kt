package com.example.progettodisistemimobile.screens.profilo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChangeNameSection(
    newNameInput: String,
    onNameChange: (String) -> Unit,
    isNameAvailable: Boolean,
    currentSessionUsername: String,
    onConfirm: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Cambia nome: ", fontSize = 16.sp, style = MaterialTheme.typography.bodyLarge)
            TextField(
                value = newNameInput,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Nuovo username") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        }
        
        if (!isNameAvailable && newNameInput.isNotEmpty()) {
            Text(
                text = "Nome utente non disponibile",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (isNameAvailable && newNameInput.isNotEmpty() && newNameInput != currentSessionUsername) {
            Button(
                onClick = onConfirm,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("Conferma")
            }
        }
    }
}
