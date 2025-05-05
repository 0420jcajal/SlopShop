package com.example.slopshop.Administrador.Productos

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Adaptador.AdaptadorImagenSeleccionada
import com.example.slopshop.Constantes
import com.example.slopshop.Entidades.Categoria
import com.example.slopshop.Entidades.Imagen


import com.example.slopshop.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityAgregarProductoBinding
    private var imagenUri: Uri?=null

    private lateinit var listaImagenesSeleccionadas: ArrayList<Imagen>
    private lateinit var adaptadorImagenSeleccionada: AdaptadorImagenSeleccionada

    private lateinit var categoriasArrayList: ArrayList<Categoria>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategoría()

        listaImagenesSeleccionadas= ArrayList()


        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImagen()
        }

        binding.categoria.setOnClickListener{
            seleccionarCategorias()
        }

        cargarImagenes()
    }
    private var idCategoria= ""
    private var nombreCategoria=""
    private fun seleccionarCategorias(){
        val listaCategorias= arrayOfNulls<String>(categoriasArrayList.size)
        for(i in listaCategorias.indices){
            listaCategorias[i]=categoriasArrayList[i].categoria
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una categoría")
            .setItems(listaCategorias){dialog, witch->
                idCategoria = categoriasArrayList[witch].id
                nombreCategoria = categoriasArrayList[witch].categoria
                binding.categoria.text= nombreCategoria
            }
            .show()
    }

    private fun cargarCategoría() {
        categoriasArrayList= ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorías").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(Categoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO LOG ERROR
            }
        })
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