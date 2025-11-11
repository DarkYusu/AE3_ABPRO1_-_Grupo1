package com.aplicaciones_android.ae3_abpro1.auth

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.kotlin.mock

/**
 * Tests unitarios para `AuthRepository`.
 *
 * Cobertura:
 *  - Cuando el servicio devuelve un token -> Success con token
 *  - Cuando el servicio devuelve null -> InvalidCredentials
 *  - Cuando el servicio lanza excepción -> NetworkError
 */
class AuthRepositoryTest {
    private val service: AuthService = mock()
    private val repository = AuthRepository(service)

    @Test
    // Caso: servicio devuelve token -> se espera Success con el token proporcionado
    fun `login returns Success when service returns token`() {
        whenever(service.fetchToken("user", "pass")).thenReturn("ey.token.mock")
        val result = repository.login("user", "pass")
        assertTrue(result is AuthResult.Success)
        val token = (result as AuthResult.Success).token
        assertEquals("ey.token.mock", token)
    }

    @Test
    // Caso: servicio devuelve null -> se espera InvalidCredentials
    fun `login returns InvalidCredentials when service returns null`() {
        whenever(service.fetchToken("user", "wrong")).thenReturn(null)
        val result = repository.login("user", "wrong")
        assertTrue(result is AuthResult.InvalidCredentials)
    }

    @Test
    // Caso: servicio lanza excepción -> se espera NetworkError
    fun `login returns NetworkError when service throws`() {
        doThrow(Exception("net")).whenever(service).fetchToken("network", "x")
        val result = repository.login("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }
}
