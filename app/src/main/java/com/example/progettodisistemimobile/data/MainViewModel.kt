package com.example.progettodisistemimobile.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()
    private val settingsManager = SettingsManager(application)

    private val _currentUser = MutableStateFlow("MarioRossi")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            val savedUser = settingsManager.currentUser.first()
            if (savedUser.isNotEmpty()) _currentUser.value = savedUser
        }
    }

    fun updateCurrentUser(newUsername: String) {
        viewModelScope.launch {
            _currentUser.value = newUsername
            settingsManager.setCurrentUser(newUsername)
        }
    }

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

    fun aggiornaProfilo(vecchioNome: String, nuovoNome: String, nuovaFoto: String?) {
        viewModelScope.launch {
            if (vecchioNome != nuovoNome) {
                dao.updateNomeUtente(vecchioNome, nuovoNome)
                updateCurrentUser(nuovoNome)
            }
            nuovaFoto?.let { photoUri ->
                try {
                    val uri = Uri.parse(photoUri)
                    if (photoUri.startsWith("content://") && !photoUri.contains(getApplication<Application>().packageName)) {
                        getApplication<Application>().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                } catch (e: Exception) { e.printStackTrace() }
                dao.updateFotoProfilo(nuovoNome, photoUri)
            }
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return !dao.utenteEsiste(username)
    }

    fun updateTheme(mode: String) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
    }

    // --- LOGICA FORMAZIONE ---

    fun scambiaRuoli(idLega: Int, username: String, c1: Cantante, r1: Int, c2: Cantante, r2: Int) {
        viewModelScope.launch {
            dao.updateRuoloCantante(idLega, username, c1.nome_cantante, r2)
            dao.updateRuoloCantante(idLega, username, c2.nome_cantante, r1)
        }
    }

    fun impostaCapitano(idLega: Int, username: String, nuovoCapitano: Cantante, squadra: List<Cantante>) {
        viewModelScope.launch {
            // Cerchiamo l'attuale capitano (ruolo 0)
            val vecchioCapitano = squadra.getOrNull(0)
            if (vecchioCapitano != null && vecchioCapitano.nome_cantante != nuovoCapitano.nome_cantante) {
                // Troviamo il ruolo attuale del nuovo capitano
                val currentIndex = squadra.indexOf(nuovoCapitano)
                if (currentIndex != -1) {
                    dao.updateRuoloCantante(idLega, username, vecchioCapitano.nome_cantante, currentIndex)
                    dao.updateRuoloCantante(idLega, username, nuovoCapitano.nome_cantante, 0)
                }
            }
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
