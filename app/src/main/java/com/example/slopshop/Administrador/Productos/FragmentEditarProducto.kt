package com.example.slopshop.Administrador.Productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.example.slopshop.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

class FragmentEditarProducto : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etPrecioDescuento: EditText
    private lateinit var etNotaDescuento: EditText
    private lateinit var switchDescuento: SwitchCompat
    private lateinit var tvCategoria: TextView
    private lateinit var btnGuardar: MaterialButton
    private lateinit var ivImagenProducto: ImageView
    private lateinit var btnEliminar : MaterialButton

    private lateinit var productoId: String

    private var listaCategorias = mutableListOf<Categoria>()
    private var categoriaSeleccionadaId: String? = null

    data class Categoria(
        val id: String = "",
        val categoria: String = "",
        val imagenUrl: String? = null
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_producto, container, false)

        etNombre = view.findViewById(R.id.etNombreProducto)
        etDescripcion = view.findViewById(R.id.etDescripcionProducto)
        etPrecio = view.findViewById(R.id.etPrecioProducto)
        etPrecioDescuento = view.findViewById(R.id.etPrecioConDescuentoProducto)
        etNotaDescuento = view.findViewById(R.id.ejemploDescuento)
        switchDescuento = view.findViewById(R.id.decSwitch)
        tvCategoria = view.findViewById(R.id.categoria)
        btnGuardar = view.findViewById(R.id.btnGuardarCambios)
        ivImagenProducto = view.findViewById(R.id.imagenProductoEditar)
        btnEliminar= view.findViewById(R.id.btnEliminar)

        productoId = arguments?.getString("productoId") ?: ""

        switchDescuento.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            etPrecioDescuento.visibility = visibility
            etNotaDescuento.visibility = visibility
        }

        etPrecioDescuento.visibility = View.GONE
        etNotaDescuento.visibility = View.GONE

        cargarCategoriasDesdeFirebase()

        switchDescuento.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPrecioDescuento.visibility = View.VISIBLE
                etNotaDescuento.visibility = View.VISIBLE
            } else {
                etPrecioDescuento.visibility = View.GONE
                etNotaDescuento.visibility = View.GONE
            }
        }
        tvCategoria.setOnClickListener {
            mostrarSelectorCategorias()
        }

        if (productoId.isNotEmpty()) {
            cargarProductoDesdeFirebase(productoId)
        }

        btnGuardar.setOnClickListener {
            mostrarConfirmacionGuardar()
        }


        btnEliminar.setOnClickListener {
            mostrarConfirmacionEliminar()
        }

        return view
    }

    private fun mostrarConfirmacionEliminar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                dialog.dismiss()
                eliminarProductoDeFirebase()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun eliminarProductoDeFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Productos").child(productoId)
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference("Productos")

        dbRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val imagenesSnap = snapshot.child("Imagenes Producto")
            val imagenesIds = imagenesSnap.children.mapNotNull { it.key }

            if (imagenesIds.isEmpty()) {
                Toast.makeText(context, "No se ha podido borrar el producto porque no se encuentran sus imagenes", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            var eliminadas = 0
            var fallo = false

            for (imagenId in imagenesIds) {
                val imgRef = storageRef.child(imagenId)
                imgRef.delete().addOnSuccessListener {
                    eliminadas++
                    if (eliminadas == imagenesIds.size && !fallo) {
                        eliminarProductoDeBaseDeDatos(dbRef)
                    }
                }.addOnFailureListener {
                    fallo = true
                    Toast.makeText(context, "Error al eliminar imagen: $imagenId", Toast.LENGTH_SHORT).show()
                }
            }

        }.addOnFailureListener {
            Toast.makeText(context, "Error al acceder a los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarProductoDeBaseDeDatos(dbRef: com.google.firebase.database.DatabaseReference) {
        dbRef.removeValue().addOnSuccessListener {

            Toast.makeText(context, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()

            val fragmentProductos = com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentProductosA()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.navFragment, fragmentProductos)
                .commit()

        }.addOnFailureListener {
            Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
        }
    }


    private fun cargarCategoriasDesdeFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Categorías")

        dbRef.get().addOnSuccessListener { snapshot ->

            listaCategorias.clear()

            for (categoriaSnap in snapshot.children) {

                val id = categoriaSnap.key ?: ""
                val categoriaNombre = categoriaSnap.child("categoria").getValue(String::class.java) ?: ""
                val imagenUrl = categoriaSnap.child("imagenUrl").getValue(String::class.java)

                if (id.isNotEmpty() && categoriaNombre.isNotEmpty()) {
                    listaCategorias.add(Categoria(id, categoriaNombre, imagenUrl))
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarSelectorCategorias() {

        if (listaCategorias.isEmpty()) {

            Toast.makeText(context, "No hay categorías disponibles", Toast.LENGTH_SHORT).show()
            return

        }

        val categoriasNombres = listaCategorias.map { it.categoria }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una categoría")
            .setItems(categoriasNombres) { dialog, which ->

                val categoriaSeleccionada = listaCategorias[which]
                tvCategoria.text = categoriaSeleccionada.categoria
                categoriaSeleccionadaId = categoriaSeleccionada.id
                dialog.dismiss()

            }
            .show()
    }

    private fun mostrarConfirmacionGuardar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar")
            .setMessage("¿Estás seguro que deseas aplicar estos cambios?")
            .setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                guardarCambiosEnFirebase {
                    irAVeryEditarProducto()
                }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun irAVeryEditarProducto() {

        val fragmentVerYEditar = FragmentVerYEditarProducto.newInstance(productoId)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.navFragment, fragmentVerYEditar)
            .addToBackStack(null)
            .commit()
    }

    private fun cargarProductoDesdeFirebase(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Productos").child(id)

        dbRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                etNombre.setText(snapshot.child("nombre").getValue(String::class.java) ?: "")
                etDescripcion.setText(snapshot.child("descripcion").getValue(String::class.java) ?: "")
                etPrecio.setText(snapshot.child("precio").getValue(String::class.java) ?: "")
                etPrecioDescuento.setText(snapshot.child("precioDescuento").getValue(String::class.java) ?: "")
                etNotaDescuento.setText(snapshot.child("ejemploDescuento").getValue(String::class.java) ?: "")
                tvCategoria.text = snapshot.child("categoria").getValue(String::class.java) ?: ""


                val imagenesProductoSnap = snapshot.child("Imagenes Producto")

                if (imagenesProductoSnap.exists() && imagenesProductoSnap.hasChildren()) {

                    val primeraImagenUrl = imagenesProductoSnap.children.first().child("imagenUrl").getValue(String::class.java)

                    if (!primeraImagenUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(primeraImagenUrl)
                            .placeholder(R.drawable.icono_producto)
                            .centerCrop()
                            .into(ivImagenProducto)
                    } else {
                        ivImagenProducto.setImageResource(R.drawable.icono_producto)
                    }
                } else {
                    ivImagenProducto.setImageResource(R.drawable.icono_producto)
                }

                val precioDescuento = snapshot.child("precioDescuento").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                switchDescuento.isChecked = precioDescuento > 0.0
            } else {
                Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarCambiosEnFirebase(onComplete: () -> Unit) {

        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precio = etPrecio.text.toString().trim()
        val precioDescuento = etPrecioDescuento.text.toString().trim()
        val notaDescuento = etNotaDescuento.text.toString().trim()
        val categoria = tvCategoria.text.toString().trim()
        val tieneDescuento = switchDescuento.isChecked

        if (nombre.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(context, "Nombre, descripción y precio son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (categoria.isEmpty()) {
            Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }
        if (tieneDescuento && precioDescuento.isEmpty()) {
            Toast.makeText(context, "Ingrese precio con descuento", Toast.LENGTH_SHORT).show()
            return
        }

        val precioDescuentoFinal = if (tieneDescuento) precioDescuento else "0"
        val notaDescuentoFinal = if (tieneDescuento) notaDescuento else ""

        val productoMap = hashMapOf<String, Any?>(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "precio" to precio,
            "categoria" to categoria,
            "precioDescuento" to precioDescuentoFinal,
            "ejemploDescuento" to notaDescuentoFinal,
            "id" to productoId
        )

        val dbRef = FirebaseDatabase.getInstance().getReference("Productos").child(productoId)
        dbRef.updateChildren(productoMap).addOnSuccessListener {
            Toast.makeText(context, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
            onComplete()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        fun newInstance(productoId: String): FragmentEditarProducto {
            val fragment = FragmentEditarProducto()
            val args = Bundle()
            args.putString("productoId", productoId)
            fragment.arguments = args
            return fragment
        }
    }
}
