package com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.slopshop.Adaptador.AdaptadorProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentCatalogoUBinding
import com.example.slopshop.databinding.FragmentProductosABinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentCatalogoU : Fragment() {


    private lateinit var binding : FragmentCatalogoUBinding
    private lateinit var mContext : Context
    private lateinit var listaProductos : ArrayList<Producto>
    private lateinit var adaptadorProducto: AdaptadorProducto

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCatalogoUBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarProductos()
    }

    private fun listarProductos() {
        listaProductos = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (uri in snapshot.children){
                    val modeloProducto = uri.getValue(Producto::class.java)
                    listaProductos.add(modeloProducto!!)
                }
                adaptadorProducto = AdaptadorProducto(mContext, listaProductos)
                binding.productosRV.adapter = adaptadorProducto
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}