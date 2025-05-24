package com.example.slopshop.Entidades

import java.io.Serializable

data class Comentario(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var puntuacion: Float = 0f,
    var uidUsuario: String = "",
    var productId: String = "",
    var timestamp: Long = 0
) : Serializable