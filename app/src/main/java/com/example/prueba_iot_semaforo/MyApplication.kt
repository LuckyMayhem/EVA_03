package com.example.prueba_iot_semaforo

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Habilita almacenamiento local en caché cuando no hay conexión a internet
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}