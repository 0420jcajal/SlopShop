package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.Imagen
import com.example.slopshop.R
import com.example.slopshop.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorImagenSeleccionada (

    private var context: Context,
    private var listaImagenesSeleccionadas : ArrayList<Imagen>
): Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>(){
    private lateinit var binding: ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return listaImagenesSeleccionadas.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo= listaImagenesSeleccionadas[position]

        val imagenUri= modelo.imageUri

        //Leer foto
        try{
            Glide.with(context)
                .load(imagenUri)
                .placeholder(R.drawable.icono_imagen)
                .into(holder.itemImagen)
        }catch (e:Exception){

        }

        //Borrar foto
        holder.borrarItemImagen.setOnClickListener {
            listaImagenesSeleccionadas.remove(modelo)
            notifyDataSetChanged()
        }

    }
    inner class HolderImagenSeleccionada(itemView : View) : ViewHolder(itemView){
        var itemImagen= binding.itemImagen
        var borrarItemImagen= binding.borrarItemImagen
    }


}