package com.example.prueba_iot_semaforo

import java.io.Serializable

class EstadisticaItem : Serializable {
    var titulo: String = ""
    var valor: String = ""

    constructor()

    constructor(titulo: String, valor: String) {
        this.titulo = titulo
        this.valor = valor
    }

    override fun toString(): String {
        return "$titulo\n$valor"
    }
}