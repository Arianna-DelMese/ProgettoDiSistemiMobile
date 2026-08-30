# FantaSanremo Mobile
Applicazione Android ispirata al FantaSanremo, sviluppata come progetto per il corso di Programmazione di Sistemi Mobile, Università di Bologna, A.A. 2025/26. 

L'app permette agli utenti di creare squadre di sette cantanti rispettando un budget e di sfidare altri partecipanti all'interno di leghe globali o locali. È inoltre possibile seguire le classifiche del Festival di Sanremo serata per serata.

## Funzionalità principali
### Gestione Utente e Profilo
• Autenticazione: Sistema di Login e Registrazione sicuro.

• Profilo Personalizzato: Modifica del nome utente e della foto profilo (tramite Galleria o scatto diretto con Fotocamera).

• Personalizzazione: Supporto al Tema Chiaro, Scuro e di Sistema con una palette colori personalizzata.

### Leghe e Social
• Creazione Leghe: Possibilità di creare leghe pubbliche o private con l'aggiunta facoltativa dell'immagine e posizione.

• Geolocalizzazione: Integrazione con mappe per trovare leghe pubbliche geolocalizzate su OpenStreetmap con marker interattivi.

• Inviti: Sistema di condivisione del codice lega per invitare altri utenti.

• Classifiche: Posizione e punteggio di tutti i partecipanti alla lega.

### Gestione Squadra
• Creazione Squadra: Selezione di 7 cantanti con un budget limitato di token, con ricerca per nome o canzone.

• Modifica Formazione: Interfaccia con Drag & Drop per scambiare titolari e riserve.

• Capitano: Selezione del capitano della squadra per ottenere punteggio bonus.

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

• Immagini: Coil per il caricamento asincrono delle immagini.

• Mappe: OpenStreetMap (osmdroid) per la visualizzazione delle leghe globali.

• Biometria: Android Biometric Library per la sicurezza dei pagamenti.

## Requisiti di Sistema
• Versione Android Minima: API 26 (Android 8.0)

• Target SDK: API 35 (Android 15)

• Permessi richiesti: Fotocamera, Posizione, Accesso a Internet, Biometria.


## Come avviare il progetto
 
1. Clonare il repository e aprirlo con Android Studio
2. Attendere il Gradle sync (scarica le dipendenze)
3. Avviare su emulatore o dispositivo con API 26 o superiore
Il database si popola automaticamente al primo avvio con 30 cantanti, alcune leghe e 6 utenti di prova.
 
**Credenziali di test:** `mario@example.com` / `pass123`
 
> Se dopo un aggiornamento l'app mostra dati incoerenti, disinstallarla e reinstallarla: lo schema del database viene rigenerato.

## Scelte progettuali e limiti noti
 
**Architettura local-first.** Tutti i dati dell’app vengono salvati localmente sul dispositivo tramite un database Room, senza utilizzare un backend esterno. Questa scelta è stata fatta considerando lo scopo didattico del progetto. Di conseguenza, le leghe create non possono essere condivise tra dispositivi diversi: la gestione di più utenti viene quindi simulata attraverso account differenti presenti sullo stesso telefono.

 
**Deep link limitati allo stesso dispositivo.** L'invito usa uno schema personalizzato (`fantasanremo://join/{id}`): funziona correttamente, ma non essendo un App Link verificato non viene reso cliccabile dalle app di messaggistica, e l'id della lega ha significato solo nel database locale.
 
**Punteggi calcolati alla creazione della squadra.** Il punteggio somma i punti dei titolari raddoppiando il capitano, e viene calcolato al momento dell'iscrizione. Il cambio formazione influisce sulle serate successive, che in questa versione non vengono simulate.

## Mockup
[Mockup Mobile.pdf](https://github.com/user-attachments/files/31617797/Mockup.Mobile.pdf)

## Autrici
Arianna Del Mese e Yue Shen.

