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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    // --- NUOVA SQUADRA ---

    // Nomi dei cantanti selezionati, nell'ordine in cui li ho scelti
    private val _cantantiSelezionati = MutableStateFlow<List<String>>(emptyList())
    val cantantiSelezionati: StateFlow<List<String>> = _cantantiSelezionati.asStateFlow()

    // Nome del cantante scelto come capitano (null se non ancora scelto)
    private val _capitano = MutableStateFlow<String?>(null)
    val capitano: StateFlow<String?> = _capitano.asStateFlow()

    companion object {
        const val CANTANTI_PER_SQUADRA = 7
    }

    /** Aggiunge o toglie un cantante dalla selezione. */
    fun toggleCantante(nomeCantante: String) {
        val attuali = _cantantiSelezionati.value

        if (nomeCantante in attuali) {
            _cantantiSelezionati.value = attuali - nomeCantante
            // Se ho tolto proprio il capitano, resta senza
            if (_capitano.value == nomeCantante) {
                _capitano.value = null
            }
        } else {
            if (attuali.size >= CANTANTI_PER_SQUADRA) return
            _cantantiSelezionati.value = attuali + nomeCantante
        }
    }

    /** Imposta il capitano. Funziona solo su un cantante già selezionato. */
    fun impostaCapitanoSelezione(nomeCantante: String) {
        if (nomeCantante in _cantantiSelezionati.value) {
            _capitano.value = nomeCantante
        }
    }

    /** Svuota la selezione, da chiamare dopo aver salvato la squadra. */
    fun resetSelezione() {
        _cantantiSelezionati.value = emptyList()
        _capitano.value = null
    }
    /**
     * Crea una nuova lega, iscrive l'utente come capitano e salva la squadra selezionata.
     * I ruoli seguono lo schema del DB: 0 = capitano, 1-4 = titolari, 5-6 = riserve.
     * onFatto viene chiamata a salvataggio concluso, con l'id della lega appena creata.
     */
    fun creaLegaConSquadra(
        nomeLega: String,
        descrizione: String,
        pubblica: Boolean,
        immagine: String?,
        latitudine: Double?,
        longitudine: Double?,
        onFatto: (Int) -> Unit
    ) {
        val username = currentUser.value
        val selezione = _cantantiSelezionati.value
        val nomeCapitano = _capitano.value

        // Guardia: non salvo squadre incomplete
        if (username.isBlank() || selezione.size != CANTANTI_PER_SQUADRA || nomeCapitano == null) return

        viewModelScope.launch {
            // 1. Creo la lega e mi faccio restituire l'id generato da Room
            val idLega = dao.insertLega(
                Lega(
                    id_lega = 0, // 0 = autogenerato
                    nome_lega = nomeLega.trim(),
                    immagine = immagine,
                    descrizione = descrizione.trim(),
                    stato = pubblica,
                    latitudine = latitudine,
                    longitudine = longitudine
                )
            ).toInt()

            // 2. Mi iscrivo alla lega come creatrice (stato = true)
            dao.joinLega(
                UtenteInLega(
                    nome_utente = username,
                    id_lega = idLega,
                    stato = true,
                    punti = 0
                )
            )

            // 3. Salvo la squadra: prima il capitano (ruolo 0), poi gli altri sei
            dao.insertComposizione(
                ComposizioneSquadra(username, idLega, nomeCapitano, 0)
            )

            val altri = selezione.filter { it != nomeCapitano }
            altri.forEachIndexed { indice, nomeCantante ->
                // indice 0..5 diventa ruolo 1..6: titolari 1-4, riserve 5-6
                dao.insertComposizione(
                    ComposizioneSquadra(username, idLega, nomeCantante, indice + 1)
                )
            }

            // 4. Scalo i token spesi
            val costo = dao.getPrezzoTotale(selezione)
            dao.aggiungiToken(username, -costo)

            resetSelezione()
            onFatto(idLega)
        }
    }
}

