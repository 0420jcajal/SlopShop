package com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Adaptador.AdaptadorProducto
import com.example.slopshop.Administrador.Productos.FragmentVerYEditarProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentProductosABinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentProductosA : Fragment() {

    private lateinit var binding: FragmentProductosABinding
    private lateinit var mContext: Context
    private var listaProductos = ArrayList<Producto>()
    private var productosFiltrados = ArrayList<Producto>()
    private lateinit var adaptadorProducto: AdaptadorProducto

    private lateinit var auth: FirebaseAuth

    private var currentPage = 0
    private val tamañoPagina = 4

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProductosABinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productosRV.layoutManager = GridLayoutManager(mContext, 2)
        adaptadorProducto = AdaptadorProducto(mContext, ArrayList())
        binding.productosRV.adapter = adaptadorProducto

        binding.productosRV.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {

                    val holder = binding.productosRV.getChildViewHolder(view)
                    val position = holder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val indexReal = currentPage * tamañoPagina + position
                        val productoSeleccionado = productosFiltrados[indexReal]

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.navFragment, FragmentVerYEditarProducto.newInstance(productoSeleccionado.id))
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            override fun onChildViewDetachedFromWindow(view: View) {}
        })

        binding.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                actualizarPagina()
            }
        }

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
                productosFiltrados = when (position) {
                    0 -> ArrayList(listaProductos.sortedByDescending { it.mediaPuntuacion })
                    1 -> ArrayList(listaProductos.filter {
                        val precio = it.precio.toFloatOrNull() ?: Float.MAX_VALUE
                        val descuento = it.precioDescuento.toFloatOrNull() ?: 0f
                        descuento > 0f && descuento < precio
                    })
                    2 -> ArrayList(listaProductos.sortedByDescending { it.id })
                    3 ->  ArrayList(listaProductos.sortedByDescending { it.precio })
                    else -> ArrayList(listaProductos)
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
        val sublista = if (from < productosFiltrados.size) productosFiltrados.subList(from, to) else emptyList()

        val itemsPagina = ArrayList(sublista)
        adaptadorProducto = AdaptadorProducto(mContext, itemsPagina)
        binding.productosRV.adapter = adaptadorProducto

        val totalPaginas = (productosFiltrados.size + tamañoPagina - 1) / tamañoPagina
        binding.txtPage.text = getString(R.string.paginaProducto, currentPage + 1, totalPaginas)

        binding.btnPrev.isEnabled = currentPage > 0
        binding.btnNext.isEnabled = currentPage + 1 < totalPaginas
    }

    private fun listarProductos() {
        val uidActual = auth.currentUser?.uid ?: return
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
                        mapaValoraciones.getOrPut(productId) { mutableListOf() }.add(puntuacion)
                    }
                }

                for (producto in listaProductos) {
                    val puntuaciones = mapaValoraciones[producto.id]
                    if (!puntuaciones.isNullOrEmpty()) {
                        producto.mediaPuntuacion = puntuaciones.sum().toFloat() / puntuaciones.size
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
