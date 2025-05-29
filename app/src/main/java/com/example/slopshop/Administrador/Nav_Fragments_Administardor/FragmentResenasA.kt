package com.example.slopshop.Administrador.Nav_Fragments_Administardor

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.slopshop.Adaptador.AdaptadorProductoValoracion
import com.example.slopshop.Administrador.Puntuaciones.FragmentComentariosProductoA
import com.example.slopshop.Entidades.ProductoValoracion
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentResenasABinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentResenasA : Fragment() {

    private lateinit var binding: FragmentResenasABinding
    private lateinit var mContext: Context
    private lateinit var adaptador: AdaptadorProductoValoracion


    private val listaProductos = ArrayList<ProductoValoracion>()
    private val productosFiltrados = ArrayList<ProductoValoracion>()

    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResenasABinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerResenas.layoutManager = LinearLayoutManager(mContext)
        adaptador = AdaptadorProductoValoracion(mContext, productosFiltrados) { producto ->

            val fragment = FragmentComentariosProductoA.newInstance(producto.id)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.navFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerResenas.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = adaptador
        }


        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarProductos(s.toString())
            }
        })


        val opciones = listOf("Mejor Valorados", "Últimos Publicados")
        val spinnerAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, opciones)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = spinnerAdapter

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ordenarProductos(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        binding.btnAtras.setOnClickListener {
            Toast.makeText(mContext, "Volviendo atrás", Toast.LENGTH_SHORT).show()
        }


        binding.recyclerResenas.layoutManager = LinearLayoutManager(mContext)
        adaptador = AdaptadorProductoValoracion(
            mContext,
            productosFiltrados
        ) { producto ->

            val fragment = FragmentComentariosProductoA.newInstance(producto.id)

            parentFragmentManager.beginTransaction()
                .replace(R.id.navFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerResenas.adapter = adaptador


        cargarProductosDelVendedor()
    }

    private fun cargarProductosDelVendedor() {
        val uidActual = auth.currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Productos")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (snap in snapshot.children) {

                    val uidVend = snap.child("uidVendedor").getValue(String::class.java) ?: continue
                    if (uidVend != uidActual) continue


                    val id = snap.key ?: continue

                    val nombre = snap.child("nombre").getValue(String::class.java) ?: "Sin nombre"


                    val primeraImagen = snap.child("Imagenes Producto")
                        .children
                        .firstOrNull()
                    val urlImagen = primeraImagen
                        ?.child("imagenUrl")
                        ?.getValue(String::class.java)
                        ?: ""

                    listaProductos.add(
                        ProductoValoracion(
                            id            = id,
                            nombreProducto = nombre,
                            imagenUrl     = urlImagen,
                            uidVendedor   = uidVend
                        )
                    )
                }
                cargarValoraciones()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun cargarValoraciones() {
        val refVal = FirebaseDatabase.getInstance().getReference("Valoraciones")
        refVal.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val mapa = mutableMapOf<String, MutableList<Int>>()
                for (snap in snapshot.children) {
                    val pid = snap.child("productId").getValue(String::class.java)
                    val pts = snap.child("puntuacion").getValue(Int::class.java)
                    if (pid != null && pts != null) {
                        mapa.getOrPut(pid) { mutableListOf() }.add(pts)
                    }
                }

                for (prod in listaProductos) {
                    val listPts = mapa[prod.id]
                    prod.valoracionPromedio = if (!listPts.isNullOrEmpty()) {
                        listPts.sum().toFloat() / listPts.size
                    } else 0f
                }

                productosFiltrados.clear()
                productosFiltrados.addAll(listaProductos)
                adaptador.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filtrarProductos(texto: String) {
        val busq = texto.lowercase()
        productosFiltrados.clear()
        productosFiltrados.addAll(
            listaProductos.filter {
                it.nombreProducto.lowercase().contains(busq)
            }
        )
        adaptador.notifyDataSetChanged()
    }

    private fun ordenarProductos(pos: Int) {
        when (pos) {
            0 -> productosFiltrados.sortByDescending { it.valoracionPromedio }
            1 -> productosFiltrados.sortByDescending { it.id }
        }
        adaptador.notifyDataSetChanged()
    }
}
