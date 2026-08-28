package com.example.progettodisistemimobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.MainViewModel
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.progettodisistemimobile.screens.profilo.ImageSourceDialog
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreaLegaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLegaCreata: (Int) -> Unit,
    idLegaDaModificare: Int? = null   // null = creazione, altrimenti modifica
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var descrizione by rememberSaveable { mutableStateOf("") }
    var pubblica by rememberSaveable { mutableStateOf(true) }
    var salvataggioInCorso by rememberSaveable { mutableStateOf(false) }

    // IMMAGINE
    val context = LocalContext.current
    var immagineUri by remember { mutableStateOf<Uri?>(null) }
    var mostraDialogo by remember { mutableStateOf(false) }
    // Serve tenerlo fuori dal launcher: la fotocamera non ci restituisce l'uri, lo decidiamo noi prima
    var uriScatto by remember { mutableStateOf<Uri?>(null) }

    // CITTA'
    val scope = rememberCoroutineScope()
    var posizione by remember { mutableStateOf<PosizioneTrovata?>(null) }
    var cercandoPosizione by remember { mutableStateOf(false) }
    var posizioneNegata by remember { mutableStateOf(false) }
    var posizioneFallita by remember { mutableStateOf(false) }

    // MODIFICA: se sto modificando una lega esistente, precarico i suoi dati
    val inModifica = idLegaDaModificare != null

    val legaEsistente by (
            if (idLegaDaModificare != null) viewModel.getLega(idLegaDaModificare)
            else kotlinx.coroutines.flow.flowOf(null)
            ).collectAsState(initial = null)

    var campiPrecaricati by remember { mutableStateOf(false) }

    LaunchedEffect(legaEsistente) {
        val lega = legaEsistente
        if (lega != null && !campiPrecaricati) {
            nome = lega.nome_lega
            descrizione = lega.descrizione
            pubblica = lega.stato
            immagineUri = lega.immagine?.let { Uri.parse(it) }
            if (lega.latitudine != null && lega.longitudine != null) {
                val citta = cercaCitta(context, lega.latitudine, lega.longitudine)
                posizione = PosizioneTrovata(lega.latitudine, lega.longitudine, citta)
            }
            campiPrecaricati = true
        }
    }

    // Funzione locale: legge la posizione e aggiorna lo stato
    fun ottieniPosizione() {
        scope.launch {
            cercandoPosizione = true
            posizioneFallita = false
            val risultato = leggiPosizione(context)
            posizione = risultato
            posizioneFallita = risultato == null
            cercandoPosizione = false
        }
    }

    val permessoPosizioneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concesso ->
        if (concesso) ottieniPosizione() else posizioneNegata = true
    }

    val galleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) immagineUri = uri
    }

    val fotocameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { successo ->
        if (successo) immagineUri = uriScatto
    }

    val permessoCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concesso ->
        if (concesso) {
            val nuovoUri = creaUriTemporaneo(context)
            uriScatto = nuovoUri
            fotocameraLauncher.launch(nuovoUri)
        }
    }

    if (mostraDialogo) {
        ImageSourceDialog(
            onDismiss = { mostraDialogo = false },
            onGallerySelect = {
                mostraDialogo = false
                galleriaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCameraSelect = {
                mostraDialogo = false
                val giaConcesso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (giaConcesso) {
                    val nuovoUri = creaUriTemporaneo(context)
                    uriScatto = nuovoUri
                    fotocameraLauncher.launch(nuovoUri)
                } else {
                    permessoCameraLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }

    val nomeValido = nome.trim().length >= 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (inModifica) "Modifica lega" else "Crea una lega") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (immagineUri != null) {
                    AsyncImage(
                        model = immagineUri,
                        contentDescription = "Immagine della lega",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    TextButton(onClick = { mostraDialogo = true }) {
                        Text("Cambia immagine")
                    }
                } else {
                    OutlinedButton(
                        onClick = { mostraDialogo = true },
                        enabled = !salvataggioInCorso
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Scegli un'immagine")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome della lega") },
                supportingText = { Text("Almeno 3 caratteri") },
                singleLine = true,
                enabled = !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it },
                label = { Text("Descrizione") },
                minLines = 3,
                maxLines = 5,
                enabled = !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text("Stato", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !pubblica,
                    onClick = { pubblica = false },
                    enabled = !salvataggioInCorso,
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Privata")
                }
                SegmentedButton(
                    selected = pubblica,
                    onClick = { pubblica = true },
                    enabled = !salvataggioInCorso,
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Pubblica")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (pubblica) "Chiunque potrà trovare la lega e iscriversi."
                else "Solo chi ha l'invito potrà entrare.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // La posizione ha senso solo per le leghe pubbliche
            if (pubblica) {
                Spacer(Modifier.height(24.dp))

                Text("Città", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                when {
                    cercandoPosizione -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Sto cercando la posizione...")
                        }
                    }

                    posizione != null -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = posizione?.citta ?: "Posizione rilevata",
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { posizione = null }) {
                                Text("Rimuovi")
                            }
                        }
                    }

                    else -> {
                        OutlinedButton(
                            onClick = {
                                posizioneNegata = false
                                val giaConcesso = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (giaConcesso) {
                                    ottieniPosizione()
                                } else {
                                    permessoPosizioneLauncher.launch(
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                }
                            },
                            enabled = !salvataggioInCorso
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Usa la mia posizione")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = when {
                        posizioneNegata -> "Senza posizione la lega sarà comunque pubblica, ma non comparirà sulla mappa."
                        posizioneFallita -> "Non sono riuscita a rilevare la posizione. Controlla che il GPS sia attivo e riprova."
                        else -> "Serve per far trovare la tua lega a chi cerca per zona. È facoltativa."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (posizioneNegata || posizioneFallita) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    salvataggioInCorso = true
                    if (idLegaDaModificare != null) {
                        viewModel.modificaLega(
                            idLega = idLegaDaModificare,
                            nomeLega = nome,
                            descrizione = descrizione,
                            pubblica = pubblica,
                            immagine = immagineUri?.toString(),
                            latitudine = posizione?.latitudine,
                            longitudine = posizione?.longitudine,
                            onFatto = { onBack() }
                        )
                    } else {
                        viewModel.creaLegaConSquadra(
                            nomeLega = nome,
                            descrizione = descrizione,
                            pubblica = pubblica,
                            immagine = immagineUri?.toString(),
                            latitudine = if (pubblica) posizione?.latitudine else null,
                            longitudine = if (pubblica) posizione?.longitudine else null,
                            onFatto = { idLega -> onLegaCreata(idLega) }
                        )
                    }
                },
                enabled = nomeValido && !salvataggioInCorso,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (inModifica) "Salva modifiche" else "Conferma")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
/** Crea un file temporaneo dove la fotocamera scriverà lo scatto. */
private fun creaUriTemporaneo(context: Context): Uri {
    val file = File.createTempFile("lega_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "com.example.progettodisistemimobile.fileprovider",
        file
    )
}