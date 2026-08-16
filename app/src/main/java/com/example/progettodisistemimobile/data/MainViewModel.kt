package com.example.progettodisistemimobile.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()

    // --- DATI REATTIVI (FLOW) ---
    // Questi vengono osservati dalla UI
    val tuttiICantantiPerPunti: Flow<List<Cantante>> = dao.getCantantiPerPunti()
    val tuttiIBundle: Flow<List<Bundle>> = dao.getAllBundles()

    fun getTokens(username: String): Flow<Int> = dao.getTokenUtente(username)

    fun getLegheUtente(username: String): Flow<List<Lega>> = dao.getLeghePerUtente(username)
    
    fun getSquadra(idLega: Int, username: String): Flow<List<Cantante>> = 
        dao.getSquadraUtenteInLega(idLega, username)

    // --- OPERAZIONI (FUNZIONI SOSPESE) ---
    
    fun registraUtente(nome: String, email: String, pass: String) {
        viewModelScope.launch {
            val nuovoUtente = Utente(
                nome_utente = nome,
                email = email,
                password = pass,
                foto_profilo = null,
                dati_biomedici = null,
                dati_pagamento = null
            )
            dao.insertUtente(nuovoUtente)
        }
    }

    fun cambiaNomeUtente(vecchioNome: String, nuovoNome: String) {
        viewModelScope.launch {
            dao.updateNomeUtente(vecchioNome, nuovoNome)
        }
    }

    fun acquistaBundle(username: String, bundle: Bundle) {
        viewModelScope.launch {
            // Logica bonus primo acquisto
            val giaAcquistato = dao.getOffertaUtente(username, bundle.id_bundle)
            
            val tokenDaAggiungere = if (giaAcquistato == null) {
                // Primo acquisto: +20% bonus (esempio)
                (bundle.token * 1.2).toInt()
            } else {
                bundle.token
            }

            // 1. Aggiungiamo i token all'utente
            dao.aggiungiToken(username, tokenDaAggiungere)
            
            // 2. Segniamo l'offerta come riscossa
            if (giaAcquistato == null) {
                dao.registraAcquistoBundle(OffertaUtente(username, bundle.id_bundle, true))
            }
        }
    }

    fun aggiornaRuolo(idSquadra: Int, nomeCantante: String, nuovoRuolo: Int) {
        viewModelScope.launch {
            dao.updateRuoloCantante(idSquadra, nomeCantante, nuovoRuolo)
        }
    }
}
