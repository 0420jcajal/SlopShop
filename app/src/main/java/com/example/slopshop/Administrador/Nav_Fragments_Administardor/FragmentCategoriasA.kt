package com.example.slopshop.Administrador.Nav_Fragments_Administardor

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentCategoriasABinding
import com.google.firebase.database.FirebaseDatabase

class FragmentCategoriasA : Fragment() {

    private lateinit var binding: FragmentCategoriasABinding
    private lateinit var mContext: Context
    private lateinit var progressDialog: ProgressDialog

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

        binding.btnAgregarCategoria.setOnClickListener{
            validarCategoria()
        }
        return binding.root
    }

    private var categoria= ""
    private fun validarCategoria() {
        categoria = binding.etCategoria.text.toString().trim()
        if(categoria.isEmpty()){
            Toast.makeText(context, "La categoría introducida se encuentra en blanco", Toast.LENGTH_SHORT).show()
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
                progressDialog.dismiss()
                Toast.makeText(context, "La categoría se ha agregado correctamente", Toast.LENGTH_SHORT).show()
                binding.etCategoria.setText("")
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(context, "Ha habido un fallo al agregar la categoria en la Base de Datos. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}