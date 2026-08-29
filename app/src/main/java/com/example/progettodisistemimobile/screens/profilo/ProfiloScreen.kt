package com.example.progettodisistemimobile.screens.profilo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.AuthViewModel
import com.example.progettodisistemimobile.data.MainViewModel
import java.io.File
import androidx.core.net.toUri

@Composable
fun ProfiloScreen(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUsername by viewModel.currentUser.collectAsState()

    val utente by viewModel.getUtente(currentUsername).collectAsState(initial = null)
    val themeMode by viewModel.themeMode.collectAsState(initial = "Sistema")

    var newNameInput by rememberSaveable { mutableStateOf("") }
    var isNameAvailable by remember { mutableStateOf(true) }

    val uriSaver = Saver<Uri?, String>(
        save = { it?.toString() ?: "" },
        restore = { if (it.isEmpty()) null else it.toUri() }
    )

    var selectedImageUri by rememberSaveable(stateSaver = uriSaver) { mutableStateOf(null) }
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var showImageSourceOptions by rememberSaveable { mutableStateOf(false) }
    var uriScatto by rememberSaveable(stateSaver = uriSaver) { mutableStateOf(null) }

    // Launcher
    val galleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            showImagePreview = true
        }
    }

    val fotocameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { successo ->
        if (successo) {
            // Qui recuperiamo l'URI che abbiamo salvato PRIMA di lanciare la fotocamera
            selectedImageUri = uriScatto
            showImagePreview = true
        }
    }

    val permessoCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concesso ->
        if (concesso) {
            val nuovoUri = creaUriTemporaneoProfilo(context)
            uriScatto = nuovoUri
            fotocameraLauncher.launch(nuovoUri)
        }
    }

    fun avviaFotocamera() {
        val giaConcesso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (giaConcesso) {
            val nuovoUri = creaUriTemporaneoProfilo(context)
            uriScatto = nuovoUri // Lo salviamo nello stato persistente
            fotocameraLauncher.launch(nuovoUri)
        } else {
            permessoCameraLauncher.launch(Manifest.permission.CAMERA)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Impostazioni Account",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            // Logout senza bordi
            TextButton(
                onClick = { authViewModel.logout() },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
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
                    galleriaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onCameraSelect = {
                    showImageSourceOptions = false
                    avviaFotocamera()
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

private fun creaUriTemporaneoProfilo(context: Context): Uri {
    val file = File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "com.example.progettodisistemimobile.fileprovider",
        file
    )
}