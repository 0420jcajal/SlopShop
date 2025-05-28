package com.example.slopshop.Entidades

import java.io.Serializable

class Producto : Serializable{

    var id : String = ""
    var nombre : String = ""
    var descripcion : String = ""
    var categoria : String = ""
    var precio : String = ""
    var precioDescuento : String = ""
    var ejemploDescuento : String = ""
    var imagenes: ArrayList<String>? = null
    var borrado: Boolean = false
    var mediaPuntuacion: Float = 0f
    var uidUsuario : String = ""


    constructor()
    constructor(
        id: String,
        nombre: String,
        descripcion: String,
        categoria: String,
        precio: String,
        precioDescuento: String,
        ejemploDescuento: String,
        imagenes: ArrayList<String>? = null,
        uidUsuario : String
    ) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.categoria = categoria
        this.precio = precio
        this.precioDescuento = precioDescuento
        this.ejemploDescuento = ejemploDescuento
        this.imagenes = imagenes
        this.uidUsuario=uidUsuario
    }
}