# FantaSanremo Mobile
Applicazione Android per la gestione di leghe, squadre e punteggi del Fantasanremo, sviluppata come progetto
per il corso di Programmazione di Sistemi Mobile. L'app permette agli utenti di creare squadre, unirsi a leghe globali o locali 
e monitorare le classifiche del Festival di Sanremo.

## Funzionalità principali
### Gestione Utente e Profilo
• Autenticazione: Sistema di Login e Registrazione sicuro.

• Profilo Personalizzato: Modifica del nome utente e della foto profilo (tramite Galleria o scatto diretto con Fotocamera).

• Personalizzazione: Supporto al Tema Chiaro, Scuro e di Sistema con una palette colori personalizzata.

### Leghe e Social
• Creazione Leghe: Possibilità di creare leghe pubbliche o private.

• Geolocalizzazione: Integrazione con mappe per trovare leghe create nelle vicinanze.

• Inviti Rapidi: Sistema di condivisione del codice lega per invitare gli amici.

• Classifiche: Monitoraggio in tempo reale della posizione degli utenti all'interno della lega.

### Gestione Squadra
• Creazione Squadra: Selezione di 7 cantanti con un budget limitato di token.

• Modifica Formazione: Interfaccia con Drag & Drop per scambiare titolari e riserve.

• Capitano: Selezione del capitano della squadra.

### Shop & Biometria
• Acquisto Token: Bundle di token acquistabili per potenziare la propria squadra.

• Sicurezza Biometrica: Integrazione con BiometricPrompt (Impronta digitale o PIN) per autorizzare gli acquisti.

• Promo Benvenuto: Bonus del 25% di token extra sul primo acquisto di ogni bundle.

## Tech Stack
• Linguaggio: Kotlin

• UI: Jetpack Compose (Material 3)

• Architettura: MVVM (Model-View-ViewModel)

• Database Locale: Room Database per la persistenza dei dati (Utenti, Cantanti, Leghe, Acquisti).

• Storage: DataStore Preferences per le impostazioni dell'utente (Tema, Sessione).

• Networking/Immagini: Coil per il caricamento asincrono delle immagini.

• Mappe: OpenStreetMap (osmdroid) per la visualizzazione delle leghe globali.

• Biometria: Android Biometric Library per la sicurezza dei pagamenti.

## Requisiti di Sistema
• Versione Android Minima: API 26 (Android 8.0)

• Target SDK: API 35 (Android 15)

• Permessi richiesti: Fotocamera, Posizione, Accesso a Internet, Biometria.

## Autrici
Arianna Del Mese e Yue Shen.
