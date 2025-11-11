package com.aplicaciones_android.ae3_abpro1.auth

import java.lang.Exception

sealed class AuthResult {
    // Ahora Success contiene el token JWT generado al autenticar correctamente
    data class Success(val token: String) : AuthResult()
    data class InvalidCredentials(val reason: String) : AuthResult()
    data class NetworkError(val exception: Exception) : AuthResult()
}

class AuthRepository(private val service: AuthService) {
    fun login(username: String, password: String): AuthResult {
        return try {
            // Intentar obtener token desde servicio remoto
            val token = service.fetchToken(username, password)
            if (token != null) AuthResult.Success(token) else AuthResult.InvalidCredentials("Credenciales inválidas")
        } catch (e: Exception) {
            AuthResult.NetworkError(e)
        }
    }
}
