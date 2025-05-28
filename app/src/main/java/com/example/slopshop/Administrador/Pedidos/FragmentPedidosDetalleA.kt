package com.example.slopshop.Administrador.Pedidos

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorPedidosDetalle
import com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentPedidosA
import com.example.slopshop.Entidades.PedidoDetalle
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentPedidosDetalleABinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FragmentPedidosDetalleA : Fragment() {
    private var _binding: FragmentPedidosDetalleABinding? = null
    private val binding get() = _binding!!

    private val listaDetalles = arrayListOf<PedidoDetalle>()
    private lateinit var adaptador: AdaptadorPedidosDetalle

    private var idPedido: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idPedido = arguments?.getString("id_pedido")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPedidosDetalleABinding.inflate(inflater, container, false)


        adaptador = AdaptadorPedidosDetalle(
            requireContext(),
            listaDetalles,
            isAdmin = true,
            onComprarDeNuevo = {},
            onValorarProducto = {}
        )

        binding.recyclerDetallePedido.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adaptador
        }

        binding.btnAtras.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.navFragment, FragmentPedidosA())
                .commit()
        }

        binding.cambiarEstado.setOnClickListener {

            val input = EditText(context).apply {
                hint = "Nuevo estado"
            }
            AlertDialog.Builder(context)
                .setTitle("Cambiar estado")
                .setView(input)
                .setPositiveButton("Cambiar") { _, _ ->
                    val nuevoEstado = input.text.toString().trim()
                    if (nuevoEstado.isNotEmpty()) {

                        AlertDialog.Builder(context)
                            .setTitle("Confirmar cambio")
                            .setMessage("¿Deseas cambiar el estado a '$nuevoEstado'? ")
                            .setPositiveButton("Sí") { _, _ ->
                                FirebaseDatabase.getInstance()
                                    .getReference("Pedidos")
                                    .child(idPedido!!)
                                    .child("estado")
                                    .setValue(nuevoEstado)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Estado cambiado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "Error al cambiar estado", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        Toast.makeText(context, "Estado vacío", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        idPedido?.let {
            cargarFechaPedido(it)
            cargarDetallesPedido(it)
        } ?: run {
            Toast.makeText(requireContext(), "Pedido inválido", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun cargarFechaPedido(id: String) {
        val refPedido = FirebaseDatabase.getInstance()
            .getReference("Pedidos")
            .child(id)

        refPedido.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val fechaStr = snap.child("fecha").getValue(String::class.java)
                    ?: snap.child("tiempoRegistro").getValue(Long::class.java)
                        ?.let { Date(it).let { d -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d) } }
                    ?: "desconocida"

                binding.tvFechaPedido.text = "Pedido realizado el día $fechaStr"
            }
            override fun onCancelled(err: DatabaseError) {
                binding.tvFechaPedido.text = "Pedido realizado el día desconocida"
            }
        })
    }

    private fun cargarDetallesPedido(id: String) {
        val refDetalle = FirebaseDatabase.getInstance()
            .getReference("PedidosDetalle")
            .orderByChild("id_pedido")
            .equalTo(id)

        refDetalle.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(detSnap: DataSnapshot) {
                listaDetalles.clear()
                var total = 0.0
                val detalles = detSnap.children.toList()
                if (detalles.isEmpty()) {
                    actualizarUI(total)
                    return
                }

                var cargados = 0
                val refProductos = FirebaseDatabase.getInstance().getReference("Productos")

                detalles.forEach { detalleSnap ->
                    val cantidad = detalleSnap.child("cantidad").getValue(Int::class.java) ?: 0
                    val idProd = detalleSnap.child("id_producto").getValue(String::class.java) ?: return@forEach

                    refProductos.child(idProd)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(prodSnap: DataSnapshot) {
                                val nombre = prodSnap.child("nombre").getValue(String::class.java) ?: "Producto"
                                val precioStr = prodSnap.child("precio").getValue(String::class.java) ?: "0"
                                val precio = precioStr.toDoubleOrNull() ?: 0.0
                                val imagen = prodSnap.child("Imagenes Producto")
                                    .children.firstOrNull()
                                    ?.child("imagenUrl")
                                    ?.getValue(String::class.java) ?: ""

                                listaDetalles.add(
                                    PedidoDetalle(
                                        idProducto = idProd,
                                        nombreProducto = nombre,
                                        imagenProducto = imagen,
                                        cantidad = cantidad,
                                        precioUnitario = precio
                                    )
                                )
                                total += cantidad * precio
                                cargados++
                                if (cargados == detalles.size) {
                                    actualizarUI(total)
                                }
                            }
                            override fun onCancelled(err: DatabaseError) {
                                cargados++
                                if (cargados == detalles.size) actualizarUI(total)
                            }
                        })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error cargando detalles", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarUI(total: Double) {
        adaptador.notifyDataSetChanged()
        binding.tvTotalPrecio.text = "Total: %.2f€".format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
