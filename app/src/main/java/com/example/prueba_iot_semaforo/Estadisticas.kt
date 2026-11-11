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

class Estadisticas : AppCompatActivity() {

    private lateinit var tvusuestadisticas: TextView
    private lateinit var lvEstadisticas: ListView
    private lateinit var btnRefrescarEst: Button
    private lateinit var btnVolverEst: Button

    private val db = FirebaseDatabase.getInstance()
    private val listaItems = ArrayList<EstadisticaItem>()
    private lateinit var adapter: ArrayAdapter<EstadisticaItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        tvusuestadisticas = findViewById(R.id.tvusuestadisticas)
        lvEstadisticas = findViewById(R.id.lvEstadisticas)
        btnRefrescarEst = findViewById(R.id.btnRefrescarEst)
        btnVolverEst = findViewById(R.id.btnVolverEst)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaItems)
        lvEstadisticas.adapter = adapter

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (!nombreUsuario.isNullOrEmpty()) {
            tvusuestadisticas.text = nombreUsuario
        }

        cargarEstadisticas()

        btnRefrescarEst.setOnClickListener {
            cargarEstadisticas()
            Toast.makeText(this, "Estadísticas actualizadas", Toast.LENGTH_SHORT).show()
        }

        btnVolverEst.setOnClickListener {
            finish()
        }
    }

    private fun cargarEstadisticas() {
        val refEstadisticas = db.getReference("Estadisticas")

        refEstadisticas.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaItems.clear()

                if (snapshot.exists()) {
                    val personas = snapshot.child("personas_detectadas").value?.toString() ?: "0"
                    val promDistancia = snapshot.child("promedio_distancia").value?.toString() ?: "0.0"
                    val promTiempoDet = snapshot.child("promedio_tiempo_detectado").value?.toString() ?: "0.0"
                    val promTiempoNoDet = snapshot.child("promedio_tiempo_no_detectado").value?.toString() ?: "0.0"
                    val ultimaAct = snapshot.child("ultima_actualizacion").value?.toString() ?: "Sin registro"

                    listaItems.add(EstadisticaItem("• Personas Detectadas en el área (<= 10cm):", "$personas personas"))
                    listaItems.add(EstadisticaItem("• Promedio de Distancia en general:", "$promDistancia cm"))
                    listaItems.add(EstadisticaItem("• Promedio tiempo en rango (<= 10cm):", "$promTiempoDet segundos"))
                    listaItems.add(EstadisticaItem("• Promedio tiempo sin detección:", "$promTiempoNoDet segundos"))
                    listaItems.add(EstadisticaItem("Última actualización:", ultimaAct))
                } else {
                    listaItems.add(EstadisticaItem("Sin datos", "Aún no se registran estadísticas en Firebase."))
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Estadisticas, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
