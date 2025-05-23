package com.example.slopshop.Administrador.Productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorImagenProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.databinding.FragmentVerYEditarProductoBinding
import com.google.firebase.database.*

class FragmentVerYEditarProducto : Fragment() {

    private var productoId: String? = null
    private lateinit var binding: FragmentVerYEditarProductoBinding
    private lateinit var listaImagenes: ArrayList<String>
    private lateinit var adaptadorImagenes: AdaptadorImagenProducto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productoId = arguments?.getString(ARG_PRODUCTO_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerYEditarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaImagenes = ArrayList()
        adaptadorImagenes = AdaptadorImagenProducto(requireContext(), listaImagenes)
        binding.rvImagenesProducto.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImagenesProducto.adapter = adaptadorImagenes

        if (productoId != null) {
            cargarProducto(productoId!!)
        } else {
            Toast.makeText(requireContext(), "ID de producto no válido", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccionProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Función no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarProducto(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val producto = snapshot.getValue(Producto::class.java)
                if (producto != null) {
                    mostrarDatosProducto(producto)
                } else {
                    Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar producto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDatosProducto(producto: Producto) {
        binding.tituloProducto.text = producto.nombre
        binding.txtCategoria.text = "Categoría: ${producto.categoria}"
        binding.txtDescripcion.text = producto.descipcion
        binding.txtPrecio.text = "Precio: $${producto.precio}"
        binding.txtPrecioDescuento.text = "Precio con Descuento: $${producto.precioDescuento}"
        binding.txtEjemploDescuento.text = producto.ejemploDescuento
        binding.txtRating.text = "Rating: ★★★★☆" // Puedes personalizar esto si agregás soporte a rating real

        listaImagenes.clear()
        // Si tu clase Producto no tiene lista de imágenes, puedes agregarla.
        // Ejemplo:
        // producto.imagenes?.let { listaImagenes.addAll(it) }
        adaptadorImagenes.notifyDataSetChanged()
    }

    companion object {
        private const val ARG_PRODUCTO_ID = "producto_id"

        fun newInstance(id: String): FragmentVerYEditarProducto {
            val fragment = FragmentVerYEditarProducto()
            val args = Bundle()
            args.putString(ARG_PRODUCTO_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
