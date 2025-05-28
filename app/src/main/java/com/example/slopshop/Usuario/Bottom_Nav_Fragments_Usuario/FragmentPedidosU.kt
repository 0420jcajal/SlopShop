package com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorPedido
import com.example.slopshop.Entidades.Pedido
import com.example.slopshop.R
import com.example.slopshop.Usuario.Pedidos.FragmentPedidosDetalleU
import com.example.slopshop.databinding.FragmentPedidosUBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentPedidosU : Fragment() {
    private var _binding: FragmentPedidosUBinding? = null
    private val binding get() = _binding!!

    private val listaFull = arrayListOf<Pedido>()
    private val listaDisplay = arrayListOf<Pedido>()
    private lateinit var adaptador: AdaptadorPedido
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedidosUBinding.inflate(inflater, container, false)

        adaptador = AdaptadorPedido(requireContext(), listaDisplay) { pedido ->
            val fragment = FragmentPedidosDetalleU().apply {
                arguments = Bundle().apply {
                    putString("id_pedido", pedido.id)
                    putString("fecha_pedido", pedido.fecha)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.navFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerPedidos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adaptador
        }

        binding.btnFiltrar.setOnClickListener {
            val query = binding.etBuscarPedido.text.toString().trim()
            filtrarPedidos(query)
        }

        cargarPedidos()

        return binding.root
    }

    private fun cargarPedidos() {
        val refPedidos = FirebaseDatabase.getInstance()
            .getReference("Pedidos")
            .orderByChild("id_cliente")
            .equalTo(uid)

        val refPedidosDetalle = FirebaseDatabase.getInstance().getReference("PedidosDetalle")
        val refProductos = FirebaseDatabase.getInstance().getReference("Productos")

        refPedidos.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaFull.clear()
                listaDisplay.clear()

                if (!snapshot.exists()) {
                    adaptador.notifyDataSetChanged()
                    return
                }

                val pedidosSnapshotList = snapshot.children.reversed().toList()
                val pedidosTemp = mutableListOf<Pedido>()
                var pedidosProcesados = 0

                if (pedidosSnapshotList.isEmpty()) {
                    adaptador.notifyDataSetChanged()
                    return
                }

                for (pedidoSnap in pedidosSnapshotList) {
                    val pedido = pedidoSnap.getValue(Pedido::class.java) ?: continue
                    pedido.id = pedidoSnap.key ?: ""

                    refPedidosDetalle.orderByChild("id_pedido").equalTo(pedido.id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(detalleSnapshot: DataSnapshot) {
                                val productosMap = mutableMapOf<String, Int>()

                                for (detalle in detalleSnapshot.children) {
                                    val idProducto = detalle.child("id_producto").getValue(String::class.java) ?: continue
                                    val cantidad = detalle.child("cantidad").getValue(Int::class.java) ?: 0
                                    productosMap[idProducto] = cantidad
                                }

                                if (productosMap.isEmpty()) {
                                    pedido.producto = "Sin productos"
                                    pedido.imagenProducto = ""
                                    pedidosTemp.add(pedido)
                                    pedidosProcesados++
                                    if (pedidosProcesados == pedidosSnapshotList.size) {
                                        actualizarListasYPasarAdapter(pedidosTemp)
                                    }
                                    return
                                }

                                val nombresProductos = mutableListOf<String>()
                                var imagenPrincipal = ""
                                var productosCargados = 0

                                for ((idProducto, cantidad) in productosMap) {
                                    refProductos.child(idProducto)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(prodSnap: DataSnapshot) {
                                                val nombre = prodSnap.child("nombre").getValue(String::class.java) ?: "Producto"

                                                val imagenUrl = prodSnap.child("Imagenes Producto").children.firstOrNull()
                                                    ?.child("imagenUrl")?.getValue(String::class.java) ?: ""

                                                nombresProductos.add("$nombre ($cantidad)")

                                                if (imagenPrincipal.isEmpty() && imagenUrl.isNotEmpty()) {
                                                    imagenPrincipal = imagenUrl
                                                }

                                                productosCargados++
                                                if (productosCargados == productosMap.size) {
                                                    pedido.producto = nombresProductos.joinToString(", ")
                                                    pedido.imagenProducto = imagenPrincipal
                                                    pedidosTemp.add(pedido)
                                                    pedidosProcesados++
                                                    if (pedidosProcesados == pedidosSnapshotList.size) {
                                                        actualizarListasYPasarAdapter(pedidosTemp)
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                productosCargados++
                                                if (productosCargados == productosMap.size) {
                                                    pedido.producto = nombresProductos.joinToString(", ")
                                                    pedido.imagenProducto = imagenPrincipal
                                                    pedidosTemp.add(pedido)
                                                    pedidosProcesados++
                                                    if (pedidosProcesados == pedidosSnapshotList.size) {
                                                        actualizarListasYPasarAdapter(pedidosTemp)
                                                    }
                                                }
                                            }
                                        })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                pedidosProcesados++
                                if (pedidosProcesados == pedidosSnapshotList.size) {
                                    actualizarListasYPasarAdapter(pedidosTemp)
                                }
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error cargando pedidos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarListasYPasarAdapter(pedidos: List<Pedido>) {
        listaFull.clear()
        listaFull.addAll(pedidos)
        listaDisplay.clear()
        listaDisplay.addAll(listaFull)
        adaptador.notifyDataSetChanged()
    }

    private fun filtrarPedidos(query: String) {
        listaDisplay.clear()
        if (query.isEmpty()) {
            listaDisplay.addAll(listaFull)
        } else {
            val lower = query.lowercase()
            listaFull.forEach { pedido ->
                if (pedido.id.lowercase().contains(lower) || pedido.producto.lowercase().contains(lower)) {
                    listaDisplay.add(pedido)
                }
            }
        }
        if (listaDisplay.isEmpty()) {
            Toast.makeText(requireContext(), "No se encontraron pedidos", Toast.LENGTH_SHORT).show()
        }
        adaptador.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
