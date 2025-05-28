package com.example.slopshop.Adaptador

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slopshop.Entidades.Carrito
import com.example.slopshop.R
import com.example.slopshop.databinding.ItemCarritoProductoBinding
import com.google.firebase.database.*

class AdaptadorCarrito(
    private val context: Context,
    private var carritoItems: ArrayList<Carrito>
) : RecyclerView.Adapter<AdaptadorCarrito.HolderCarrito>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCarrito {
        val binding = ItemCarritoProductoBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCarrito(binding)
    }

    override fun onBindViewHolder(holder: HolderCarrito, position: Int) {
        val item = carritoItems[position]

        holder.binding.nombreProducto.text = item.nombre
        holder.binding.etQuantity.setText(item.cantidad.toString())
        holder.binding.tvPrecioProducto.text = String.format("%.2f €/Ud", item.precio_unitario)

        actualizarIcono(holder,item.cantidad)


        cargarPrimeraImagen(item.id_producto, holder)


        holder.binding.btnSumar.setOnClickListener {
            val nuevaCantidad = item.cantidad + 1
            actualizarCantidadFirebase(item.id_producto, nuevaCantidad)
            actualizarIcono(holder, nuevaCantidad)
        }

        holder.binding.btnRestar.setOnClickListener {
            val nuevaCantidad = if (item.cantidad > 1) item.cantidad - 1 else 1
            actualizarCantidadFirebase(item.id_producto, nuevaCantidad)
            actualizarIcono(holder, nuevaCantidad)
        }

        holder.binding.btnEliminar.setOnClickListener {
            holder.binding.btnEliminar.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de que deseas eliminar \"${item.nombre}\" del carrito?")
                    .setPositiveButton("Sí") { _, _ ->
                        eliminarDeFirebase(item.id_producto)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = carritoItems.size

    private fun actualizarCantidadFirebase(idProducto: String, nuevaCantidad: Int) {
        val uid = getUidActual()
        val ref = FirebaseDatabase.getInstance().getReference("Carritos/$uid/$idProducto")
        ref.child("cantidad").setValue(nuevaCantidad)
    }

    private fun eliminarDeFirebase(idProducto: String) {
        val uid = getUidActual()
        FirebaseDatabase.getInstance().getReference("Carritos/$uid/$idProducto").removeValue()
    }

    private fun actualizarIcono(holder: HolderCarrito, cantidad: Int) {
        val icono = if (cantidad <= 1)
            R.drawable.icono_borrar_xml
        else
            R.drawable.icono_menos
        holder.binding.btnRestar.setIconResource(icono)
    }

    private fun getUidActual(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    private fun cargarPrimeraImagen(idProducto: String, holder: HolderCarrito) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos/$idProducto/Imagenes Producto")
        ref.limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (img in snapshot.children) {
                    val url = img.child("imagenUrl").value.toString()
                    Glide.with(context)
                        .load(url)
                        .placeholder(com.example.slopshop.R.drawable.icono_producto)
                        .into(holder.binding.imagenProducto)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class HolderCarrito(val binding: ItemCarritoProductoBinding) : RecyclerView.ViewHolder(binding.root)
}