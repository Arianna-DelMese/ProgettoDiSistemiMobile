package com.example.progettodisistemimobile.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- UTENTE ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUtente(utente: Utente)

    @Query("SELECT * FROM utente WHERE nome_utente = :username")
    suspend fun getUtenteByUsername(username: String): Utente?

    @Query("SELECT * FROM utente WHERE nome_utente = :username")
    fun getUtenteFlow(username: String): Flow<Utente?>

    @Query("SELECT token FROM utente WHERE nome_utente = :username")
    fun getTokenUtente(username: String): Flow<Int>

    @Query("UPDATE utente SET nome_utente = :nuovoNome WHERE nome_utente = :vecchioNome")
    suspend fun updateNomeUtente(vecchioNome: String, nuovoNome: String)

    @Query("UPDATE utente SET foto_profilo = :nuovaFoto WHERE nome_utente = :username")
    suspend fun updateFotoProfilo(username: String, nuovaFoto: String)

    @Query("SELECT COUNT(*) FROM utente WHERE nome_utente = :username")
    suspend fun countUtentiByUsername(username: String): Int

    // --- CANTANTI E CLASSIFICHE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCantante(cantante: Cantante)

    @Query("SELECT * FROM cantante ORDER BY punti DESC")
    fun getCantantiPerPunti(): Flow<List<Cantante>>

    @Query("SELECT * FROM cantante ORDER BY pos_serata_1 ASC")
    fun getClassificaSerata1(): Flow<List<Cantante>>

    @Query("SELECT * FROM cantante ORDER BY pos_serata_2 ASC")
    fun getClassificaSerata2(): Flow<List<Cantante>>

    @Query("SELECT * FROM cantante ORDER BY pos_serata_3 ASC")
    fun getClassificaSerata3(): Flow<List<Cantante>>

    @Query("SELECT * FROM cantante ORDER BY pos_serata_4 ASC")
    fun getClassificaSerata4(): Flow<List<Cantante>>

    @Query("SELECT * FROM cantante ORDER BY pos_serata_5 ASC")
    fun getClassificaSerata5(): Flow<List<Cantante>>

    // --- LEGA E UTENTI IN LEGA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLega(lega: Lega): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun joinLega(utenteInLega: UtenteInLega): Long

    @Query("""
        SELECT * FROM lega 
        INNER JOIN utente_in_lega ON lega.id_lega = utente_in_lega.id_lega 
        WHERE utente_in_lega.nome_utente = :username
    """)
    fun getLeghePerUtente(username: String): Flow<List<Lega>>

    @Query("SELECT * FROM utente_in_lega WHERE id_lega = :idLega ORDER BY punti DESC")
    fun getClassificaLega(idLega: Int): Flow<List<UtenteInLega>>

    @Query("SELECT * FROM utente_in_lega WHERE id_lega = :idLega AND stato = 1 LIMIT 1")
    suspend fun getCreatoreLega(idLega: Int): UtenteInLega?

    // --- SQUADRA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComposizione(composizione: ComposizioneSquadra)

    @Query("""
        SELECT cantante.* FROM cantante 
        INNER JOIN composizione_squadra ON cantante.nome_cantante = composizione_squadra.nome_cantante 
        INNER JOIN utente_in_lega ON composizione_squadra.id_squadra = utente_in_lega.id_squadra 
        WHERE utente_in_lega.id_lega = :idLega AND utente_in_lega.nome_utente = :username
        ORDER BY composizione_squadra.ruolo ASC
    """)
    fun getSquadraUtenteInLega(idLega: Int, username: String): Flow<List<Cantante>>

    @Query("UPDATE composizione_squadra SET ruolo = :nuovoRuolo WHERE id_squadra = :idSquadra AND nome_cantante = :nomeCantante")
    suspend fun updateRuoloCantante(idSquadra: Int, nomeCantante: String, nuovoRuolo: Int)

    // --- BUNDLE E OFFERTE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBundle(bundle: Bundle)

    @Query("SELECT * FROM bundle")
    fun getAllBundles(): Flow<List<Bundle>>

    @Query("SELECT * FROM offerta_utente WHERE nome_utente = :username AND id_bundle = :idBundle")
    suspend fun getOffertaUtente(username: String, idBundle: Int): OffertaUtente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registraAcquistoBundle(offerta: OffertaUtente)
    
    @Query("UPDATE utente SET token = token + :nuoviToken WHERE nome_utente = :username")
    suspend fun aggiungiToken(username: String, nuoviToken: Int)
}
