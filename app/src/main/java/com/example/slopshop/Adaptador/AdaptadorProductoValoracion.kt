package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.ProductoValoracion
import com.example.slopshop.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProductoValoracion(
    private val context: Context,
    private val listaProductos: ArrayList<ProductoValoracion>,
    private val onItemClick: (ProductoValoracion) -> Unit= {}
) : RecyclerView.Adapter<AdaptadorProductoValoracion.HolderProducto>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        val view = LayoutInflater.from(context).inflate(R.layout.item_producto_valoracion, parent, false)
        return HolderProducto(view)
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val producto = listaProductos[position]

        holder.nombreProducto.text = producto.nombreProducto
        Glide.with(context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.icono_producto)
            .into(holder.imagenProducto)

        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }


        val refComentarios = FirebaseDatabase.getInstance()
            .getReference("Valoraciones")
            .orderByChild("productId")
            .equalTo(producto.id)

        refComentarios.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val puntuaciones = snapshot.children.mapNotNull {
                    it.child("puntuacion").getValue(Float::class.java)
                }

                val promedio = if (puntuaciones.isNotEmpty()) {
                    puntuaciones.average().toFloat()
                } else 0f

                producto.valoracionPromedio = promedio

                holder.ratingBar.rating = promedio
                holder.valoracion.text = String.format("%.1f", promedio)
            }

            override fun onCancelled(error: DatabaseError) {
                holder.ratingBar.rating = 0f
                holder.valoracion.text = "0.0"
            }
        })
    }

    override fun getItemCount(): Int = listaProductos.size

    inner class HolderProducto(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagenProducto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreProducto)
        val valoracion: TextView = itemView.findViewById(R.id.valoracionProducto)
        val ratingBar: RatingBar = itemView.findViewById(R.id.estrellasComentario)
    }
}