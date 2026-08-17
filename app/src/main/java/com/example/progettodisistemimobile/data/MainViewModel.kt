package com.example.progettodisistemimobile.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()

    // --- STREAM DI DATI (FLOW) ---
    val tuttiICantantiPerPunti: Flow<List<Cantante>> = dao.getCantantiPerPunti()
    val tuttiIBundle: Flow<List<Bundle>> = dao.getAllBundles()
    
    fun getTokens(username: String): Flow<Int> = dao.getTokenUtente(username)
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
            if (vecchioNome != nuovoNome) dao.updateNomeUtente(vecchioNome, nuovoNome)
            nuovaFoto?.let { dao.updateFotoProfilo(nuovoNome, it) }
        }
    }

    // --- OPERAZIONI LEGA E SQUADRA ---
    fun creaLega(nome: String, desc: String, username: String) {
        viewModelScope.launch {
            val id = dao.insertLega(Lega(0, nome, null, desc, true, null, null))
            dao.joinLega(UtenteInLega(0, username, id.toInt(), true, 0))
        }
    }

    fun aggiornaRuoloCantante(idSquadra: Int, nomeCantante: String, nuovoRuolo: Int) {
        viewModelScope.launch {
            dao.updateRuoloCantante(idSquadra, nomeCantante, nuovoRuolo)
        }
    }

    // --- LOGICA ACQUISTO BUNDLE ---
    fun acquistaBundle(username: String, bundle: Bundle) {
        viewModelScope.launch {
            val giaAcquistato = dao.getOffertaUtente(username, bundle.id_bundle)
            val bonus = if (giaAcquistato == null) 1.2 else 1.0 // +20% se è la prima volta
            
            dao.aggiungiToken(username, (bundle.token * bonus).toInt())
            if (giaAcquistato == null) {
                dao.registraAcquistoBundle(OffertaUtente(username, bundle.id_bundle, true))
            }
        }
    }
}
