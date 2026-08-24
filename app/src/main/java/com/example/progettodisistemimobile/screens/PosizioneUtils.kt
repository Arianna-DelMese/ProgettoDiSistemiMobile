package com.example.progettodisistemimobile.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale

/** Coordinate + nome della città, se siamo riusciti a ricavarlo. */
data class PosizioneTrovata(
    val latitudine: Double,
    val longitudine: Double,
    val citta: String?
)

/**
 * Chiede al sistema la posizione attuale e prova a tradurla in un nome di città.
 * Restituisce null se la posizione non è disponibile (GPS spento, permesso negato...).
 * Va chiamata solo DOPO aver verificato il permesso.
 */
@SuppressLint("MissingPermission")
suspend fun leggiPosizione(context: Context): PosizioneTrovata? {
    return try {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val posizione = client.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).await() ?: return null

        val citta = try {
            Geocoder(context, Locale.getDefault())
                .getFromLocation(posizione.latitude, posizione.longitude, 1)
                ?.firstOrNull()
                ?.locality
        } catch (e: Exception) {
            null // il geocoder può fallire senza rete
        }

        PosizioneTrovata(posizione.latitude, posizione.longitude, citta)
    } catch (e: Exception) {
        null
    }
}