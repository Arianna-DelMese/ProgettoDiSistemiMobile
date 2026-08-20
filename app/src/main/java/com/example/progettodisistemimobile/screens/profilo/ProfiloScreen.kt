package com.example.progettodisistemimobile.screens.profilo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.MainViewModel
import java.io.File

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
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    // Preparazione sicura file per Fotocamera nella Cache
    val tempUri = remember {
        val file = File(context.cacheDir, "profile_pic_preview.jpg")
        if (file.exists()) file.delete()
        file.createNewFile()
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) { selectedImageUri = uri; showImagePreview = true } }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) { selectedImageUri = tempUri; showImagePreview = true } }
    )

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
        // --- SEZIONE DATI UTENTE (Unita) ---
        ProfileHeader(utente = utente, sessionUsername = currentSessionUsername)
        TokenDisplay(tokens = utente?.token ?: 0)

        Spacer(modifier = Modifier.height(16.dp))

        // --- SEZIONE NOME ---
        ChangeNameSection(
            newNameInput = newNameInput,
            onNameChange = { newNameInput = it },
            isNameAvailable = isNameAvailable,
            currentSessionUsername = currentSessionUsername,
            onConfirm = {
                viewModel.aggiornaProfilo(currentSessionUsername, newNameInput, null)
                currentSessionUsername = newNameInput
                newNameInput = ""
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- SEZIONE IMMAGINE (Unita) ---
        ChangeImageSection(
            showImagePreview = showImagePreview,
            selectedImageUri = selectedImageUri,
            onChooseSource = { showImageSourceOptions = true },
            onProceed = {
                viewModel.aggiornaProfilo(currentSessionUsername, currentSessionUsername, selectedImageUri.toString())
                showImagePreview = false
                selectedImageUri = null
            }
        )

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

        // --- SEZIONE TEMA ---
        ThemeSelectionSection(
            themeMode = themeMode,
            onThemeChange = { viewModel.updateTheme(it) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}