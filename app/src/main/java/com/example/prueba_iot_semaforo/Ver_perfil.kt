package com.example.prueba_iot_semaforo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ver_perfil : AppCompatActivity() {

    private lateinit var tvUsuPerfil: TextView
    private lateinit var tvRut: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvOcupacion: TextView
    private lateinit var tvPassword: TextView
    private lateinit var btnVolver: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        tvUsuPerfil = findViewById(R.id.tvusuperfil)
        tvRut = findViewById(R.id.tvrutper)
        tvNombre = findViewById(R.id.tvnomper)
        tvApellido = findViewById(R.id.tvapeper)
        tvCorreo = findViewById(R.id.tvemaper)
        tvOcupacion = findViewById(R.id.tvocuper)
        tvPassword = findViewById(R.id.tvconper)
        btnVolver = findViewById(R.id.btnvolper)

        @Suppress("DEPRECATION")
        val usu = intent.getSerializableExtra("usu") as? Usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        if (usu != null) {
            mostrarUsuario(usu)
        } else if (nombreUsuario.isNotEmpty()) {
            db.getReference("Usuario").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (u in snapshot.children) {
                        val nom = u.child("nombre").value?.toString() ?: ""
                        val ape = u.child("apellido").value?.toString() ?: ""
                        val nomCompleto = "$nom $ape".trim()

                        if (nom.equals(nombreUsuario, ignoreCase = true) || nomCompleto.equals(nombreUsuario, ignoreCase = true)) {
                            val rutInt = u.child("rut").value?.toString()?.toIntOrNull() ?: 0
                            val cor = u.child("correo").value?.toString() ?: ""
                            val ocu = u.child("ocupacion").value?.toString() ?: u.child("cargo").value?.toString() ?: ""
                            val pass = u.child("password").value?.toString()
                                ?: u.child("contraseña").value?.toString()
                                ?: u.child("contrasena").value?.toString()
                                ?: ""

                            val usuarioObj = Usuario(rutInt, nom, ape, cor, ocu, pass)
                            mostrarUsuario(usuarioObj)
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun mostrarUsuario(usuario: Usuario) {
        val nomCompleto = "${usuario.nombre} ${usuario.apellido}".trim()
        tvUsuPerfil.text = if (nomCompleto.isNotEmpty()) nomCompleto else "Usuario"
        tvRut.text = if (usuario.rut != 0) usuario.rut.toString() else "N/A"
        tvNombre.text = if (usuario.nombre.isNotEmpty()) usuario.nombre else "N/A"
        tvApellido.text = if (usuario.apellido.isNotEmpty()) usuario.apellido else "N/A"
        tvCorreo.text = if (usuario.correo.isNotEmpty()) usuario.correo else "N/A"
        tvOcupacion.text = if (usuario.ocupacion.isNotEmpty()) usuario.ocupacion else "N/A"
        tvPassword.text = "•••••••••••• (Protegida)"
    }
}
