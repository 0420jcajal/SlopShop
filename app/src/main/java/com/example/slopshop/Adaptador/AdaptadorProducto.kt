package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.R
import com.example.slopshop.databinding.ItemProductoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdaptadorProducto : RecyclerView.Adapter<AdaptadorProducto.HolderProducto> {

    private lateinit var  binding : ItemProductoBinding
    private var mContext : Context
    private var listaProductos : ArrayList<Producto>

    constructor(mContext: Context, listaProductos: ArrayList<Producto>) {
        this.mContext = mContext
        this.listaProductos = listaProductos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderProducto(binding.root)
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = listaProductos[position]

        val nombre = modeloProducto.nombre
        val precio = modeloProducto.precio
        val precioDescuento = modeloProducto.precioDescuento
        val ejemploDescuento = modeloProducto.ejemploDescuento

        cargarPrimeraImagen(modeloProducto, holder)

        holder.nombreProducto.text="${nombre}"
        holder.precioProducto.text="${precio}${" EUR"}"
        holder.precioDescuento.text="${precioDescuento}"
        holder.ejemploDescuento.text="${ejemploDescuento}"

        if(precioDescuento.isNotEmpty() && ejemploDescuento.isNotEmpty()){
            mostrarDescuento(holder)
        }

    }

    private fun mostrarDescuento(holder: AdaptadorProducto.HolderProducto) {

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(producto in snapshot.children){
                    val ejemploDescuento="${producto.child("ejemploProducto").value}"
                    val precioDescuento="${producto.child("precioDescuento").value}"

                    if(precioDescuento.isNotEmpty() && ejemploDescuento.isNotEmpty()){
                        holder.precioDescuento.text="${precioDescuento}${" EUR"}"
                        holder.ejemploDescuento.text="${ejemploDescuento}"

                        holder.ejemploDescuento.visibility=View.VISIBLE
                        holder.precioDescuento.visibility=View.VISIBLE

                    } else{
                        //TODO LOG
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }

        })
    }

    private fun cargarPrimeraImagen(modeloProducto: Producto, holder: AdaptadorProducto.HolderProducto) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes Producto")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (uri in snapshot.children){
                        val imagenUrl = "${uri.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.icono_producto)
                                .into(holder.imagenProducto)
                        }catch (e:Exception){
                            System.out.print("No se ha podido cargar: Error: ${e}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    inner class HolderProducto(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagenProducto = binding.imagenProducto
        var nombreProducto = binding.nombreProducto
        var precioProducto = binding.precioProducto
        var precioDescuento = binding.precioDescuento
        var ejemploDescuento = binding.ejemploDescuento
    }


}