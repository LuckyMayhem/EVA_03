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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManipularEsp32 : AppCompatActivity() {

    private lateinit var tvusumanipular: TextView
    private lateinit var tvEstadoActual: TextView
    private lateinit var btnControlSensorBuzzer: Button
    private lateinit var btnVolverManipular: Button

    private val db = FirebaseDatabase.getInstance()
    private val estadoRef = db.getReference("Estado_ESP32")
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

        estadoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sistemaActivo = snapshot.child("sensor_buzzer_activo").getValue(Boolean::class.java) ?: true
                actualizarVistas()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManipularEsp32, "Error al sincronizar con Firebase", Toast.LENGTH_SHORT).show()
            }
        })

        btnControlSensorBuzzer.setOnClickListener {
            val nuevoEstado = !sistemaActivo
            val accionTexto = if (nuevoEstado) "Sensor y Buzzer Encendidos" else "Apagón: Ahorro de Energía (Sensor y Buzzer OFF)"
            val fechaActual = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val mapaEstado = HashMap<String, Any>()
            mapaEstado["sensor_buzzer_activo"] = nuevoEstado
            mapaEstado["accion"] = accionTexto
            mapaEstado["fecha"] = fechaActual
            mapaEstado["hora"] = horaActual

            estadoRef.updateChildren(mapaEstado).addOnSuccessListener {
                db.getReference("Acciones_Historial").push().setValue(mapaEstado)
                val toastMsg = if (nuevoEstado) "Sistema Activado" else "¡Modo Ahorro y Apagón Enviado!"
                Toast.makeText(this@ManipularEsp32, toastMsg, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@ManipularEsp32, "Fallo al enviar instrucción", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverManipular.setOnClickListener {
            finish()
        }
    }

    private fun actualizarVistas() {
        if (sistemaActivo) {
            tvEstadoActual.text = "Estado: EN SERVICIO (Activo)"
            tvEstadoActual.setTextColor(Color.parseColor("#2E7D32"))
            btnControlSensorBuzzer.text = "Apagar Sensor y Buzzer"
            btnControlSensorBuzzer.setBackgroundColor(Color.parseColor("#D32F2F"))
        } else {
            tvEstadoActual.text = "Estado: MODO AHORRO (Apagado)"
            tvEstadoActual.setTextColor(Color.parseColor("#C62828"))
            btnControlSensorBuzzer.text = "Encender Sensor y Buzzer"
            btnControlSensorBuzzer.setBackgroundColor(Color.parseColor("#388E3C"))
        }
    }
}