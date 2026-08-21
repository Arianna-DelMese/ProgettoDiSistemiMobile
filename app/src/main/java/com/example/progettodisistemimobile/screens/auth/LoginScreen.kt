package com.example.progettodisistemimobile.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.AuthUiState
import com.example.progettodisistemimobile.data.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegistrazione: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibile by rememberSaveable { mutableStateOf(false) }

    val isLoading = uiState is AuthUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))

        Text(
            text = "FANTASANREMO",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Accedi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.resetErrore()
            },
            label = { Text("E-mail") },
            singleLine = true,
            enabled = !isLoading,
            isError = uiState is AuthUiState.Error,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.resetErrore()
            },
            label = { Text("Password") },
            singleLine = true,
            enabled = !isLoading,
            isError = uiState is AuthUiState.Error,
            visualTransformation = if (passwordVisibile) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
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
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp)) {
            (uiState as? AuthUiState.Error)?.let { stato ->
                Text(
                    text = stato.messaggio,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.login(email, password) },
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

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { /* TODO: Credential Manager + Google Sign In */ },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login con Google")
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Non hai un account?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                viewModel.resetErrore()
                onNavigateToRegistrazione()
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrati")
        }

        Spacer(Modifier.height(32.dp))
    }
}