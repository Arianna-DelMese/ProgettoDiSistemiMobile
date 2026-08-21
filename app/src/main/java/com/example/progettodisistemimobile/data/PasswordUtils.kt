package com.example.progettodisistemimobile.data

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Hashing delle password con salt casuale per utente.
 * Il valore salvato nel campo Utente.password ha la forma "saltHex:hashHex".
 */
object PasswordUtils {

    private const val SEPARATOR = ":"
    private const val SALT_BYTES = 16

    /** Genera un salt casuale e restituisce "salt:hash" da salvare nel DB. */
    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        val saltHex = salt.toHex()
        return saltHex + SEPARATOR + sha256(saltHex + password)
    }

    /** Verifica una password in chiaro contro il valore "salt:hash" salvato. */
    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(SEPARATOR)
        if (parts.size != 2) return false
        val (saltHex, expectedHash) = parts
        return sha256(saltHex + password) == expectedHash
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .toHex()

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}