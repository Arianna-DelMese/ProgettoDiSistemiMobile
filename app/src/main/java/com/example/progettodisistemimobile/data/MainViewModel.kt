package com.example.progettodisistemimobile.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()
    private val settingsManager = SettingsManager(application)

    // --- SESSIONE UTENTE GLOBALE ---
    // Inizializziamo con l'utente di test
    private val _currentUser = MutableStateFlow("MarioRossi")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    fun updateCurrentUser(newUsername: String) {
        _currentUser.value = newUsername
    }

    // --- STREAM DI DATI (FLOW) ---
    val tuttiICantantiPerPunti: Flow<List<Cantante>> = dao.getCantantiPerPunti()
    val tuttiIBundle: Flow<List<Bundle>> = dao.getAllBundles()
    val themeMode: Flow<String> = settingsManager.themeMode

    fun getTokens(username: String): Flow<Int> = dao.getTokenUtente(username)
    fun getUtente(username: String): Flow<Utente?> = dao.getUtenteFlow(username)
    fun getLegheUtente(username: String): Flow<List<Lega>> = dao.getLeghePerUtente(username)
    fun getClassificaLega(idLega: Int): Flow<List<UtenteInLega>> = dao.getClassificaLega(idLega)
    fun getSquadra(idLega: Int, username: String): Flow<List<Cantante>> = dao.getSquadraUtenteInLega(idLega, username)

    // --- OPERAZIONI UTENTE ---
    fun registraUtente(nome: String, email: String, pass: String) {
        viewModelScope.launch {
            dao.insertUtente(Utente(nome, email, pass, null, 150, null, null))
        }
    }

    fun aggiornaProfilo(vecchioNome: String, nuovoNome: String, nuovaFoto: String?) {
        viewModelScope.launch {
            if (vecchioNome != nuovoNome) {
                dao.updateNomeUtente(vecchioNome, nuovoNome)
                // Fondamentale: aggiorniamo la sessione globale per sincronizzare tutte le schermate
                updateCurrentUser(nuovoNome)
            }
            nuovaFoto?.let { dao.updateFotoProfilo(nuovoNome, it) }
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        // Verifica se il nome esiste già nel DB
        return !dao.utenteEsiste(username)
    }

    fun updateTheme(mode: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    // --- LOGICA ACQUISTO BUNDLE ---
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
