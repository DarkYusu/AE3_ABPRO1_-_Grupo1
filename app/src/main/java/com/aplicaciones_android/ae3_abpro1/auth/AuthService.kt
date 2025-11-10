// ...existing code...
package com.aplicaciones_android.ae3_abpro1.auth

/**
 * Simula una API remota para autenticación.
 */
interface AuthService {
    /** Retorna true si las credenciales son correctas, lanza excepción en fallo de red. */
    @Throws(Exception::class)
    fun authenticate(username: String, password: String): Boolean
}

