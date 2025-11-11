package com.aplicaciones_android.ae3_abpro1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aplicaciones_android.ae3_abpro1.auth.*

/**
 * Actividad principal que muestra un formulario de login.
 *
 * Notas de implementación:
 * - Contiene una implementación en memoria de `AuthService` pensada para
 *   demos y tests (no para producción).
 * - Mensajes de UI están externalizados en `res/values/strings.xml`.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de insets para edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Servicio simulado en memoria. Credenciales válidas definidas aquí.
        val service = object : AuthService {
            private val valid = listOf(
                "sebastian" to "123456",
                "alumno1" to "alumno1",
                "alumno2" to "alumno2"
            )

            override fun authenticate(username: String, password: String): Boolean {
                // Simular fallo de red con el username reservado "network"
                if (username == "network") throw Exception("Error de red simulado")
                return valid.any { it.first == username && it.second == password }
            }

            override fun fetchToken(username: String, password: String): String? {
                if (username == "network") throw Exception("Error de red simulado")
                return if (valid.any { it.first == username && it.second == password }) "token_$username" else null
            }
        }

        // Inyectar dependencias simples
        val repository = AuthRepository(service)
        val manager = AuthManager(repository)

        // Referencias a elementos de la UI
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val message = findViewById<TextView>(R.id.message)

        // Manejador del click del botón de login
        loginButton.setOnClickListener {
            val result = manager.signIn(usernameInput.text.toString(), passwordInput.text.toString())
            when (result) {
                is AuthResult.Success -> message.text = getString(R.string.login_success)
                is AuthResult.InvalidCredentials -> message.text = result.reason
                is AuthResult.NetworkError -> message.text = getString(R.string.network_error, result.exception.message ?: "")
            }
        }
    }
}