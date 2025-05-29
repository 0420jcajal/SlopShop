package com.example.slopshop.Entidades

import java.io.Serializable

data class ProductoValoracion(

    val id: String ="",
    val nombreProducto: String,
    val imagenUrl: String,
    var valoracionPromedio: Float = 0f,
    val uidVendedor: String = ""

):Serializable{
    constructor() : this("", "", "", 0f, "")
}