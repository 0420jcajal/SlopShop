package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.R

class AdaptadorImagenProducto(
    private val context: Context,
    private val listaImagenes: List<String>
) : RecyclerView.Adapter<AdaptadorImagenProducto.ImagenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_imagen_producto, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val urlImagen = listaImagenes[position]
        Glide.with(context)
            .load(urlImagen)
            .placeholder(R.drawable.icono_producto)
            .error(R.drawable.icono_producto)
            .into(holder.imgProducto)
    }

    override fun getItemCount(): Int = listaImagenes.size

    class ImagenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imagenProducto)
    }
}
