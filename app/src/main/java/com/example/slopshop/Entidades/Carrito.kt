package com.example.slopshop.Entidades

import java.io.Serializable
data class Carrito(

    val id_producto: String = "",
    val nombre: String = "",
    val cantidad: Int = 1,
    val precio_unitario: Double = 0.0

): Serializable