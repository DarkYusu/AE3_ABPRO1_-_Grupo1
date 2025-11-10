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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Implementación simple del servicio (solo para demo y tests)
        val service = object : AuthService {
            override fun authenticate(username: String, password: String): Boolean {
                // Simula fallo de red si username == "network"
                if (username == "network") throw Exception("Simulated network error")
                return username == "user" && password == "pass"
            }
        }

        val repository = AuthRepository(service)
        val manager = AuthManager(repository)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val message = findViewById<TextView>(R.id.message)

        loginButton.setOnClickListener {
            val result = manager.signIn(usernameInput.text.toString(), passwordInput.text.toString())
            when (result) {
                is AuthResult.Success -> message.text = "Inicio de sesión exitoso"
                is AuthResult.InvalidCredentials -> message.text = result.reason // mostrar solo la razón
                is AuthResult.NetworkError -> message.text = "Error de red: ${'$'}{result.exception.message}"
            }
        }
    }
}