package com.example.prueba_iot_semaforo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class Login : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnIrCrear: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etCorreo = findViewById(R.id.txtrut)
        etPassword = findViewById(R.id.txtcon)
        btnIniciar = findViewById(R.id.btnini)
        btnIrCrear = findViewById(R.id.btncrear)

        btnIniciar.setOnClickListener {
            val credencialIngresada = etCorreo.text.toString().trim()
            val passPlano = etPassword.text.toString().trim()

            if (credencialIngresada.isEmpty() || passPlano.isEmpty()) {
                Toast.makeText(this, "Ingresa tus credenciales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passHash = hashSHA256(passPlano)

            db.getReference("Usuario").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var accesoValido = false
                    var usuarioEncontrado: Usuario? = null
                    var nombreMostrar = "Usuario"

                    for (u in snapshot.children) {
                        val correoBD = u.child("correo").value?.toString()?.trim() ?: ""
                        val rutBD = u.child("rut").value?.toString()?.trim() ?: ""

                        // Coincidencia por correo o por RUT
                        val idCoincide = correoBD.equals(credencialIngresada, ignoreCase = true) ||
                                rutBD.equals(credencialIngresada, ignoreCase = true)

                        if (idCoincide) {
                            val passBD = u.child("password").value?.toString()?.trim()
                                ?: u.child("contraseña").value?.toString()?.trim()
                                ?: u.child("contrasena").value?.toString()?.trim()
                                ?: ""

                            // Coincidencia de contraseña (hash SHA-256 o texto plano)
                            if (passBD.equals(passHash, ignoreCase = true) || passBD == passPlano) {
                                accesoValido = true
                                val nombre = u.child("nombre").value?.toString() ?: ""
                                val apellido = u.child("apellido").value?.toString() ?: ""
                                val correo = if (correoBD.isNotEmpty()) correoBD else credencialIngresada
                                val rutInt = rutBD.toIntOrNull() ?: credencialIngresada.toIntOrNull() ?: 0
                                val ocupacion = u.child("ocupacion").value?.toString()
                                    ?: u.child("cargo").value?.toString()
                                    ?: ""

                                usuarioEncontrado = Usuario(rutInt, nombre, apellido, correo, ocupacion, passBD)

                                nombreMostrar = if (nombre.isNotEmpty()) {
                                    if (apellido.isNotEmpty()) "$nombre $apellido" else nombre
                                } else {
                                    "Usuario"
                                }
                                break
                            }
                        }
                    }

                    if (accesoValido) {
                        val intent = Intent(this@Login, Menu::class.java)
                        intent.putExtra("usu", usuarioEncontrado)
                        intent.putExtra("NOMBRE_USUARIO", nombreMostrar)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Login, "Error de red con Firebase", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnIrCrear.setOnClickListener {
            val intent = Intent(this, Crear_cuenta::class.java)
            startActivity(intent)
        }
    }

    private fun hashSHA256(texto: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
