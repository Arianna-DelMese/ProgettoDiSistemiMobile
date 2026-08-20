package com.example.progettodisistemimobile.screens.profilo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.data.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfiloScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    
    // Gestione sessione utente locale
    var currentSessionUsername by rememberSaveable { mutableStateOf("MarioRossi") }
    
    // Dati reattivi dal DB
    val utente by viewModel.getUtente(currentSessionUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    // Stati per Cambia Nome
    var newNameInput by remember { mutableStateOf("") }
    var isNameAvailable by remember { mutableStateOf(true) }
    
    // Stati per Cambia Immagine
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    // Preparazione sicura file per Fotocamera nella Cache
    val tempUri = remember {
        val file = File(context.cacheDir, "profile_pic_preview.jpg")
        if (file.exists()) file.delete()
        file.createNewFile()
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // Launcher per Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                showImagePreview = true
            }
        }
    )

    // Launcher per Fotocamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                selectedImageUri = tempUri
                showImagePreview = true
            }
        }
    )

    // Validazione disponibilità nome real-time
    LaunchedEffect(newNameInput) {
        if (newNameInput.isNotEmpty() && newNameInput != currentSessionUsername) {
            isNameAvailable = viewModel.isUsernameAvailable(newNameInput)
        } else {
            isNameAvailable = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        // --- INTESTAZIONE: FOTO E NOME ---
        ProfileHeader(utente = utente, sessionUsername = currentSessionUsername)

        // --- RIGA TOKEN ---
        TokenDisplay(tokens = utente?.token ?: 0)

        // --- FUNZIONE: CAMBIA NOME ---
        Text(text = "Impostazioni Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Cambia nome: ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            TextField(
                value = newNameInput,
                onValueChange = { newNameInput = it },
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
                onClick = {
                    viewModel.aggiornaProfilo(currentSessionUsername, newNameInput, null)
                    currentSessionUsername = newNameInput 
                    newNameInput = ""
                },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("Conferma")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- FUNZIONE: CAMBIA IMMAGINE ---
        if (!showImagePreview) {
            Button(
                onClick = { showImageSourceOptions = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambia immagine profilo")
            }
        } else {
            // Anteprima post-scelta
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Anteprima",
                    modifier = Modifier.size(160.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    TextButton(onClick = { showImageSourceOptions = true }) {
                        Text("Scegli un'altra")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = {
                        viewModel.aggiornaProfilo(currentSessionUsername, currentSessionUsername, selectedImageUri.toString())
                        showImagePreview = false
                        selectedImageUri = null
                    }) {
                        Text("Procedi")
                    }
                }
            }
        }

        if (showImageSourceOptions) {
            ImageSourceDialog(
                onDismiss = { showImageSourceOptions = false },
                onGallerySelect = {
                    showImageSourceOptions = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraSelect = {
                    showImageSourceOptions = false
                    cameraLauncher.launch(tempUri)
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- SEZIONE: TEMA ---
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
                    themes.forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme) }, 
                            onClick = { 
                                viewModel.updateTheme(theme)
                                expanded = false 
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
