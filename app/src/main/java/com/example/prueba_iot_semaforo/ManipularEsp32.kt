package com.example.prueba_iot_semaforo

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManipularEsp32 : AppCompatActivity() {

    private lateinit var tvusumanipular: TextView
    private lateinit var tvEstadoActual: TextView
    private lateinit var btnControlSensorBuzzer: Button
    private lateinit var btnVolverManipular: Button

    private val db = FirebaseDatabase.getInstance()
    private val estadoRef = db.getReference("Estado_ESP32/sensor_buzzer_activo")
    private var sistemaActivo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_esp32)

        tvusumanipular = findViewById(R.id.tvusumanipular)
        tvEstadoActual = findViewById(R.id.tvEstadoActual)
        btnControlSensorBuzzer = findViewById(R.id.btnControlSensorBuzzer)
        btnVolverManipular = findViewById(R.id.btnVolverManipular)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (!nombreUsuario.isNullOrEmpty()) {
            tvusumanipular.text = nombreUsuario
        }

        // Monitoreo en tiempo real del estado en Firebase
        estadoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sistemaActivo = snapshot.getValue(Boolean::class.java) ?: true
                actualizarVistas()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManipularEsp32, "Error de sincronización con Firebase", Toast.LENGTH_SHORT).show()
            }
        })

        btnControlSensorBuzzer.setOnClickListener {
            val nuevoEstado = !sistemaActivo
            estadoRef.setValue(nuevoEstado).addOnSuccessListener {
                val mensaje = if (nuevoEstado) "Sensor y Buzzer ACTIVADOS" else "Sensor y Buzzer DESACTIVADOS"
                Toast.makeText(this@ManipularEsp32, mensaje, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@ManipularEsp32, "Error al enviar instrucción", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverManipular.setOnClickListener {
            finish()
        }
    }

    private fun actualizarVistas() {
        if (sistemaActivo) {
            tvEstadoActual.text = "Estado: ACTIVADO"
            tvEstadoActual.setTextColor(Color.parseColor("#2E7D32")) // Verde oscuro
            btnControlSensorBuzzer.text = "Desactivar Sensor y Buzzer"
            btnControlSensorBuzzer.setBackgroundColor(Color.parseColor("#D32F2F")) // Rojo
        } else {
            tvEstadoActual.text = "Estado: DESACTIVADO"
            tvEstadoActual.setTextColor(Color.parseColor("#C62828")) // Rojo oscuro
            btnControlSensorBuzzer.text = "Activar Sensor y Buzzer"
            btnControlSensorBuzzer.setBackgroundColor(Color.parseColor("#388E3C")) // Verde
        }
    }
}