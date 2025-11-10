package com.aplicaciones_android.ae3_abpro1.auth

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthManagerTest {
    private val service: AuthService = mock()
    private val repository = AuthRepository(service)
    private val manager = AuthManager(repository)

    @Test
    fun `signIn returns InvalidCredentials when inputs empty`() {
        val result = manager.signIn("", "")
        assertTrue(result is AuthResult.InvalidCredentials)
    }

    @Test
    fun `signIn delegates to repository and returns Success`() {
        whenever(service.authenticate("user", "pass")).thenReturn(true)
        val result = manager.signIn("user", "pass")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    fun `signIn handles network error`() {
        whenever(service.authenticate("network", "x")).then { throw Exception("net") }
        val result = manager.signIn("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }
}

