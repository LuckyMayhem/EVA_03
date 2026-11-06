package com.example.prueba_iot_semaforo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class Ver_acciones : AppCompatActivity() {

    private lateinit var lvAcciones: ListView
    private lateinit var btnRefrescar: Button
    private lateinit var btnVolver: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_acciones)

        lvAcciones = findViewById(R.id.lvAcciones)
        btnRefrescar = findViewById(R.id.btnRefrescar)
        btnVolver = findViewById(R.id.btnvolacc)

        listar_datos()

        btnRefrescar.setOnClickListener {
            listar_datos()
            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun listar_datos() {
        val dbref = db.getReference("Datos")
        val listaDatos = ArrayList<Datos>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDatos)
        lvAcciones.adapter = adapter

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaDatos.clear()
                for (nodo in snapshot.children) {
                    val distancia = nodo.child("distancia").value?.toString() ?: "0"
                    val estaenVerde = nodo.child("estaen_verde").getValue(Boolean::class.java) ?: false
                    val tiempoRes = nodo.child("tiempo_res").value?.toString() ?: "0"
                    val fecha = nodo.child("fecha").value?.toString() ?: ""

                    val registro = Datos(distancia, estaenVerde, tiempoRes, fecha)
                    listaDatos.add(registro)
                }
                // Registros más recientes primero
                Collections.reverse(listaDatos)
                lvAcciones.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Ver_acciones, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}