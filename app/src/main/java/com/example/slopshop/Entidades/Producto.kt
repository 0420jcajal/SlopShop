package com.example.slopshop.Entidades

class Producto {

    var id : String = ""
    var nombre : String = ""
    var descipcion : String = ""
    var categoria : String = ""
    var precio : String = ""
    var precioDescuento : String = ""
    var ejemploDescuento : String = ""

    constructor()
    constructor(
        id: String,
        nombre: String,
        descipcion: String,
        categoria: String,
        precio: String,
        precioDescuento: String,
        ejemploDescuento: String
    ) {
        this.id = id
        this.nombre = nombre
        this.descipcion = descipcion
        this.categoria = categoria
        this.precio = precio
        this.precioDescuento = precioDescuento
        this.ejemploDescuento = ejemploDescuento
    }
}