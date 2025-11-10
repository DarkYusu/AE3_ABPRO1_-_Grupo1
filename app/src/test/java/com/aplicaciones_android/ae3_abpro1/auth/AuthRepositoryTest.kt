package com.aplicaciones_android.ae3_abpro1.auth

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.kotlin.mock

/**
 * Tests unitarios para AuthRepository
 * -----------------------------------
 * Tipo de test: Unit test (JUnit) usando Mockito-Kotlin para simular dependencias.
 * Objetivo: Verificar la lógica de `AuthRepository.login(...)` en los diferentes
 * escenarios que debe manejar:
 *  - Autenticación exitosa (Success)
 *  - Credenciales inválidas (InvalidCredentials)
 *  - Error de red/excepción (NetworkError)
 *
 * Requisitos cubiertos:
 *  - Probar el repositorio de autenticación para simular respuestas de éxito y error.
 */
class AuthRepositoryTest {
    private val service: AuthService = mock()
    private val repository = AuthRepository(service)

    @Test
    // Caso: servicio devuelve true -> se espera AuthResult.Success
    // Tipo de test: unitario
    fun `login returns Success when service authenticates`() {
        whenever(service.authenticate("user", "pass")).thenReturn(true)
        val result = repository.login("user", "pass")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    // Caso: servicio devuelve false -> se espera AuthResult.InvalidCredentials
    // Verifica que el repositorio mapea correctamente la respuesta de la API/servicio.
    fun `login returns InvalidCredentials when service returns false`() {
        whenever(service.authenticate("user", "wrong")).thenReturn(false)
        val result = repository.login("user", "wrong")
        assertTrue(result is AuthResult.InvalidCredentials)
    }

    @Test
    // Caso: servicio lanza excepción -> se espera AuthResult.NetworkError
    // Se usa `doThrow` para simular una excepción de red en la dependencia externa.
    fun `login returns NetworkError when service throws`() {
        doThrow(Exception("net")).whenever(service).authenticate("network", "x")
        val result = repository.login("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }
}
