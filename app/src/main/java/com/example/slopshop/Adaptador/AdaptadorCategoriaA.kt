package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Entidades.Categoria
import com.example.slopshop.databinding.ItemCategoriaABinding

class AdaptadorCategoria(
    private val context: Context,
    private var listaCategorias: ArrayList<Categoria>,
    private val onEliminarClick: (Categoria) -> Unit
) : RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        val binding = ItemCategoriaABinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategoria(binding)
    }

    override fun getItemCount(): Int = listaCategorias.size

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val categoria = listaCategorias[position]
        holder.binding.nombreCategoriaA.text = categoria.categoria

        holder.binding.eliminarCategoria.setOnClickListener{
            onEliminarClick(categoria)
        }
    }

    inner class HolderCategoria(val binding: ItemCategoriaABinding) : RecyclerView.ViewHolder(binding.root)
}