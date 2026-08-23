package com.example.progettodisistemimobile.screens.profilo

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

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
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                TextButton(onClick = onChooseSource) {
                    Text("Scegli un'altra")
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
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGallerySelect: () -> Unit,
    onCameraSelect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Origine Immagine") },
        text = { Text("Vuoi scattare una foto o usarne una esistente?") },
        confirmButton = {
            TextButton(onClick = onGallerySelect) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Galleria")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCameraSelect) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Fotocamera")
                }
            }
        }
    )
}