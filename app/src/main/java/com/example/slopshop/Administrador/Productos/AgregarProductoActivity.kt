package com.example.slopshop.Administrador.Productos

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.view.View
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
import com.google.firebase.storage.FirebaseStorage

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityAgregarProductoBinding
    private var imagenUri: Uri?=null

    private lateinit var listaImagenesSeleccionadas: ArrayList<Imagen>
    private lateinit var adaptadorImagenSeleccionada: AdaptadorImagenSeleccionada

    private lateinit var categoriasArrayList: ArrayList<Categoria>
    private  lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategoría()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.etPrecioConDescuentoProducto.visibility= View.GONE
        binding.ejemploDescuento.visibility= View.GONE

        binding.decuentoSwitch.setOnCheckedChangeListener{buttonView, isChecked->
            if(isChecked){
                binding.etPrecioConDescuentoProducto.visibility= View.VISIBLE
                binding.ejemploDescuento.visibility= View.VISIBLE
            }else{
                binding.etPrecioConDescuentoProducto.visibility= View.GONE
                binding.ejemploDescuento.visibility= View.GONE
            }

        }

        listaImagenesSeleccionadas= ArrayList()


        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImagen()
        }

        binding.categoria.setOnClickListener{
            seleccionarCategorias()
        }

        binding.btnAgregarProducto.setOnClickListener{
            validarInfo()
        }

        cargarImagenes()
    }

    private var nombreProducto= ""
    private var descripcionProducto= ""
    private var categoriaProducto =""
    private var precioProducto = ""
    private var tieneDescuentoProducto = false
    private var precioDescuentoProducto = ""
    private var ejemploDescuento= ""
    private fun validarInfo() {
        nombreProducto= binding.etNombreProducto.text.toString().trim()
        descripcionProducto = binding.etDescripcionProducto.text.toString().trim()
        categoriaProducto = binding.categoria.text.toString().trim()
        precioProducto = binding.etPrecioProducto.text.toString().trim()
        tieneDescuentoProducto = binding.decuentoSwitch.isChecked

        if (nombreProducto.isEmpty()){
            binding.etNombreProducto.error = "Introduce un nombre para el producto"
            binding.etNombreProducto.requestFocus()
        }
        else if (descripcionProducto.isEmpty()){
            binding.etDescripcionProducto.error = "Introduce la descripción del producto"
            binding.etDescripcionProducto.requestFocus()
        }
        else if (categoriaProducto.isEmpty()){
            binding.categoria.error = "Introduce una categoria para el producto"
            binding.categoria.requestFocus()
        }
        else if (precioProducto.isEmpty()){
            binding.etPrecioProducto.error = "Introduce un precio para el producto"
            binding.etPrecioProducto.requestFocus()
        }
        else if(imagenUri==null){
            Toast.makeText(this, "Seleccione al menos una imagen para el producto", Toast.LENGTH_SHORT).show()
        }else{
            if(tieneDescuentoProducto){
                precioDescuentoProducto= binding.etPrecioConDescuentoProducto.text.toString().trim()
                ejemploDescuento= binding.ejemploDescuento.text.toString().trim()
                if(precioDescuentoProducto.isEmpty()){
                    binding.etPrecioConDescuentoProducto.error = "Introduce un precio con descuento para el producto"
                    binding.etPrecioConDescuentoProducto.requestFocus()
                }
                else if(ejemploDescuento.isEmpty()){
                    binding.ejemploDescuento.error = "Introduce un precio con descuento para el producto"
                    binding.ejemploDescuento.requestFocus()
                }else{
                    agregarProducto()
                }
            }else{
                precioDescuentoProducto = "0"
                ejemploDescuento= ""
                agregarProducto()
            }
        }
    }

    private fun agregarProducto() {
        progressDialog.setMessage("Agregando el producto a la base de datos")
        progressDialog.show()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        val keyFireBaseId= ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "${keyFireBaseId}"
        hashMap["nombre"]= "${nombreProducto}"
        hashMap["descripcion"]= "${descripcionProducto}"
        hashMap["categoria"]= "${categoriaProducto}"
        hashMap["precio"]= "${precioProducto}"
        hashMap["precioDescuento"]= "${precioDescuentoProducto}"
        hashMap["ejemploDescuento"]= "${ejemploDescuento}"

        ref.child(keyFireBaseId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImagenesProducto(keyFireBaseId)
            }
            .addOnFailureListener {e ->
                Toast.makeText(this, "Error al subir el producto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenesProducto(keyFireBaseId: String){

        for(i in listaImagenesSeleccionadas.indices){
            val modeloImagenSeleccionada = listaImagenesSeleccionadas[i]
            val nombreImagen = modeloImagenSeleccionada.id
            val rutaImagen = "Productos/$nombreImagen"

            val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
            storageRef.putFile(modeloImagenSeleccionada.imageUri!!)
                .addOnSuccessListener {taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while(!uriTask.isSuccessful);
                    val urlImagenCargada = uriTask.result

                    if(uriTask.isSuccessful){
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"]="${modeloImagenSeleccionada.id}"
                        hashMap["imagenUrl"]="${urlImagenCargada}"

                        val ref = FirebaseDatabase.getInstance().getReference("Productos")
                        ref.child(keyFireBaseId).child("Imagenes Producto")
                            .child(nombreImagen)
                            .updateChildren(hashMap)

                        progressDialog.dismiss()
                        Toast.makeText(this, "El producto se ha agregado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarUI()
                    }

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al subir imagen del producto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limpiarUI() {
        listaImagenesSeleccionadas.clear()
        adaptadorImagenSeleccionada.notifyDataSetChanged()
        binding.etNombreProducto.setText("")
        binding.etDescripcionProducto.setText("")
        binding.etPrecioProducto.setText("")
        binding.categoria.setText("")
        binding.decuentoSwitch.isChecked= false
        binding.etPrecioConDescuentoProducto.setText("")
        binding.ejemploDescuento.setText("")
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