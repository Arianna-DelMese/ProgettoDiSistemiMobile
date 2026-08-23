package com.example.progettodisistemimobile.screens.profilo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val currentUsername by viewModel.currentUser.collectAsState()
    val utente by viewModel.getUtente(currentUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    var newNameInput by remember { mutableStateOf("") }
    var isNameAvailable by remember { mutableStateOf(true) }

    // Saver per gestire Uri con rememberSaveable
    val uriSaver = Saver<Uri?, String>(
        save = { it?.toString() ?: "" },
        restore = { if (it.isEmpty()) null else Uri.parse(it) }
    )

    var selectedImageUri by rememberSaveable(stateSaver = uriSaver) { mutableStateOf(null) }
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    // Launcher Fotocamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) showImagePreview = true }
    )

    // Launcher Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                showImagePreview = true
            }
        }
    )

    fun prepareCameraUri(): Uri? {
        return try {
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, "profile_pic_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(
                context,
                "com.example.progettodisistemimobile.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

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
                    val uri = prepareCameraUri()
                    if (uri != null) {
                        selectedImageUri = uri
                        cameraLauncher.launch(uri)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        ThemeSelectionSection(
            themeMode = themeMode,
            onThemeChange = { viewModel.updateTheme(it) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}