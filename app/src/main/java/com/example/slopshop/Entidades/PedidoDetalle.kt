package com.example.slopshop.Entidades

import java.io.Serializable

data class PedidoDetalle(
    val idProducto: String = "",
    val nombreProducto: String = "",
    val imagenProducto: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,


):Serializable