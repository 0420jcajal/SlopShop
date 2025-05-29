package com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Adaptador.AdaptadorProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentCatalogoUBinding
import com.google.firebase.database.*

class FragmentCatalogoU : Fragment() {

    private lateinit var binding: FragmentCatalogoUBinding
    private lateinit var mContext: Context
    private var listaProductos: ArrayList<Producto> = ArrayList()
    private var productosFiltrados: ArrayList<Producto> = ArrayList()
    private lateinit var adaptadorProducto: AdaptadorProducto

    private var currentPage = 0
    private val tamañoPagina = 4

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCatalogoUBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.catalogoProductosRV.layoutManager = GridLayoutManager(mContext, 2)
        adaptadorProducto = AdaptadorProducto(mContext, ArrayList())
        binding.catalogoProductosRV.adapter = adaptadorProducto

        binding.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                actualizarPagina()
            }
        }

        binding.catalogoProductosRV.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = binding.catalogoProductosRV.getChildViewHolder(view)
                    val position = holder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val indexReal = currentPage * tamañoPagina + position
                        val productoSeleccionado = productosFiltrados[indexReal]
                        val uidActual = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.navFragment, com.example.slopshop.Usuario.Productos.FragmentVerYComprarProducto.newInstance(productoSeleccionado.id))
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        })

        binding.btnNext.setOnClickListener {
            currentPage++
            actualizarPagina()
        }

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().lowercase()
                productosFiltrados = ArrayList(
                    listaProductos.filter {
                        it.nombre.lowercase().contains(texto) || it.categoria.lowercase().contains(texto)
                    }
                )
                currentPage = 0
                actualizarPagina()
            }
        })

        val opcionesSpinner = listOf("Mejor Valorados", "En Oferta", "Últimos publicados", "Mas Baratos")
        val adapterSpinner = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, opcionesSpinner)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = adapterSpinner

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        productosFiltrados = ArrayList(listaProductos.sortedByDescending { it.mediaPuntuacion ?: 0f })
                    }
                    1 -> {
                        productosFiltrados = ArrayList(listaProductos.filter {
                            it.precioDescuento.isNotBlank() && it.precioDescuento != it.precio
                        })
                    }
                    2 -> {
                        productosFiltrados = ArrayList(listaProductos.sortedByDescending { it.id })
                    }
                    3 ->  ArrayList(listaProductos.sortedByDescending { it.precio })

                    else -> {
                        productosFiltrados = ArrayList(listaProductos)
                    }
                }
                currentPage = 0
                actualizarPagina()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                productosFiltrados = ArrayList(listaProductos)
                currentPage = 0
                actualizarPagina()
            }
        }

        listarProductos()
    }

    private fun actualizarPagina() {
        val from = currentPage * tamañoPagina
        val to = minOf(from + tamañoPagina, productosFiltrados.size)
        val sublista = if (from < productosFiltrados.size)
            productosFiltrados.subList(from, to)
        else
            emptyList()

        adaptadorProducto = AdaptadorProducto(mContext, ArrayList(sublista))
        binding.catalogoProductosRV.adapter = adaptadorProducto

        val totalPaginas = (productosFiltrados.size + tamañoPagina - 1) / tamañoPagina
        binding.txtPage.text = getString(R.string.paginaProducto, currentPage + 1, totalPaginas)

        binding.btnPrev.isEnabled = currentPage > 0
        binding.btnNext.isEnabled = currentPage + 1 < totalPaginas
    }

    private fun listarProductos() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (uri in snapshot.children) {
                    val modeloProducto = uri.getValue(Producto::class.java)
                    if (modeloProducto != null && modeloProducto.borrado != true) {
                        listaProductos.add(modeloProducto)
                    }
                }
                calcularValoraciones()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calcularValoraciones() {
        val refVal = FirebaseDatabase.getInstance().getReference("Valoraciones")
        refVal.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapaValoraciones = mutableMapOf<String, MutableList<Int>>()

                for (valSnap in snapshot.children) {
                    val productId = valSnap.child("productId").getValue(String::class.java)
                    val puntuacion = valSnap.child("puntuacion").getValue(Int::class.java)

                    if (productId != null && puntuacion != null) {
                        if (!mapaValoraciones.containsKey(productId)) {
                            mapaValoraciones[productId] = mutableListOf()
                        }
                        mapaValoraciones[productId]?.add(puntuacion)
                    }
                }

                for (producto in listaProductos) {
                    val puntuaciones = mapaValoraciones[producto.id]
                    if (!puntuaciones.isNullOrEmpty()) {
                        val media = puntuaciones.sum().toFloat() / puntuaciones.size
                        producto.mediaPuntuacion = media
                    } else {
                        producto.mediaPuntuacion = 0f
                    }
                }

                productosFiltrados = ArrayList(listaProductos)
                if (isAdded) {
                    currentPage = 0
                    actualizarPagina()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
