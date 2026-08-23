package com.example.progettodisistemimobile.screens.profilo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.progettodisistemimobile.data.MainViewModel
import java.io.File

@Composable
fun ProfiloScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // SORGENTE UNICA: l'utente loggato viene dal ViewModel
    val currentUsername by viewModel.currentUser.collectAsState()

    val utente by viewModel.getUtente(currentUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    // Stati UI locali
    var newNameInput by remember { mutableStateOf("") }
    var isNameAvailable by remember { mutableStateOf(true) }

    // Saver per l'URI (persistenza alla rotazione/cambio activity)
    val uriSaver = Saver<Uri?, String>(
        save = { it?.toString() ?: "" },
        restore = { if (it.isEmpty()) null else Uri.parse(it) }
    )
    var selectedImageUri by rememberSaveable(stateSaver = uriSaver) { mutableStateOf(null) }
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    // Launcher per la Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) { 
                selectedImageUri = uri
                showImagePreview = true 
            } 
        }
    )

    // Launcher per la Fotocamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> 
            if (success) {
                showImagePreview = true 
            }
        }
    )

    // Funzione per generare l'URI e lanciarlo immediatamente
    fun launchCameraFlow() {
        try {
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) directory.mkdirs()
            
            val file = File(directory, "profile_pic_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context, 
                "com.example.progettodisistemimobile.fileprovider", 
                file
            )
            
            selectedImageUri = uri // Salviamo per l'anteprima successiva
            cameraLauncher.launch(uri) // Lanciamo subito l'intent
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Validazione nome
    LaunchedEffect(newNameInput) {
        if (newNameInput.isNotEmpty() && newNameInput != currentUsername) {
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
        // --- VISUALIZZAZIONE DATI UTENTE ---
        UserInfoSection(utente = utente, sessionUsername = currentUsername)

        Spacer(modifier = Modifier.height(16.dp))

        // --- SEZIONE CAMBIA NOME ---
        ChangeNameSection(
            newNameInput = newNameInput,
            onNameChange = { newNameInput = it },
            isNameAvailable = isNameAvailable,
            currentSessionUsername = currentUsername,
            onConfirm = {
                viewModel.aggiornaProfilo(currentUsername, newNameInput, null)
                newNameInput = ""
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- SEZIONE CAMBIA IMMAGINE ---
        ChangeImageSection(
            showImagePreview = showImagePreview,
            selectedImageUri = selectedImageUri,
            onChooseSource = { showImageSourceOptions = true },
            onProceed = {
                viewModel.aggiornaProfilo(currentUsername, currentUsername, selectedImageUri.toString())
                showImagePreview = false
                selectedImageUri = null
            }
        )

        // Popup scelta Galleria o Fotocamera
        if (showImageSourceOptions) {
            ImageSourceDialog(
                onDismiss = { showImageSourceOptions = false },
                onGallerySelect = {
                    showImageSourceOptions = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraSelect = {
                    showImageSourceOptions = false
                    launchCameraFlow()
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- SEZIONE TEMA ---
        ThemeSelectionSection(
            themeMode = themeMode,
            onThemeChange = { viewModel.updateTheme(it) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}
