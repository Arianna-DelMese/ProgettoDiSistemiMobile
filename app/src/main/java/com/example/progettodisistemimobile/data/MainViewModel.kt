package com.example.progettodisistemimobile.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()
    private val settingsManager = SettingsManager(application)

    // --- SESSIONE UTENTE ---
    // Osserva il DataStore: si aggiorna da solo quando l'utente fa login/logout
    val currentUser: StateFlow<String> = settingsManager.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    fun updateCurrentUser(newUsername: String) {
        viewModelScope.launch {
            settingsManager.setCurrentUser(newUsername)
        }
    }

    // --- STREAM DI DATI ---
    val tuttiICantantiPerPunti = dao.getCantantiPerPunti()
    val tuttiIBundle = dao.getAllBundles()
    val themeMode = settingsManager.themeMode

    fun getTokens(username: String) = dao.getTokenUtente(username)
    fun getUtente(username: String) = dao.getUtenteFlow(username)
    fun getLegheUtente(username: String) = dao.getLeghePerUtente(username)
    fun getLega(idLega: Int) = dao.getLegaById(idLega)
    fun getDatiPartecipazione(idLega: Int, username: String) = dao.getUtenteInLega(idLega, username)
    fun getClassificaLegaConCapitano(idLega: Int) = dao.getClassificaLegaConCapitano(idLega)
    fun getSquadra(idLega: Int, username: String) = dao.getSquadraUtenteInLega(idLega, username)

    // --- OPERAZIONI UTENTE ---
    fun aggiornaProfilo(vecchioNome: String, nuovoNome: String, nuovaFoto: String?) {
        viewModelScope.launch {
            if (vecchioNome != nuovoNome) {
                dao.updateNomeUtente(vecchioNome, nuovoNome)
                updateCurrentUser(nuovoNome)
            }
            nuovaFoto?.let { photoUri ->
                try {
                    val uri = Uri.parse(photoUri)
                    if (photoUri.startsWith("content://")) {
                        getApplication<Application>().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dao.updateFotoProfilo(nuovoNome, photoUri)
            }
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return !dao.utenteEsiste(username)
    }

    fun updateTheme(mode: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    fun aggiornaRuoloCantante(idLega: Int, username: String, nomeCantante: String, nuovoRuolo: Int) {
        viewModelScope.launch {
            dao.updateRuoloCantante(idLega, username, nomeCantante, nuovoRuolo)
        }
    }

    fun acquistaBundle(username: String, bundle: Bundle) {
        viewModelScope.launch {
            val giaAcquistato = dao.getOffertaUtente(username, bundle.id_bundle)
            val bonus = if (giaAcquistato == null) 1.2 else 1.0
            dao.aggiungiToken(username, (bundle.token * bonus).toInt())
            if (giaAcquistato == null) {
                dao.registraAcquistoBundle(OffertaUtente(username, bundle.id_bundle, true))
            }
        }
    }
}
