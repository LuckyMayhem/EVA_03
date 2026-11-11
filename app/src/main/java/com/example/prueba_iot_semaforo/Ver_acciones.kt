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

        listarAcciones()

        btnRefrescar.setOnClickListener {
            listarAcciones()
            Toast.makeText(this, "Acciones refrescadas", Toast.LENGTH_SHORT).show()
        }

        btnvolacc.setOnClickListener {
            finish()
        }
    }

    private fun listarAcciones() {
        val refHistorial = db.getReference("Acciones_Historial")
        val listaAcciones = ArrayList<EstadoAccion>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAcciones)
        lvAcciones.adapter = adapter

        refHistorial.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAcciones.clear()
                for (nodo in snapshot.children) {
                    val activo = nodo.child("sensor_buzzer_activo").getValue(Boolean::class.java) ?: false
                    val accion = nodo.child("accion").value?.toString() ?: "Acción sin detalle"
                    val fecha = nodo.child("fecha").value?.toString() ?: ""
                    val hora = nodo.child("hora").value?.toString() ?: ""

                    listaAcciones.add(EstadoAccion(activo, accion, fecha, hora))
                }

                if (listaAcciones.isEmpty()) {
                    db.getReference("Estado_ESP32").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(sActual: DataSnapshot) {
                            val activo = sActual.child("sensor_buzzer_activo").getValue(Boolean::class.java) ?: true
                            val accion = sActual.child("accion").value?.toString() ?: "Estado Inicial"
                            val fecha = sActual.child("fecha").value?.toString() ?: ""
                            val hora = sActual.child("hora").value?.toString() ?: ""
                            listaAcciones.add(EstadoAccion(activo, accion, fecha, hora))
                            lvAcciones.adapter = adapter
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    Collections.reverse(listaAcciones)
                    lvAcciones.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Ver_acciones, "Error al leer acciones", Toast.LENGTH_SHORT).show()
            }
        })
    }
}