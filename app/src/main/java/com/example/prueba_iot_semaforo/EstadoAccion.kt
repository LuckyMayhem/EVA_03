package com.example.prueba_iot_semaforo

import java.io.Serializable

class EstadoAccion : Serializable {
    var sensor_buzzer_activo: Boolean = true
    var accion: String = ""
    var fecha: String = ""
    var hora: String = ""

    constructor()

    constructor(sensor_buzzer_activo: Boolean, accion: String, fecha: String, hora: String) {
        this.sensor_buzzer_activo = sensor_buzzer_activo
        this.accion = accion
        this.fecha = fecha
        this.hora = hora
    }

    override fun toString(): String {
        return """
            Acción: $accion
            Estado Activo: $sensor_buzzer_activo
            Fecha: $fecha
            Hora: $hora
        """.trimIndent()
    }
}