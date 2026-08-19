package com.example.progettodisistemimobile.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
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
    
    // Gestione sessione: MarioRossi è l'utente iniziale
    var currentSessionUsername by rememberSaveable { mutableStateOf("MarioRossi") }
    
    // Dati reattivi dal database
    val utente by viewModel.getUtente(currentSessionUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    // Stati per "Cambia Nome"
    var newNameInput by remember { mutableStateOf("") }
    var isNameAvailable by remember { mutableStateOf(true) }
    
    // Stati per "Cambia Immagine"
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    // Funzione helper per ottenere il file temporaneo per la fotocamera
    fun getTempUri(): Uri {
        val tempFile = File(context.cacheDir, "profile_pic_tmp.jpg")
        if (tempFile.exists()) tempFile.delete()
        tempFile.createNewFile()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    // Launcher: Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                showImagePreview = true
            }
        }
    )

    // Launcher: Fotocamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // selectedImageUri è già stato impostato al momento del lancio
                showImagePreview = true
            }
        }
    )

    // Validazione nome mentre l'utente scrive
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
        // --- TESTATA: FOTO E NOME ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            if (utente?.foto_profilo != null) {
                AsyncImage(
                    model = utente?.foto_profilo,
                    contentDescription = "Foto profilo",
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

            // Nome persistente
            Text(
                text = utente?.nome_utente ?: currentSessionUsername,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- TOKEN ---
        Row(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(text = "I miei token: ", fontSize = 18.sp, color = Color.Gray)
            Text(
                text = "${utente?.token ?: 0}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

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
                placeholder = { Text("Nuovo nome") },
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
            // Anteprima immagine
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

        // Dialog per scelta sorgente
        if (showImageSourceOptions) {
            AlertDialog(
                onDismissRequest = { showImageSourceOptions = false },
                title = { Text("Seleziona Sorgente") },
                text = { Text("Vuoi scattare una nuova foto o caricarne una dalla galleria?") },
                confirmButton = {
                    TextButton(onClick = {
                        showImageSourceOptions = false
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Row { Icon(Icons.Default.Image, null); Spacer(Modifier.width(8.dp)); Text("Galleria") }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showImageSourceOptions = false
                        val uri = getTempUri()
                        selectedImageUri = uri // Prepariamo l'URI per il launcher
                        cameraLauncher.launch(uri)
                    }) {
                        Row { Icon(Icons.Default.CameraAlt, null); Spacer(Modifier.width(8.dp)); Text("Fotocamera") }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SEZIONE: TEMA ---
        var expanded by remember { mutableStateOf(false) }
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
                    listOf("Chiaro", "Scuro", "Sistema").forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) }, 
                            onClick = { 
                                viewModel.updateTheme(t)
                                expanded = false 
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
