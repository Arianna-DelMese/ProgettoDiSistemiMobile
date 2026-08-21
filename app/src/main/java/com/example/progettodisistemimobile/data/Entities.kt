package com.example.progettodisistemimobile.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "utente")
data class Utente(
    @PrimaryKey val nome_utente: String,
    val email: String,
    val password: String,
    val foto_profilo: String?, // URI dell'immagine
    val token: Int = 0,
    val dati_biomedici: String?,
    val dati_pagamento: String?
)

@Entity(tableName = "lega")
data class Lega(
    @PrimaryKey(autoGenerate = true) val id_lega: Int = 0,
    val nome_lega: String,
    val immagine: String?,
    val descrizione: String,
    val stato: Boolean, //true se pubblica, false se privata
    val latitudine: Double?,
    val longitudine: Double?
)

@Entity(
    tableName = "utente_in_lega",
    primaryKeys = ["nome_utente", "id_lega"],
    foreignKeys = [
        ForeignKey(
            entity = Utente::class,
            parentColumns = ["nome_utente"],
            childColumns = ["nome_utente"],
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Lega::class,
            parentColumns = ["id_lega"],
            childColumns = ["id_lega"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UtenteInLega(
    val nome_utente: String,
    val id_lega: Int,
    val stato: Boolean, // true se creatore/admin
    val punti: Int = 0
)

@Entity(
    tableName = "composizione_squadra",
    primaryKeys = ["nome_utente", "id_lega", "nome_cantante"],
    foreignKeys = [
        ForeignKey(
            entity = UtenteInLega::class,
            parentColumns = ["nome_utente", "id_lega"],
            childColumns = ["nome_utente", "id_lega"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Cantante::class,
            parentColumns = ["nome_cantante"],
            childColumns = ["nome_cantante"],
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ComposizioneSquadra(
    val nome_utente: String,
    val id_lega: Int,
    val nome_cantante: String,
    val ruolo: Int // 0=Capitano, 1-4=Titolari, 5-6=Riserve
)

@Entity(tableName = "cantante")
data class Cantante(
    @PrimaryKey val nome_cantante: String,
    val canzone: String,
    val prezzo: Int,
    val punti: Int = 0,
    val ospite: String?,
    val cover: String?,
    val pos_serata_1: Int? = null,
    val pos_serata_2: Int? = null,
    val pos_serata_3: Int? = null,
    val pos_serata_4: Int? = null,
    val pos_serata_5: Int? = null
)

@Entity(tableName = "bundle")
data class Bundle(
    @PrimaryKey val id_bundle: Int,
    val token: Int,
    val prezzo: Int
)

@Entity(
    tableName = "offerta_utente",
    primaryKeys = ["nome_utente", "id_bundle"],
    foreignKeys = [
        ForeignKey(
            entity = Utente::class,
            parentColumns = ["nome_utente"],
            childColumns = ["nome_utente"],
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Bundle::class,
            parentColumns = ["id_bundle"],
            childColumns = ["id_bundle"]
        )
    ]
)
data class OffertaUtente(
    val nome_utente: String,
    val id_bundle: Int,
    val stato: Boolean
)
