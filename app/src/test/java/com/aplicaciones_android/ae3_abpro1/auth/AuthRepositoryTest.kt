package com.aplicaciones_android.ae3_abpro1.auth

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.kotlin.mock

class AuthRepositoryTest {
    private val service: AuthService = mock()
    private val repository = AuthRepository(service)

    @Test
    fun `login returns Success when service authenticates`() {
        whenever(service.authenticate("user", "pass")).thenReturn(true)
        val result = repository.login("user", "pass")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    fun `login returns InvalidCredentials when service returns false`() {
        whenever(service.authenticate("user", "wrong")).thenReturn(false)
        val result = repository.login("user", "wrong")
        assertTrue(result is AuthResult.InvalidCredentials)
    }

    @Test
    fun `login returns NetworkError when service throws`() {
        doThrow(Exception("net")).whenever(service).authenticate("network", "x")
        val result = repository.login("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }
}
