package com.example.slopshop.Administrador.Productos

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Adaptador.AdaptadorImagenSeleccionada
import com.example.slopshop.Constantes
import com.example.slopshop.Entidades.Imagen


import com.example.slopshop.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityAgregarProductoBinding
    private var imagenUri: Uri?=null

    private lateinit var listaImagenesSeleccionadas: ArrayList<Imagen>
    private lateinit var adaptadorImagenSeleccionada: AdaptadorImagenSeleccionada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaImagenesSeleccionadas= ArrayList()


        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImagen()
        }

        cargarImagenes()
    }

    private fun cargarImagenes() {
        adaptadorImagenSeleccionada= AdaptadorImagenSeleccionada(this, listaImagenesSeleccionadas)
        binding.RVImagenesProducto.adapter=adaptadorImagenSeleccionada
    }

    private fun seleccionarImagen(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                resultadoImagen.launch(intent)
            }
    }
    private val resultadoImagen=
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                val tiempo= "${Constantes().obtenerTiempo()}"
                val modeloImagenSeleccionada = Imagen(tiempo, imagenUri, null, false)
                listaImagenesSeleccionadas.add(modeloImagenSeleccionada)
                cargarImagenes()
            }else{
                Toast.makeText(this, "No se ha cargado ninguna imagen", Toast.LENGTH_SHORT).show()
            }

        }

}