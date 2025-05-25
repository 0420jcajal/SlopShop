package com.example.slopshop.Administrador.Productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.slopshop.Adaptador.AdaptadorImagenProducto
import com.example.slopshop.Administrador.Puntuaciones.FragmentComentariosProductoA
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.databinding.FragmentVerYEditarProductoBinding
import com.google.firebase.database.*
import com.example.slopshop.R

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

        binding.btnEditar.setOnClickListener {
            productoId?.let { id ->
                val fragmentEditar = FragmentEditarProducto().apply {
                    arguments = Bundle().apply {
                        putString("productoId", id)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.navFragment, fragmentEditar)
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                Toast.makeText(requireContext(), "ID de producto no válido", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnVerComentarios.setOnClickListener {
            val fragment = FragmentComentariosProductoA.newInstance(productoId)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.navFragment, fragment)
                .addToBackStack(null)
                .commit()
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
        binding.txtDescripcion.text = producto.descripcion

        mostrarDescuento(producto)

        cargarRating(producto.id)

        listaImagenes.clear()
        adaptadorImagenes.notifyDataSetChanged()

        cargarPrimeraImagen(producto.id)
        cargarImagenesProducto(producto.id)
    }

    private fun mostrarDescuento(producto: Producto) {
        if (producto.precioDescuento != "0") {

            binding.txtPrecio.apply {
                text = "Precio: ${producto.precio}€"
                paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(typeface, android.graphics.Typeface.NORMAL)
                visibility = View.VISIBLE
            }
            binding.txtPrecioDescuento.visibility = View.VISIBLE
            binding.txtEjemploDescuento.visibility = View.VISIBLE

            binding.txtPrecioDescuento.text = "Precio: ${producto.precioDescuento}€"
            binding.txtEjemploDescuento.text = producto.ejemploDescuento
        } else {

            binding.txtPrecio.visibility = View.VISIBLE
            binding.txtPrecioDescuento.visibility = View.GONE
            binding.txtEjemploDescuento.visibility = View.GONE

            binding.txtPrecio.text = "Precio: ${producto.precio}€"
        }
    }

    private fun cargarPrimeraImagen(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(id).child("Imagenes Producto")
            .limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (uri in snapshot.children) {
                        val imagenUrl = "${uri.child("imagenUrl").value}"
                        try {
                            Glide.with(requireContext())
                                .load(imagenUrl)
                                .placeholder(android.R.color.background_light)
                                .fitCenter()
                                .into(binding.imagenPrincipalProducto)
                        } catch (e: Exception) {
                            println("No se ha podido cargar la imagen: $e")
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error cargando imagen", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarImagenesProducto(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id).child("Imagenes Producto")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaImagenes.clear()
                for (imageSnapshot in snapshot.children) {
                    val imagenUrl = imageSnapshot.child("imagenUrl").getValue(String::class.java)
                    imagenUrl?.let {
                        listaImagenes.add(it)
                    }
                }
                adaptadorImagenes.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error cargando imágenes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarRating(productoId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Valoraciones")
        ref.orderByChild("productId").equalTo(productoId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var total = 0f
                    var count = 0
                    for (valoracion in snapshot.children) {
                        val puntuacion = valoracion.child("puntuacion").getValue(Int::class.java) ?: 0
                        total += puntuacion
                        count++
                    }
                    if (count > 0) {
                        val promedio = total / count
                        val estrellas = "★".repeat(promedio.toInt()) + "☆".repeat(5 - promedio.toInt())
                        binding.txtRating.text = "Puntuación:  $estrellas  ${"%.1f".format(promedio)}"
                    } else {
                        binding.txtRating.text = "Sin puntuaciones aún"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.txtRating.text = "Error al cargar rating"
                }
            })
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
