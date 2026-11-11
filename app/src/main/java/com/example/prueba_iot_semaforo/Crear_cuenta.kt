package com.example.prueba_iot_semaforo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class Crear_cuenta : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolver: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        etNombre = findViewById(R.id.etnomreg)
        etCorreo = findViewById(R.id.etcorreg)
        etPassword = findViewById(R.id.etconreg)
        btnRegistrar = findViewById(R.id.btncrearcuenta)
        btnVolver = findViewById(R.id.btnvolcrear)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val passPlano = etPassword.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty() || passPlano.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passSeguro = hashSHA256(passPlano)

            val usuario = HashMap<String, Any>()
            usuario["nombre"] = nombre
            usuario["correo"] = correo
            usuario["password"] = passSeguro

            db.getReference("Usuario").push().setValue(usuario).addOnSuccessListener {
                Toast.makeText(this, "Cuenta registrada de forma segura", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al registrar en Firebase", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun hashSHA256(texto: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}