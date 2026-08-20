package com.example.progettodisistemimobile.screens.profilo

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.data.Utente

@Composable
fun ProfileHeader(utente: Utente?, sessionUsername: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        if (utente?.foto_profilo != null) {
            AsyncImage(
                model = utente.foto_profilo,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = utente?.nome_utente ?: sessionUsername,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TokenDisplay(tokens: Int) {
    Row(modifier = Modifier.padding(bottom = 32.dp)) {
        Text(text = "I miei token: ", fontSize = 18.sp, color = Color.Gray)
        Text(
            text = "$tokens",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ChangeNameSection(
    newNameInput: String,
    onNameChange: (String) -> Unit,
    isNameAvailable: Boolean,
    currentSessionUsername: String,
    onConfirm: () -> Unit
) {
    Column {
        Text(text = "Impostazioni Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Cambia nome: ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            TextField(
                value = newNameInput,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Nuovo username") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
        }
        
        if (!isNameAvailable && newNameInput.isNotEmpty()) {
            Text("Nome utente già scelto", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
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

@Composable
fun ChangeImageSection(
    showImagePreview: Boolean,
    selectedImageUri: Uri?,
    onChooseSource: () -> Unit,
    onProceed: () -> Unit
) {
    if (!showImagePreview) {
        Button(
            onClick = onChooseSource,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambia immagine profilo")
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Anteprima",
                modifier = Modifier.size(160.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                TextButton(onClick = onChooseSource) {
                    Text("Scegli un'altra immagine")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onProceed) {
                    Text("Procedi")
                }
            }
        }
    }
}

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

@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGallerySelect: () -> Unit,
    onCameraSelect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Sorgente") },
        text = { Text("Vuoi scattare una foto o caricarne una esistente?") },
        confirmButton = {
            TextButton(onClick = onGallerySelect) {
                Row { Icon(Icons.Default.Image, null); Spacer(Modifier.width(8.dp)); Text("Galleria") }
            }
        },
        dismissButton = {
            TextButton(onClick = onCameraSelect) {
                Row { Icon(Icons.Default.CameraAlt, null); Spacer(Modifier.width(8.dp)); Text("Fotocamera") }
            }
        }
    )
}
