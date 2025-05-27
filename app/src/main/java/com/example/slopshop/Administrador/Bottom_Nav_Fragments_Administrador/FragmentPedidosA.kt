package com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Adaptador.AdaptadorPedido
import com.example.slopshop.Entidades.Pedido
import com.example.slopshop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentPedidosA : Fragment() {

    private lateinit var recyclerPedidos: RecyclerView
    private lateinit var adaptador: AdaptadorPedido
    private lateinit var listaFull: MutableList<Pedido>
    private lateinit var listaDisplay: MutableList<Pedido>
    private lateinit var etBuscar: EditText
    private lateinit var btnFiltrar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pedidos_a, container, false)
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos)
        etBuscar = view.findViewById(R.id.etBuscarPedido)
        btnFiltrar = view.findViewById(R.id.btnFiltrar)

        listaFull = mutableListOf()
        listaDisplay = mutableListOf()
        adaptador = AdaptadorPedido(requireContext(), listaDisplay)

        recyclerPedidos.layoutManager = LinearLayoutManager(requireContext())
        recyclerPedidos.adapter = adaptador

        cargarPedidosParaVendedor()

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().lowercase()
                val filtrados = listaFull.filter {
                    it.producto.lowercase().contains(texto) ||
                            it.estado.lowercase().contains(texto) ||
                            it.fecha.lowercase().contains(texto)
                }
                listaDisplay.clear()
                listaDisplay.addAll(filtrados)
                adaptador.notifyDataSetChanged()
            }
        })

        return view
    }

    private fun cargarPedidosParaVendedor() {
        val uidVendedor = FirebaseAuth.getInstance().uid ?: return
        val refProductos = FirebaseDatabase.getInstance().getReference("Productos")
        val refPedidosDetalle = FirebaseDatabase.getInstance().getReference("PedidosDetalle")
        val refPedidos = FirebaseDatabase.getInstance().getReference("Pedidos")

        val productosDelVendedor = mutableSetOf<String>()

        refProductos.orderByChild("uidVendedor").equalTo(uidVendedor)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(productosSnapshot: DataSnapshot) {
                    for (prodSnap in productosSnapshot.children) {
                        prodSnap.key?.let { productosDelVendedor.add(it) }
                    }

                    if (productosDelVendedor.isEmpty()) {
                        adaptador.notifyDataSetChanged()
                        return
                    }

                    refPedidosDetalle.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(detalleSnapshot: DataSnapshot) {
                            val pedidosRelacionados = mutableMapOf<String, MutableList<Pair<String, Int>>>()

                            for (detalle in detalleSnapshot.children) {
                                val idProducto = detalle.child("id_producto").getValue(String::class.java)
                                val idPedido = detalle.child("id_pedido").getValue(String::class.java)
                                val cantidad = detalle.child("cantidad").getValue(Int::class.java) ?: 0

                                if (idProducto != null && idPedido != null && productosDelVendedor.contains(idProducto)) {
                                    pedidosRelacionados.getOrPut(idPedido) { mutableListOf() }
                                        .add(Pair(idProducto, cantidad))
                                }
                            }

                            if (pedidosRelacionados.isEmpty()) {
                                adaptador.notifyDataSetChanged()
                                return
                            }

                            val pedidosTemp = mutableListOf<Pedido>()
                            var pedidosProcesados = 0

                            for ((idPedido, productos) in pedidosRelacionados) {
                                refPedidos.child(idPedido)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(pedidoSnap: DataSnapshot) {
                                            val pedido = pedidoSnap.getValue(Pedido::class.java)
                                            if (pedido != null) {
                                                pedido.id = idPedido

                                                val nombresProductos = mutableListOf<String>()
                                                var imagenPrincipal = ""
                                                var productosCargados = 0

                                                for ((idProd, cantidad) in productos) {
                                                    refProductos.child(idProd)
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(prodSnap: DataSnapshot) {
                                                                val nombre = prodSnap.child("nombre").getValue(String::class.java) ?: "Producto"
                                                                val imagenUrl = prodSnap.child("Imagenes Producto").children
                                                                    .firstOrNull()?.child("imagenUrl")?.getValue(String::class.java) ?: ""

                                                                nombresProductos.add("$nombre ($cantidad)")

                                                                if (imagenPrincipal.isEmpty() && imagenUrl.isNotEmpty()) {
                                                                    imagenPrincipal = imagenUrl
                                                                }

                                                                productosCargados++
                                                                if (productosCargados == productos.size) {
                                                                    pedido.producto = nombresProductos.joinToString(", ")
                                                                    pedido.imagenProducto = imagenPrincipal
                                                                    pedidosTemp.add(pedido)
                                                                    pedidosProcesados++
                                                                    if (pedidosProcesados == pedidosRelacionados.size) {
                                                                        actualizarListaYMostrar(pedidosTemp)
                                                                    }
                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                productosCargados++
                                                                if (productosCargados == productos.size) {
                                                                    pedido.producto = nombresProductos.joinToString(", ")
                                                                    pedido.imagenProducto = imagenPrincipal
                                                                    pedidosTemp.add(pedido)
                                                                    pedidosProcesados++
                                                                    if (pedidosProcesados == pedidosRelacionados.size) {
                                                                        actualizarListaYMostrar(pedidosTemp)
                                                                    }
                                                                }
                                                            }
                                                        })
                                                }
                                            } else {
                                                pedidosProcesados++
                                                if (pedidosProcesados == pedidosRelacionados.size) {
                                                    actualizarListaYMostrar(pedidosTemp)
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            pedidosProcesados++
                                            if (pedidosProcesados == pedidosRelacionados.size) {
                                                actualizarListaYMostrar(pedidosTemp)
                                            }
                                        }
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), "Error al cargar detalles: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar productos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun actualizarListaYMostrar(pedidos: List<Pedido>) {
        listaFull.clear()
        listaFull.addAll(pedidos.sortedByDescending { it.timestamp })
        listaDisplay.clear()
        listaDisplay.addAll(listaFull)
        adaptador.notifyDataSetChanged()
    }
}
