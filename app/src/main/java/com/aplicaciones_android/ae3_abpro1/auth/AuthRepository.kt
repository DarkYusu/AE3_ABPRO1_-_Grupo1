// ...existing code...
package com.aplicaciones_android.ae3_abpro1.auth

sealed class AuthResult {
    object Success : AuthResult()
    data class InvalidCredentials(val reason: String) : AuthResult()
    data class NetworkError(val exception: Exception) : AuthResult()
}

class AuthRepository(private val service: AuthService) {
    fun login(username: String, password: String): AuthResult {
        return try {
            val ok = service.authenticate(username, password)
            if (ok) AuthResult.Success else AuthResult.InvalidCredentials("Credenciales inválidas")
        } catch (e: Exception) {
            AuthResult.NetworkError(e)
        }
    }
}

