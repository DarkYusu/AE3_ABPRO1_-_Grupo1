package com.aplicaciones_android.ae3_abpro1.auth

/**
 * AuthManager
 * ----------------
 * Lógica de negocio para el proceso de login.
 *
 * Responsabilidad:
 * - Validar que los campos de entrada no estén vacíos.
 * - Normalizar (trim) el nombre de usuario antes de delegar en el repositorio.
 * - Devolver un resultado de tipo [AuthResult] que encapsula éxito, credenciales
 *   inválidas o errores de red.
 *
 * Uso en tests:
 * - Tests unitarios: `app/src/test/java/.../auth/AuthManagerTest.kt` prueban:
 *     * Caso inputs vacíos -> `AuthResult.InvalidCredentials`.
 *     * Delegación al repositorio para casos de éxito y error de red.
 * - Tests de instrumentación (UI): indirectamente ejercitado por
 *   `app/src/androidTest/java/.../LoginActivityTest.kt` a través de `MainActivity`.
 */
class AuthManager(private val repository: AuthRepository) {
    /**
     * signIn
     * @param username nombre de usuario (nullable) recibido desde la UI o caller
     * @param password contraseña (nullable)
     * @return [AuthResult] con el resultado de la operación
     *
     * Comportamiento:
     * - Si `username` o `password` están vacíos o nulos, se devuelve
     *   `AuthResult.InvalidCredentials("Usuario o contraseña vacíos")`.
     *   (esto permite que la capa de presentación muestre un mensaje adecuado)
     * - En caso contrario, se llama a `repository.login(...)` y se devuelve
     *   el resultado tal cual (puede ser Success, InvalidCredentials o NetworkError).
     *
     * Cobertura en tests unitarios:
     * - `signIn("", "")` -> valida la rama de credenciales vacías.
     * - `signIn("user", "pass")` -> delega al repositorio (mockeado) y espera Success.
     * - `signIn("network", "x")` -> cuando el servicio/repository lanza excepción,
     *   se espera `AuthResult.NetworkError`.
     */
    fun signIn(username: String?, password: String?): AuthResult {
        // Validación básica de entrada: evita llamadas con valores nulos o vacíos
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            // Devuelve un AuthResult que indica credenciales inválidas.
            // Este texto también es verificado por algunos tests unitarios.
            return AuthResult.InvalidCredentials("Usuario o contraseña vacíos")
        }
        // Normalizar usuario (el repository espera el username sin espacios al inicio/fin)
        return repository.login(username.trim(), password)
    }
}
