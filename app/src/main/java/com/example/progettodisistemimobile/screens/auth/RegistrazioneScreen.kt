package com.example.progettodisistemimobile.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.AuthUiState
import com.example.progettodisistemimobile.data.AuthViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrazioneScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var nomeUtente by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confermaPassword by rememberSaveable { mutableStateOf("") }

    val isLoading = uiState is AuthUiState.Loading
    val messaggioErrore = (uiState as? AuthUiState.Error)?.messaggio
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Registrato) {
            viewModel.resetErrore()
            onBack()
        }
    }
    var passwordVisibile by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrati") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.resetErrore()
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Torna al login"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "FANTASANREMO",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = nomeUtente,
                onValueChange = {
                    nomeUtente = it
                    viewModel.resetErrore()
                },
                label = { Text("Nome utente") },
                supportingText = { Text("Sarà visibile agli altri partecipanti") },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.resetErrore()
                },
                label = { Text("E-mail") },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.resetErrore()
                },
                label = { Text("Password") },
                supportingText = { Text("Almeno 6 caratteri") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (passwordVisibile) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisibile = !passwordVisibile }) {
                        Icon(
                            imageVector = if (passwordVisibile) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (passwordVisibile) "Nascondi password"
                            else "Mostra password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confermaPassword,
                onValueChange = {
                    confermaPassword = it
                    viewModel.resetErrore()
                },
                label = { Text("Conferma password") },
                singleLine = true,
                enabled = !isLoading,
                isError = confermaPassword.isNotEmpty() && confermaPassword != password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp)) {
                messaggioErrore?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.registra(nomeUtente, email, password, confermaPassword)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Conferma")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}