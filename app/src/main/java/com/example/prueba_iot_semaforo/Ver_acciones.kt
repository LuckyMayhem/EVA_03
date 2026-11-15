package com.example.prueba_iot_semaforo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class Ver_acciones : AppCompatActivity() {

    private lateinit var tvusuacciones: TextView
    private lateinit var lvAcciones: ListView
    private lateinit var btnRefrescar: Button
    private lateinit var btnvolacc: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_acciones)

        tvusuacciones = findViewById(R.id.tvusuacciones)
        lvAcciones = findViewById(R.id.lvAcciones)
        btnRefrescar = findViewById(R.id.btnRefrescar)
        btnvolacc = findViewById(R.id.btnvolacc)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (!nombreUsuario.isNullOrEmpty()) {
            tvusuacciones.text = nombreUsuario
        }

        listarDatos()

        btnRefrescar.setOnClickListener {
            listarDatos()
            Toast.makeText(this, "Datos refrescados", Toast.LENGTH_SHORT).show()
        }

        btnvolacc.setOnClickListener {
            finish()
        }
    }

    private fun listarDatos() {
        val refDatos = db.getReference("Datos")
        val listaDatos = ArrayList<Datos>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDatos)
        lvAcciones.adapter = adapter

        refDatos.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaDatos.clear()
                for (nodo in snapshot.children) {
                    val dato = nodo.getValue(Datos::class.java)
                    if (dato != null) {
                        listaDatos.add(dato)
                    } else {
                        val distancia = nodo.child("distancia").value?.toString() ?: ""
                        val estaenVerdeVal = nodo.child("estaen_verde").value
                        val estaenVerde = when (estaenVerdeVal) {
                            is Boolean -> estaenVerdeVal
                            is String -> estaenVerdeVal.toBoolean()
                            else -> false
                        }
                        val fecha = nodo.child("fecha").value?.toString() ?: ""
                        val hora = nodo.child("hora").value?.toString() ?: ""
                        val tiempoRes = nodo.child("tiempo_res").value?.toString() ?: ""

                        listaDatos.add(Datos(distancia, estaenVerde, fecha, hora, tiempoRes))
                    }
                }

                if (listaDatos.isEmpty()) {
                    Toast.makeText(this@Ver_acciones, "No hay registros en 'Datos'", Toast.LENGTH_SHORT).show()
                } else {
                    Collections.reverse(listaDatos)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Ver_acciones, "Error de sincronización con la base de datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
