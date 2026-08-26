package com.example.progettodisistemimobile.screens.shop

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.progettodisistemimobile.data.Bundle

fun Context.findActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

fun showBiometricPurchasePrompt(
    context: Context,
    bundle: Bundle,
    onSuccess: () -> Unit
) {
    val fragmentActivity = context.findActivity() ?: run {
        Toast.makeText(context, "Errore: Activity non trovata", Toast.LENGTH_SHORT).show()
        return
    }

    val executor = ContextCompat.getMainExecutor(context)
    val biometricManager = BiometricManager.from(context)

    // Definiamo i metodi di sblocco: Impronta (Strong) o PIN/Sequenza
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL

    val canAuthenticate = biometricManager.canAuthenticate(authenticators)

    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        Toast.makeText(context, "Configura un PIN o un'impronta nelle impostazioni", Toast.LENGTH_LONG).show()
        return
    }

    val biometricPrompt = BiometricPrompt(
        fragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(context, "Errore: $errString", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Conferma Acquisto")
        .setSubtitle("Autorizza il pagamento di ${bundle.prezzo}€")
        .setAllowedAuthenticators(authenticators)
        // Nota: se usi DEVICE_CREDENTIAL, non devi impostare setNegativeButtonText
        .build()

    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        Toast.makeText(context, "Impossibile avviare la scansione", Toast.LENGTH_SHORT).show()
    }
}
