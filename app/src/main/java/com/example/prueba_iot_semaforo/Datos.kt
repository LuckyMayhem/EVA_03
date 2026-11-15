package com.example.prueba_iot_semaforo

import java.io.Serializable

class Datos : Serializable {
    var distancia: String = ""
    var estaen_verde: Boolean = false
    var fecha: String = ""
    var hora: String = ""
    var tiempo_res: String = ""

    // Constructor vacío requerido por Firebase
    constructor()

    // Constructor con parámetros que coinciden exactamente con los campos del ESP32 / Firebase
    constructor(distancia: String, estaen_verde: Boolean, fecha: String, hora: String, tiempo_res: String) {
        this.distancia = distancia
        this.estaen_verde = estaen_verde
        this.fecha = fecha
        this.hora = hora
        this.tiempo_res = tiempo_res
    }

    // Formato idéntico a los nombres de campo de la base de datos de Firebase
    override fun toString(): String {
        return """
            distancia: $distancia
            estaen_verde: $estaen_verde
            fecha: $fecha
            hora: $hora
            tiempo_res: $tiempo_res
        """.trimIndent()
    }
}
