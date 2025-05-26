package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Entidades.Direccion
import com.example.slopshop.databinding.ItemDireccionBinding
import com.google.firebase.database.FirebaseDatabase

class AdaptadorDirecciones(
    private val context: Context,
    private var listaDirecciones: ArrayList<Direccion>,
    private val onItemClick: (Direccion, Int) -> Unit,
    private val onEliminarClick: (Direccion, Int) -> Unit
) : RecyclerView.Adapter<AdaptadorDirecciones.HolderDireccion>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDireccion {
        val binding = ItemDireccionBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderDireccion(binding)
    }

    override fun onBindViewHolder(holder: HolderDireccion, position: Int) {
        val direccion = listaDirecciones[position]

        holder.binding.tvPais.text = direccion.pais
        holder.binding.tvProvinciaCiudad.text = "${direccion.provincia}, ${direccion.ciudad}"
        holder.binding.tvCalle.text = direccion.calle

        holder.binding.root.setOnClickListener {
            onItemClick(direccion, position)
        }

        holder.binding.btnEliminar.setOnClickListener {
            eliminarDireccionFirebase(direccion.id) {
                onEliminarClick(direccion, position)
            }
        }
    }

    override fun getItemCount(): Int = listaDirecciones.size

    private fun eliminarDireccionFirebase(idDireccion: String, onComplete: () -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Direcciones").child(idDireccion)
        ref.removeValue().addOnSuccessListener {
            onComplete()
        }
    }

    inner class HolderDireccion(val binding: ItemDireccionBinding) : RecyclerView.ViewHolder(binding.root)
}
