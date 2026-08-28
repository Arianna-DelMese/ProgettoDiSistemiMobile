package com.example.progettodisistemimobile.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.ceil
import androidx.core.net.toUri

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()
    private val settingsManager = SettingsManager(application)

    // --- SESSIONE UTENTE PERSISTENTE ---
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

    val purchasedBundleIds: StateFlow<List<Int>> = currentUser
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else dao.getPurchasedBundleIds(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // --- OPERAZIONI PROFILO ---
    fun aggiornaProfilo(vecchioNome: String, nuovoNome: String, nuovaFoto: String?) {
        viewModelScope.launch {
            if (vecchioNome != nuovoNome) {
                dao.updateNomeUtente(vecchioNome, nuovoNome)
                updateCurrentUser(nuovoNome)
            }
            nuovaFoto?.let { photoUri ->
                try {
                    val uri = photoUri.toUri()
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

    suspend fun isUsernameAvailable(username: String): Boolean = !dao.utenteEsiste(username)

    fun updateTheme(mode: String) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
    }

    // --- LOGICA MODIFICA FORMAZIONE ---
    fun scambiaRuoli(idLega: Int, username: String, c1: Cantante, r1: Int, c2: Cantante, r2: Int) {
        viewModelScope.launch {
            dao.updateRuoloCantante(idLega, username, c1.nome_cantante, r2)
            dao.updateRuoloCantante(idLega, username, c2.nome_cantante, r1)
        }
    }

    fun impostaCapitano(idLega: Int, username: String, nuovoCapitano: Cantante, squadra: List<Cantante>) {
        viewModelScope.launch {
            val vecchioCapitano = squadra.getOrNull(0)
            if (vecchioCapitano != null && vecchioCapitano.nome_cantante != nuovoCapitano.nome_cantante) {
                val currentIndex = squadra.indexOf(nuovoCapitano)
                if (currentIndex != -1) {
                    dao.updateRuoloCantante(idLega, username, vecchioCapitano.nome_cantante, currentIndex)
                    dao.updateRuoloCantante(idLega, username, nuovoCapitano.nome_cantante, 0)
                }
            }
        }
    }

    // --- LOGICA SHOP ---
    fun acquistaBundle(username: String, bundle: Bundle) {
        viewModelScope.launch {
            val giaAcquistato = dao.getOffertaUtente(username, bundle.id_bundle)
            val bonusPercentuale = if (giaAcquistato == null) 1.25 else 1.0
            val tokenTotali = ceil(bundle.token * bonusPercentuale).toInt()

            dao.aggiungiToken(username, tokenTotali)
            if (giaAcquistato == null) {
                dao.registraAcquistoBundle(OffertaUtente(username, bundle.id_bundle))
            }
        }
    }

    // --- LOGICA SELEZIONE NUOVA SQUADRA ---
    private val _cantantiSelezionati = MutableStateFlow<List<String>>(emptyList())
    val cantantiSelezionati: StateFlow<List<String>> = _cantantiSelezionati.asStateFlow()

    private val _capitano = MutableStateFlow<String?>(null)
    val capitano: StateFlow<String?> = _capitano.asStateFlow()

    companion object {
        const val CANTANTI_PER_SQUADRA = 7
    }

    fun toggleCantante(nomeCantante: String) {
        val attuali = _cantantiSelezionati.value
        if (nomeCantante in attuali) {
            _cantantiSelezionati.value = attuali - nomeCantante
            if (_capitano.value == nomeCantante) _capitano.value = null
        } else {
            if (attuali.size < CANTANTI_PER_SQUADRA) {
                _cantantiSelezionati.value = attuali + nomeCantante
            }
        }
    }

    fun impostaCapitanoSelezione(nomeCantante: String) {
        if (nomeCantante in _cantantiSelezionati.value) {
            _capitano.value = nomeCantante
        }
    }

    fun resetSelezione() {
        _cantantiSelezionati.value = emptyList()
        _capitano.value = null
    }

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

        if (username.isBlank() || selezione.size != CANTANTI_PER_SQUADRA || nomeCapitano == null) return

        viewModelScope.launch {
            val idLega = dao.insertLega(
                Lega(0, nomeLega.trim(), immagine, descrizione.trim(), pubblica, latitudine, longitudine)
            ).toInt()

            dao.joinLega(UtenteInLega(username, idLega, true, 0))
            
            // Inserimento capitano (ruolo 0)
            dao.insertComposizione(ComposizioneSquadra(username, idLega, nomeCapitano, 0))

            // Inserimento altri (ruoli 1-6)
            selezione.filter { it != nomeCapitano }.forEachIndexed { i, nome ->
                dao.insertComposizione(ComposizioneSquadra(username, idLega, nome, i + 1))
            }

            val costo = dao.getPrezzoTotale(selezione)
            dao.aggiungiToken(username, -costo)

            // 5. Calcolo il punteggio iniziale della squadra
            val punti = dao.calcolaPuntiSquadra(idLega, username)
            dao.aggiornaPunti(idLega, username, punti)

            resetSelezione()
            onFatto(idLega)
        }
    }

    fun cercaLeghe(filtro: String) = dao.cercaLeghePubbliche(filtro, currentUser.value)

    /**
     * Iscrive l'utente a una lega esistente e vi salva la squadra selezionata.
     * Stessa logica di creaLegaConSquadra, ma senza creare la lega.
     */
    fun uniscitiALegaConSquadra(idLega: Int, onFatto: () -> Unit) {
        val username = currentUser.value
        val selezione = _cantantiSelezionati.value
        val nomeCapitano = _capitano.value

        if (username.isBlank() || selezione.size != CANTANTI_PER_SQUADRA || nomeCapitano == null) return

        viewModelScope.launch {
            // Chi entra dopo non è il creatore, quindi stato = false
            dao.joinLega(UtenteInLega(username, idLega, false, 0))

            dao.insertComposizione(ComposizioneSquadra(username, idLega, nomeCapitano, 0))
            selezione.filter { it != nomeCapitano }.forEachIndexed { indice, nome ->
                dao.insertComposizione(ComposizioneSquadra(username, idLega, nome, indice + 1))
            }

            val costo = dao.getPrezzoTotale(selezione)
            dao.aggiungiToken(username, -costo)

            resetSelezione()
            // Calcolo subito il punteggio della squadra appena creata
            val punti = dao.calcolaPuntiSquadra(idLega, username)
            dao.aggiornaPunti(idLega, username, punti)

            resetSelezione()
            onFatto()
            onFatto()
        }
    }
    fun modificaLega(
        idLega: Int,
        nomeLega: String,
        descrizione: String,
        pubblica: Boolean,
        immagine: String?,
        latitudine: Double?,
        longitudine: Double?,
        onFatto: () -> Unit
    ) {
        if (nomeLega.trim().length < 3) return

        viewModelScope.launch {
            dao.aggiornaLega(
                idLega = idLega,
                nome = nomeLega.trim(),
                descrizione = descrizione.trim(),
                pubblica = pubblica,
                immagine = immagine,
                // Se torna privata, le coordinate non servono più
                latitudine = if (pubblica) latitudine else null,
                longitudine = if (pubblica) longitudine else null
            )
            onFatto()
        }
    }

}
