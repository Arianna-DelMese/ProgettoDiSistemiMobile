package com.example.progettodisistemimobile.data

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Stato della UI delle schermate di autenticazione. */
sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Registrato : AuthUiState
    data class Error(val messaggio: String) : AuthUiState
}
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).appDao()
    private val settings = SettingsManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * null = sto ancora leggendo il DataStore
     * ""   = nessun utente loggato
     * altro = username dell'utente loggato
     */
    val sessione: StateFlow<String?> = settings.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    // Login

    fun login(email: String, password: String) {
        val emailPulita = email.trim()

        if (emailPulita.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Compila tutti i campi")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val utente = dao.getUtenteByEmail(emailPulita)

            // Messaggio volutamente generico: non riveliamo se l'email esiste.
            if (utente == null || !PasswordUtils.verify(password, utente.password)) {
                _uiState.value = AuthUiState.Error("Email o password non corretti")
                return@launch
            }

            settings.setCurrentUser(utente.nome_utente)
            _uiState.value = AuthUiState.Idle
        }
    }

    // Registrazione

    fun registra(
        nomeUtente: String,
        email: String,
        password: String,
        confermaPassword: String
    ) {
        val nomePulito = nomeUtente.trim()
        val emailPulita = email.trim()

        val erroreValidazione = validaRegistrazione(nomePulito, emailPulita, password, confermaPassword)
        if (erroreValidazione != null) {
            _uiState.value = AuthUiState.Error(erroreValidazione)
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            if (dao.utenteEsiste(nomePulito)) {
                _uiState.value = AuthUiState.Error("Nome utente già in uso")
                return@launch
            }
            if (dao.emailEsiste(emailPulita)) {
                _uiState.value = AuthUiState.Error("Esiste già un account con questa email")
                return@launch
            }

            dao.insertUtente(
                Utente(
                    nome_utente = nomePulito,
                    email = emailPulita,
                    password = PasswordUtils.hash(password),
                    foto_profilo = null,
                    token = 100, // token di benvenuto
                )
            )

            _uiState.value = AuthUiState.Registrato
        }
    }

    private fun validaRegistrazione(
        nomeUtente: String,
        email: String,
        password: String,
        conferma: String
    ): String? = when {
        nomeUtente.isBlank() || email.isBlank() || password.isBlank() ->
            "Compila tutti i campi"
        nomeUtente.length < 3 ->
            "Il nome utente deve avere almeno 3 caratteri"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "Inserisci un indirizzo email valido"
        password.length < 6 ->
            "La password deve avere almeno 6 caratteri"
        password != conferma ->
            "Le due password non coincidono"
        else -> null
    }

    // ---------------- LOGOUT ----------------

    fun logout() {
        viewModelScope.launch {
            settings.clearCurrentUser()
            _uiState.value = AuthUiState.Idle
        }
    }

    /** Riporta lo stato a Idle, tranne mentre un'operazione è in corso. */
    fun resetErrore() {
        if (_uiState.value !is AuthUiState.Loading) {
            _uiState.value = AuthUiState.Idle
        }
    }
}