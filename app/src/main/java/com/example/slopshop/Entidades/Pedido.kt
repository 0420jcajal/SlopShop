package com.example.slopshop.Entidades

import java.io.Serializable

data class Pedido(
    var id: String = "",
    var fecha: String = "",
    var entregado: Boolean = false,
    var estado: String = "",
    var imagenProducto: String = "",
    var producto: String = "",
    var productos: Map<String, Int> = emptyMap(),
    var timestamp: Long =0L

): Serializable