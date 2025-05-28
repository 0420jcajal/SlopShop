package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.PedidoDetalle
import com.example.slopshop.R
import com.example.slopshop.databinding.ItemPedidoDetalleBinding

class AdaptadorPedidosDetalle(
    private val context: Context,
    private val listaDetalles: List<PedidoDetalle>,
    private val isAdmin: Boolean = false,
    private val onComprarDeNuevo: (PedidoDetalle) -> Unit = {},
    private val onValorarProducto: (PedidoDetalle) -> Unit = {}
) : RecyclerView.Adapter<AdaptadorPedidosDetalle.HolderDetalle>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDetalle {
        val binding = ItemPedidoDetalleBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return HolderDetalle(binding)
    }

    override fun getItemCount(): Int = listaDetalles.size

    override fun onBindViewHolder(holder: HolderDetalle, position: Int) {
        val detalle = listaDetalles[position]


        if (detalle.imagenProducto.isNotEmpty()) {
            Glide.with(context)
                .load(detalle.imagenProducto)
                .placeholder(R.drawable.icono_producto)
                .into(holder.binding.imagenProducto)
        } else {
            holder.binding.imagenProducto.setImageResource(R.drawable.icono_producto)
        }


        holder.binding.nombreProducto.text = "${detalle.nombreProducto} (${detalle.cantidad})"


        holder.binding.mensajeEntrega.text = "${detalle.precioUnitario} €/Ud"

        if (isAdmin) {
            holder.binding.btnComprarDeNuevo.visibility = View.GONE
            holder.binding.btnValorarProducto.visibility = View.GONE
        } else {
            holder.binding.btnComprarDeNuevo.visibility = View.VISIBLE
            holder.binding.btnValorarProducto.visibility = View.VISIBLE
            holder.binding.btnComprarDeNuevo.setOnClickListener {
                onComprarDeNuevo(detalle)
            }
            holder.binding.btnValorarProducto.setOnClickListener {
                onValorarProducto(detalle)
            }
        }
    }

    inner class HolderDetalle(val binding: ItemPedidoDetalleBinding) :
        RecyclerView.ViewHolder(binding.root)
}
