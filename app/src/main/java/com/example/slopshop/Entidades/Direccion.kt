package com.example.slopshop.Entidades

import java.io.Serializable

data class Direccion(
    var id: String = "",
    val pais: String = "",
    val provincia: String = "",
    val ciudad: String = "",
    val calle: String = ""
):Serializable