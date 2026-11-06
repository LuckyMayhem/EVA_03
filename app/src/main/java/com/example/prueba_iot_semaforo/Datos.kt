package com.example.prueba_iot_semaforo

import java.io.Serializable

class Datos : Serializable {
    var distancia: String = ""
    var estaen_verde: Boolean = false
    var tiempo_res: String = ""
    var fecha: String = ""

    // Constructor vacío requerido por Firebase
    constructor()

    // Constructor con parámetros
    constructor(distancia: String, estaen_verde: Boolean, tiempo_res: String, fecha: String) {
        this.distancia = distancia
        this.estaen_verde = estaen_verde
        this.tiempo_res = tiempo_res
        this.fecha = fecha
    }

    // Formato con el que se mostrará en el ListView de Ver_acciones
    override fun toString(): String {
        return """
            Distancia: $distancia
            Estaen_verde: $estaen_verde
            Tiempo_res: $tiempo_res
            Fecha: $fecha
        """.trimIndent()
    }
}