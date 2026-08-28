package com.example.progettodisistemimobile.screens

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.progettodisistemimobile.data.Lega
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Mappa OpenStreetMap con un segnaposto per ogni lega che ha delle coordinate.
 * Toccando un segnaposto si seleziona la lega corrispondente.
 */
@Composable
fun MappaLeghe(
    leghe: List<Lega>,
    onLegaSelezionata: (Lega) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // osmdroid va configurato una volta prima di creare la mappa
    remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        // Obbligatorio: OSM richiede di identificare l'app che scarica le mappe
        // OSM blocca gli user agent che iniziano con "com.example":
        // serve una stringa identificativa nostra
        Configuration.getInstance().userAgentValue = "FantaSanremoUnibo/1.0"
        true
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.OpenTopo)
                setMultiTouchControls(true)
                controller.setZoom(5.5)
                controller.setCenter(GeoPoint(42.5, 12.5)) // centro Italia
            }
        },
        update = { mapView ->
            // Ridisegno i segnaposti a ogni cambio della lista
            mapView.overlays.clear()

            leghe.forEach { lega ->
                val lat = lega.latitudine
                val lon = lega.longitudine
                if (lat != null && lon != null) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(lat, lon)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = lega.nome_lega
                        snippet = lega.descrizione
                        setOnMarkerClickListener { _, _ ->
                            onLegaSelezionata(lega)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            }

            mapView.invalidate() // forza il ridisegno
        }
    )
}