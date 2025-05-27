package com.example.slopshop.Usuario.Productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.slopshop.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.slopshop.Adaptador.AdaptadorImagenProducto
import com.example.slopshop.Administrador.Productos.FragmentVerYEditarProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.Usuario.Puntuaciones.FragmentPublicarComentario
import com.example.slopshop.Usuario.Productos.FragmentComentariosProductoU
import com.example.slopshop.databinding.FragmentVerYComprarProductoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FragmentVerYComprarProducto : Fragment() {

    private var productoId: String? = null
    private lateinit var binding: FragmentVerYComprarProductoBinding
    private lateinit var listaImagenes: ArrayList<String>
    private lateinit var adaptadorImagenes: AdaptadorImagenProducto

    private var productoActual: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productoId = arguments?.getString(ARG_PRODUCTO_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVerYComprarProductoBinding.inflate(inflater, container, false)
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

        binding.btnAgregarAlCarrito.setOnClickListener {
            val nombre = binding.tituloProducto.text.toString()
            val cantidad = binding.etQuantity.text.toString().toIntOrNull() ?: 1

            val precioTotal = if (binding.txtPrecioDescuento.visibility == View.GONE) {
                binding.txtPrecio.text.toString()
            } else {
                binding.txtPrecioDescuento.text.toString()
            }
            val precioUnitario = productoActual?.let {
                (it.precioDescuento.takeIf { it != "0" } ?: it.precio).toDoubleOrNull() ?: 0.0
            } ?: 0.0

            val mensaje = "Producto: $nombre\nCantidad: $cantidad \nPrecio $precioTotal"
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Añadir al Carrito")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    agregarProductoAlCarrito(productoId!!, nombre, cantidad.toInt(), precioUnitario)
                    Toast.makeText(requireContext(), "Añadido el producto al carrito", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.btnVerComentarios.setOnClickListener {
            productoId?.let { id ->
                parentFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.navFragment,
                        FragmentComentariosProductoU.newInstance(id)
                    )
                    .addToBackStack(null)
                    .commit()
            } ?: Toast.makeText(requireContext(), "Ha habido un error al cargar los comentarios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarProducto(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productoActual = snapshot.getValue(Producto::class.java)
                if (productoActual != null) {
                    mostrarDatosProducto(productoActual!!)
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

        cantidadProducto(producto)
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

    private fun cantidadProducto(producto: Producto) {
        var cantidad = 1
        actualizarPrecioTotal(producto, cantidad)


        binding.btnSumar.setOnClickListener {
            cantidad++
            binding.etQuantity.setText(cantidad.toString())
            actualizarPrecioTotal(producto, cantidad)

        }

        binding.btnRestar.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
            } else {
                Toast.makeText(requireContext(), "¿Eliminar producto del carrito?", Toast.LENGTH_SHORT).show()


            }
            binding.etQuantity.setText(cantidad.toString())
            actualizarPrecioTotal(producto, cantidad)

        }

        binding.etQuantity.setOnEditorActionListener { _, _, _ ->
            val nuevoValor = binding.etQuantity.text.toString().toIntOrNull() ?: 1
            cantidad = if (nuevoValor >= 1) nuevoValor else 1
            binding.etQuantity.setText(cantidad.toString())
            actualizarPrecioTotal(producto, cantidad)

            true
        }
    }




    private fun mostrarDescuento(producto: Producto) {
        if (producto.precioDescuento != "0") {

            binding.txtPrecio.apply {
                text = "Precio: ${producto.precio}€"
                paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                visibility = View.VISIBLE
            }

            binding.txtPrecioDescuento.apply {
                paintFlags = 0
                visibility = View.VISIBLE
            }
            binding.txtEjemploDescuento.apply {
                text = producto.ejemploDescuento
                visibility = View.VISIBLE
            }
        } else {

            binding.txtEjemploDescuento.visibility = View.GONE

            binding.txtPrecio.apply {
                paintFlags = 0
                visibility = View.VISIBLE
            }
            binding.txtPrecioDescuento.visibility = View.VISIBLE
        }
    }


    private fun actualizarPrecioTotal(producto: Producto, cantidad: Int) {

        val precioUnitario = (producto.precioDescuento.takeIf { it != "0" }
            ?: producto.precio)
            .toDoubleOrNull() ?: 0.0

        val total = precioUnitario * cantidad

        if (producto.precioDescuento != "0") {

            binding.txtPrecioDescuento.text = "Total: %.2f€".format(total)

        } else {

            binding.txtPrecio.text = "Total: %.2f€".format(total)
            binding.txtPrecioDescuento.visibility = View.GONE
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

    private fun agregarProductoAlCarrito(idProducto: String, nombre: String, cantidad: Int, precio: Double) {
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: "sin identificar"

        val refCarrito = FirebaseDatabase.getInstance().getReference("Carritos")
            .child(idUsuario)
            .child(idProducto)

        refCarrito.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cantidadExistente = snapshot.child("cantidad").getValue(Int::class.java) ?: 0
                val nuevaCantidad = cantidadExistente + cantidad

                val carritoMap = mapOf(
                    "id_producto" to idProducto,
                    "nombre" to nombre,
                    "cantidad" to nuevaCantidad,
                    "precio_unitario" to precio
                )

                refCarrito.setValue(carritoMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Producto actualizado en el carrito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al comprobar el carrito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_PRODUCTO_ID = "producto_id"

        fun newInstance(id: String): FragmentVerYComprarProducto {
            return FragmentVerYComprarProducto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCTO_ID, id)
                }
            }
        }
    }
}
