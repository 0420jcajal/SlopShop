package com.example.slopshop.Entidades

import android.net.Uri

class Imagen {

    var id = ""
    var imageUri : Uri?=null
    var imagenUrl : String ?=null
    var deInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imagenUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imagenUrl = imagenUrl
        this.deInternet = deInternet
    }


}