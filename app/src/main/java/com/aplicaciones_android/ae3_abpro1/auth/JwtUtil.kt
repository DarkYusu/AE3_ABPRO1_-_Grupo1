package com.aplicaciones_android.ae3_abpro1.auth

import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8
import java.time.Instant

/**
 * JwtUtil (versión sin dependencias externas)
 * ------------------------------------------
 * Genera y verifica JWT utilizando HMAC-SHA256. Para evitar dependencias en
 * `org.json` (que no está disponible en unit tests JVM), se construye JSON
 * manualmente con escape básico.
 */
object JwtUtil {
    private const val ALG = "HS256"

    fun generateToken(subject: String, secret: String, expiresInSeconds: Long = 3600): String {
        val headerJson = buildJson(mapOf("alg" to ALG, "typ" to "JWT"))
        val now = Instant.now().epochSecond
        val payloadJson = buildJson(mapOf("sub" to subject, "iat" to now, "exp" to (now + expiresInSeconds)))

        val headerB64 = base64UrlEncode(headerJson.toByteArray(UTF_8))
        val payloadB64 = base64UrlEncode(payloadJson.toByteArray(UTF_8))
        val signingInput = "$headerB64.$payloadB64"
        val signature = signHmacSha256(signingInput.toByteArray(UTF_8), secret.toByteArray(UTF_8))
        val signatureB64 = base64UrlEncode(signature)
        return "$signingInput.$signatureB64"
    }

    fun isTokenValid(token: String, secret: String): Boolean {
        try {
            val parts = token.split('.')
            if (parts.size != 3) return false
            val headerB64 = parts[0]
            val payloadB64 = parts[1]
            val signatureB64 = parts[2]

            val signingInput = "$headerB64.$payloadB64"
            val expectedSig = signHmacSha256(signingInput.toByteArray(UTF_8), secret.toByteArray(UTF_8))
            val expectedSigB64 = base64UrlEncode(expectedSig)
            if (!constantTimeEquals(expectedSigB64.toByteArray(UTF_8), signatureB64.toByteArray(UTF_8))) return false

            val payloadJson = String(base64UrlDecode(payloadB64), UTF_8)
            val exp = extractLongFromJson(payloadJson, "exp") ?: return false
            val now = Instant.now().epochSecond
            if (exp != 0L && now > exp) return false
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private fun signHmacSha256(data: ByteArray, key: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(key, "HmacSHA256")
        mac.init(secretKeySpec)
        return mac.doFinal(data)
    }

    private fun base64UrlEncode(input: ByteArray): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input)
    }

    private fun base64UrlDecode(input: String): ByteArray {
        return Base64.getUrlDecoder().decode(input)
    }

    // Comparación en tiempo constante para mitigar ataques por temporización.
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }

    // Construye una representación JSON simple a partir de un mapa. Maneja valores
    // String y Number (Long/Int). No es un parser completo pero es suficiente para
    // los campos usados en los tokens.
    private fun buildJson(map: Map<String, Any>): String {
        val sb = StringBuilder()
        sb.append('{')
        var first = true
        for ((k, v) in map) {
            if (!first) sb.append(',')
            first = false
            sb.append('"').append(escapeJson(k)).append('"').append(':')
            when (v) {
                is String -> {
                    sb.append('"').append(escapeJson(v)).append('"')
                }
                is Number -> {
                    sb.append(v.toString())
                }
                else -> {
                    sb.append('"').append(escapeJson(v.toString())).append('"')
                }
            }
        }
        sb.append('}')
        return sb.toString()
    }

    private fun escapeJson(s: String): String {
        val sb = StringBuilder()
        for (ch in s) {
            when (ch) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    // Extrae un valor numérico (long) simple de un JSON plano sin parsear
    // completamente. Busca "key":<number> y devuelve el número si lo encuentra.
    private fun extractLongFromJson(json: String, key: String): Long? {
        // Buscar "key" (con comillas) para evitar coincidencias parciales
        val idx = json.indexOf("\"$key\"")
        if (idx == -1) return null
        var pos = idx + key.length + 2 // posicion después de "key"
        // avanzar hasta ':'
        while (pos < json.length && json[pos] != ':') pos++
        pos++
        // saltar espacios
        while (pos < json.length && json[pos].isWhitespace()) pos++
        // leer número
        val start = pos
        var end = start
        while (end < json.length && (json[end].isDigit() || json[end] == '-')) end++
        if (start == end) return null
        return try {
            json.substring(start, end).toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
