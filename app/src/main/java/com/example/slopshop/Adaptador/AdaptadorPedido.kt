package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.Pedido
import com.example.slopshop.R
import com.example.slopshop.databinding.ItemPedidoBinding

class AdaptadorPedido(
    private val context: Context,
    private val listaPedidos: List<Pedido>,
    private val onClick: (Pedido) -> Unit ={}
) : RecyclerView.Adapter<AdaptadorPedido.HolderPedido>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPedido {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return HolderPedido(binding)
    }

    override fun getItemCount(): Int = listaPedidos.size

    override fun onBindViewHolder(holder: HolderPedido, position: Int) {
        val pedido = listaPedidos[position]

        if (pedido.imagenProducto.isNotEmpty()) {
            Glide.with(context)
                .load(pedido.imagenProducto)
                .placeholder(R.drawable.icono_producto)
                .into(holder.binding.imagenProducto)
        } else {
            holder.binding.imagenProducto.setImageResource(R.drawable.icono_producto)
        }

        holder.binding.nombreProducto.text = pedido.producto

        holder.binding.mensajeEntrega.text = if (pedido.entregado) {
            "Entregado el ${pedido.fecha}"
        } else {
            pedido.estado
        }

        holder.binding.tvPrecioProducto.setOnClickListener {
            onClick(pedido)
        }
    }

    inner class HolderPedido(val binding: ItemPedidoBinding) :
        RecyclerView.ViewHolder(binding.root)
}