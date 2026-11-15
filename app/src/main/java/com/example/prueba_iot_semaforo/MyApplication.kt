package com.example.prueba_iot_semaforo

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Habilita persistencia y almacenamiento local offline en Firebase
        val db = FirebaseDatabase.getInstance()
        db.setPersistenceEnabled(true)

        // Mantiene sincronizadas las nodos principales en almacenamiento local
        db.getReference("Datos").keepSynced(true)
        db.getReference("Usuario").keepSynced(true)
        db.getReference("Estado_ESP32").keepSynced(true)
    }
}
