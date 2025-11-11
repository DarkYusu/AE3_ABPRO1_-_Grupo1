package com.aplicaciones_android.ae3_abpro1.auth

/**
 * Simula una API remota para autenticación.
 */
interface AuthService {
    /** Retorna true si las credenciales son correctas, lanza excepción en fallo de red. */
    @Throws(Exception::class)
    fun authenticate(username: String, password: String): Boolean

    /**
     * Intenta obtener un token JWT del proveedor remoto.
     * - Devuelve el token si las credenciales son válidas.
     * - Devuelve null si las credenciales son inválidas (por ejemplo 401).
     * - Lanza excepción en caso de fallo de red u otros errores.
     */
    @Throws(Exception::class)
    fun fetchToken(username: String, password: String): String?
}
