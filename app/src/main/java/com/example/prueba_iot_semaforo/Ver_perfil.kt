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

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvPassword: TextView
    private lateinit var btnVolver: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        tvNombre = findViewById(R.id.tvnomperfil)
        tvCorreo = findViewById(R.id.tvcorperfil)
        tvPassword = findViewById(R.id.tvconperfil)
        btnVolver = findViewById(R.id.btnvolperfil)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        db.getReference("Usuario").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (u in snapshot.children) {
                    val nom = u.child("nombre").value?.toString() ?: ""
                    if (nom.equals(nombreUsuario, ignoreCase = true)) {
                        val correo = u.child("correo").value?.toString() ?: ""
                        tvNombre.text = nom
                        tvCorreo.text = correo
                        tvPassword.text = "•••••••••••• (Cifrada SHA-256)"
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnVolver.setOnClickListener {
            finish()
        }
    }
}