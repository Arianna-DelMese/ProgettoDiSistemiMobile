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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.AuthViewModel
import com.example.progettodisistemimobile.data.MainViewModel
import java.io.File

@Composable
fun ProfiloScreen(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // SORGENTE UNICA PERSISTENTE: leggiamo l'utente correntemente loggato dal ViewModel
    val currentUsername by viewModel.currentUser.collectAsState()
    
    val utente by viewModel.getUtente(currentUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    // Stati UI per le modifiche
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

    // Funzione sicura per generare l'URI della fotocamera
    fun getTmpUri(): Uri? {
        return try {
            val tempFile = File(context.cacheDir, "profile_pic_preview.jpg")
            if (tempFile.exists()) tempFile.delete()
            tempFile.createNewFile()
            FileProvider.getUriForFile(context, "com.example.progettodisistemimobile.fileprovider", tempFile)
        } catch (e: Exception) { null }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) { selectedImageUri = uri; showImagePreview = true } }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) showImagePreview = true }
    )

    // Validazione disponibilità nome
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
        // --- VISUALIZZAZIONE DATI (UserInfoSection.kt) ---
        UserInfoSection(utente = utente, sessionUsername = currentUsername)

        Spacer(modifier = Modifier.height(16.dp))

        // --- INTESTAZIONE IMPOSTAZIONI E LOGOUT ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Impostazioni Account",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            TextButton(
                onClick = { authViewModel.logout() },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // --- CAMBIA NOME (ChangeNameSection.kt) ---
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

        Spacer(modifier = Modifier.height(32.dp))

        // --- CAMBIA IMMAGINE (ChangeImageSection.kt) ---
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

        if (showImageSourceOptions) {
            ImageSourceDialog(
                onDismiss = { showImageSourceOptions = false },
                onGallerySelect = {
                    showImageSourceOptions = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraSelect = {
                    showImageSourceOptions = false
                    val uri = getTmpUri()
                    if (uri != null) {
                        selectedImageUri = uri
                        cameraLauncher.launch(uri)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- TEMA (ThemeSelectionSection.kt) ---
        ThemeSelectionSection(
            themeMode = themeMode,
            onThemeChange = { viewModel.updateTheme(it) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}
