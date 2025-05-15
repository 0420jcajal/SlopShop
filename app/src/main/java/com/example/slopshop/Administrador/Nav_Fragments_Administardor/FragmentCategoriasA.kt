package com.example.slopshop.Administrador.Nav_Fragments_Administardor

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentCategoriasABinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FragmentCategoriasA : Fragment() {

    private lateinit var binding: FragmentCategoriasABinding
    private lateinit var mContext: Context
    private lateinit var progressDialog: ProgressDialog
    private var imageUri : Uri?=null

    override fun onAttach(context: Context) {
        mContext=context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriasABinding.inflate(inflater, container, false)

        progressDialog=ProgressDialog(context)
        progressDialog.setTitle("Agregando Categoria, Espere por favor.")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imgAgregarCategoria.setOnClickListener(){
            ImagePicker.with(requireActivity())
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .createIntent { intent ->
                    resultadoImg.launch(intent)
                }
        }

        binding.btnAgregarCategoria.setOnClickListener{
            validarCategoria()
        }
        return binding.root
    }
    private val resultadoImg=
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode== Activity.RESULT_OK){
                val data = resultado.data
                imageUri= data!!.data
                binding.imgAgregarCategoria.setImageURI(imageUri)
            } else {
                Toast.makeText(mContext, "No se ha podido agregar la imagen de categoría", Toast.LENGTH_SHORT).show()
            }
        }

    private var categoria= ""
    private fun validarCategoria() {
        categoria = binding.etCategoria.text.toString().trim()
        if(categoria.isEmpty()){
            Toast.makeText(context, "La categoría introducida se encuentra en blanco", Toast.LENGTH_SHORT).show()
        }else if(imageUri==null){
            Toast.makeText(context, "Por favor, tiene que seleccionar una imagen para la categoría", Toast.LENGTH_SHORT).show()
        }else{
            agregarCategoriaBD()
        }
    }

    private fun agregarCategoriaBD() {

        progressDialog.setMessage("Agregando la categoría")
        progressDialog.show()

        val ref = FirebaseDatabase.getInstance().getReference("Categorías")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "${keyId}"
        hashMap["categoria"]="${categoria}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                
                subirImagenBD(keyId)
                /*
                progressDialog.dismiss()
                Toast.makeText(context, "La categoría se ha agregado correctamente", Toast.LENGTH_SHORT).show()
                binding.etCategoria.setText("")
                 */
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(context, "Ha habido un fallo al agregar la categoria en la Base de Datos. Error: ${e.message}", Toast.LENGTH_SHORT).show()
                //TODO LOG
            }

    }

    private fun subirImagenBD(keyId: String){

        progressDialog.setMessage("Subiendo imagen...")
        progressDialog.show()

        val nombreImagen= keyId
        val nombreCarpeta= "Categorías/$nombreImagen"
        val storageReference = FirebaseStorage.getInstance().getReference(nombreCarpeta)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->
                progressDialog.dismiss()
                val uriTask = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val urlImgCargada = uriTask.result
                if(uriTask.isSuccessful){
                    val hashMap = HashMap<String, Any>()
                    hashMap["imagenUrl"]="$urlImgCargada"
                    val ref = FirebaseDatabase.getInstance().getReference("Categorías")
                    ref.child(nombreImagen).updateChildren(hashMap)
                    Toast.makeText(mContext, "La imagen se ha subido correctamente", Toast.LENGTH_SHORT).show()
                    binding.etCategoria.setText("")
                    imageUri= null
                    binding.imgAgregarCategoria.setImageURI(imageUri)
                    binding.imgAgregarCategoria.setImageResource(R.drawable.ic_categorias)
                }
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(context, " ${e.message}", Toast.LENGTH_SHORT).show()
                //TODO LOG
            }

    }


}