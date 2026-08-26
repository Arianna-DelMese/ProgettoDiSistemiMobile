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

    @Query("UPDATE utente SET dati_biomedici = :dati WHERE nome_utente = :username")
    suspend fun updateDatiBiomedici(username: String, dati: String)

    @Query("SELECT EXISTS(SELECT 1 FROM utente WHERE nome_utente = :username)")
    suspend fun utenteEsiste(username: String): Boolean

    @Query("SELECT COUNT(*) FROM utente WHERE nome_utente = :username")
    suspend fun countUtentiByUsername(username: String): Int

    @Query("SELECT * FROM utente WHERE email = :email")
    suspend fun getUtenteByEmail(email: String): Utente?

    @Query("SELECT EXISTS(SELECT 1 FROM utente WHERE email = :email)")
    suspend fun emailEsiste(email: String): Boolean

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

    @Query("SELECT * FROM lega WHERE id_lega = :idLega")
    fun getLegaById(idLega: Int): Flow<Lega?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun joinLega(utenteInLega: UtenteInLega)

    @Query("SELECT * FROM utente_in_lega WHERE id_lega = :idLega AND nome_utente = :username")
    fun getUtenteInLega(idLega: Int, username: String): Flow<UtenteInLega?>

    @Query("""
        SELECT * FROM lega 
        INNER JOIN utente_in_lega ON lega.id_lega = utente_in_lega.id_lega 
        WHERE utente_in_lega.nome_utente = :username
    """)
    fun getLeghePerUtente(username: String): Flow<List<Lega>>

    @Query("""
        SELECT u.*, c.nome_cantante as nome_capitano
        FROM utente_in_lega u
        LEFT JOIN composizione_squadra c ON u.nome_utente = c.nome_utente AND u.id_lega = c.id_lega AND c.ruolo = 0
        WHERE u.id_lega = :idLega
        ORDER BY u.punti DESC
    """)
    fun getClassificaLegaConCapitano(idLega: Int): Flow<List<UserRankingItem>>

    @Query("SELECT * FROM utente_in_lega WHERE id_lega = :idLega AND stato = 1 LIMIT 1")
    suspend fun getCreatoreLega(idLega: Int): UtenteInLega?

    // --- SQUADRA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComposizione(composizione: ComposizioneSquadra)

    @Query("""
        SELECT cantante.* FROM cantante 
        INNER JOIN composizione_squadra ON cantante.nome_cantante = composizione_squadra.nome_cantante 
        WHERE composizione_squadra.id_lega = :idLega AND composizione_squadra.nome_utente = :username
        ORDER BY composizione_squadra.ruolo ASC
    """)
    fun getSquadraUtenteInLega(idLega: Int, username: String): Flow<List<Cantante>>

    @Query("""
        UPDATE composizione_squadra 
        SET ruolo = :nuovoRuolo 
        WHERE id_lega = :idLega AND nome_utente = :username AND nome_cantante = :nomeCantante
    """)
    suspend fun updateRuoloCantante(idLega: Int, username: String, nomeCantante: String, nuovoRuolo: Int)

    @Query("SELECT COALESCE(SUM(prezzo), 0) FROM cantante WHERE nome_cantante IN (:nomi)")
    suspend fun getPrezzoTotale(nomi: List<String>): Int

    // --- BUNDLE E OFFERTE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBundle(bundle: Bundle)

    @Query("SELECT * FROM bundle")
    fun getAllBundles(): Flow<List<Bundle>>

    @Query("SELECT id_bundle FROM offerta_utente WHERE nome_utente = :username")
    fun getPurchasedBundleIds(username: String): Flow<List<Int>>

    @Query("SELECT * FROM offerta_utente WHERE nome_utente = :username AND id_bundle = :idBundle")
    suspend fun getOffertaUtente(username: String, idBundle: Int): OffertaUtente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registraAcquistoBundle(offerta: OffertaUtente)
    
    @Query("UPDATE utente SET token = token + :nuoviToken WHERE nome_utente = :username")
    suspend fun aggiungiToken(username: String, nuoviToken: Int)
}
