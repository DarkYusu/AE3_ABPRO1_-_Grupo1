package com.aplicaciones_android.ae3_abpro1.auth

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/**
 * RemoteAuthService
 * - Implementación simple basada en HttpURLConnection para obtener JWT desde
 *   un servicio remoto.
 * - La baseUrl debe apuntar al host que provee la API (p. ej. https://test-poke-jwt-...)
 * - Este cliente es muy básico: envía POST JSON {"username","password"} y
 *   busca en la respuesta un campo `token`, `access_token` o `jwt`.
 *
 * El constructor permite pasar un `loginPath` o dejarlo vacío; en este último
 * caso el cliente intentará endpoints comunes.
 */
class RemoteAuthService(private val baseUrl: String, private val loginPath: String? = null) : AuthService {
    private val defaultPaths = listOf("/login", "/auth/login", "/token", "/authenticate")

    override fun authenticate(username: String, password: String): Boolean {
        return try {
            val t = fetchToken(username, password)
            t != null
        } catch (e: Exception) {
            throw e
        }
    }

    override fun fetchToken(username: String, password: String): String? {
        val pathsToTry = if (!loginPath.isNullOrBlank()) listOf(loginPath) else defaultPaths
        var lastException: Exception? = null
        for (p in pathsToTry) {
            val urlString = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) + p else baseUrl + p
            try {
                val token = postForToken(urlString, username, password)
                if (token != null) return token
            } catch (e: Exception) {
                lastException = e
                // intentar siguiente path
            }
        }
        // Si ninguna ruta devolvió token, si tuvimos excepción final la relanzamos
        if (lastException != null) throw lastException
        return null
    }

    private fun postForToken(urlString: String, username: String, password: String): String? {
        val url = URL(urlString)
        var conn: HttpURLConnection? = null
        try {
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 5000
                readTimeout = 5000
            }

            val body = "{\"username\":\"${escapeJson(username)}\",\"password\":\"${escapeJson(password)}\"}"
            val out: OutputStream = conn.outputStream
            out.write(body.toByteArray(Charsets.UTF_8))
            out.flush()
            out.close()

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }

            if (code in 200..299) {
                return extractTokenFromJson(response)
            } else if (code == 401) {
                return null
            } else {
                throw Exception("HTTP ${code}: ${response}")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            conn?.disconnect()
        }
    }

    private fun extractTokenFromJson(json: String): String? {
        // Buscamos claves comunes sin usar regex complejos.
        val candidates = listOf("token", "access_token", "jwt", "accessToken")
        for (key in candidates) {
            val quoted = "\"$key\""
            val idx = json.indexOf(quoted)
            if (idx == -1) continue
            // buscar ':' después de idx
            var pos = idx + quoted.length
            while (pos < json.length && json[pos] != ':') pos++
            if (pos >= json.length) continue
            pos++
            // saltar espacios
            while (pos < json.length && json[pos].isWhitespace()) pos++
            // si empieza con '"' es un string
            if (pos < json.length && json[pos] == '"') {
                val start = pos + 1
                val end = json.indexOf('"', start)
                if (end > start) return json.substring(start, end)
            } else {
                // valor no string: leer hasta coma o cierre
                val start = pos
                var end = start
                while (end < json.length && json[end] != ',' && json[end] != '}' && !json[end].isWhitespace()) end++
                if (end > start) return json.substring(start, end)
            }
        }
        return null
    }

    private fun escapeJson(s: String): String {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
    }
}
