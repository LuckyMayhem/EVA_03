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

        etCorreo = findViewById(R.id.etcorlogin)
        etPassword = findViewById(R.id.etconlogin)
        btnIniciar = findViewById(R.id.btniniciarsesion)
        btnIrCrear = findViewById(R.id.btncrearlogin)

        btnIniciar.setOnClickListener {
            val correoIngresado = etCorreo.text.toString().trim()
            val passPlano = etPassword.text.toString().trim()

            if (correoIngresado.isEmpty() || passPlano.isEmpty()) {
                Toast.makeText(this, "Ingresa tus credenciales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passHash = hashSHA256(passPlano)

            db.getReference("Usuario").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var accesoValido = false
                    var nombreUsuario = ""

                    for (u in snapshot.children) {
                        val correoBD = u.child("correo").value?.toString() ?: ""
                        val passBD = u.child("password").value?.toString() ?: ""

                        if (correoBD.equals(correoIngresado, ignoreCase = true) && passBD == passHash) {
                            accesoValido = true
                            nombreUsuario = u.child("nombre").value?.toString() ?: "Usuario"
                            break
                        }
                    }

                    if (accesoValido) {
                        val intent = Intent(this@Login, Menu::class.java)
                        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
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