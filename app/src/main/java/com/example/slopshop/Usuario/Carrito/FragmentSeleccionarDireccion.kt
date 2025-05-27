package com.example.slopshop.Usuario.Carrito

import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.ProgressDialog.show
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorDirecciones
import com.example.slopshop.Entidades.Direccion
import com.example.slopshop.R
import com.example.slopshop.Usuario.Nav_Fragments_Usuario.FragmentInicioU
import com.example.slopshop.databinding.FragmentSeleccionarDireccionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentSeleccionarDireccion : Fragment() {

    private lateinit var binding: FragmentSeleccionarDireccionBinding
    private lateinit var listaDirecciones: ArrayList<Direccion>
    private lateinit var adaptadorDirecciones: AdaptadorDirecciones
    private val uid: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeleccionarDireccionBinding.inflate(inflater, container, false)

        listaDirecciones = arrayListOf()
        adaptadorDirecciones = AdaptadorDirecciones(
            requireContext(),
            listaDirecciones,
            onItemClick = { direccion, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar dirección")
                    .setMessage("¿Deseas usar esta dirección para la compra?\n\n${direccion.calle}\n\n${direccion.pais}, ${direccion.provincia}\n\n${direccion.ciudad}\n\n\nSe procedera con la compra")
                    .setPositiveButton("Sí") { _, _ ->

                        realizarPedido(direccion)

                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            onEliminarClick = { direccion, position ->
                eliminarDireccionFirebase(direccion.id)
                listaDirecciones.removeAt(position)
                adaptadorDirecciones.notifyItemRemoved(position)
            }
        )

        binding.recyclerDirecciones.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDirecciones.adapter = adaptadorDirecciones

        cargarDirecciones()

        binding.btnNuevaDireccion.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.navFragment, FragmentAgregarDireccion())
                .addToBackStack(null)
                .commit()
        }

        binding.btnAtras.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.navFragment, FragmentCarritoU())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun realizarPedido(direccion: Direccion) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val carritoRef = FirebaseDatabase.getInstance().getReference("Carritos").child(uid)
        val pedidoRef = FirebaseDatabase.getInstance().getReference("Pedidos").push()
        val pedidoId = pedidoRef.key ?: return

        val progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Comprando...")
            setCancelable(false)
            show()
        }

        carritoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    return
                }

                val detallesRef = FirebaseDatabase.getInstance().getReference("PedidosDetalle")

                val tareas = mutableListOf<DatabaseReference>()

                for (productoSnap in snapshot.children) {
                    val producto = productoSnap.value as? Map<*, *> ?: continue
                    val detalleRef = detallesRef.push()

                    val detalle = mapOf(
                        "id_pedido" to pedidoId,
                        "id_producto" to (producto["id_producto"] ?: ""),
                        "cantidad" to (producto["cantidad"] ?: 1)
                    )

                    detalleRef.setValue(detalle)
                    tareas.add(detalleRef)
                }

                val pedidoData = mapOf(
                    "id_cliente" to uid,
                    "estado" to "En proceso",
                    "direccion" to "${direccion.calle}, ${direccion.ciudad}, ${direccion.provincia}",
                    "tiempoRegistro" to ServerValue.TIMESTAMP
                )

                pedidoRef.setValue(pedidoData).addOnSuccessListener {
                    carritoRef.removeValue().addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Producto comprado con éxito", Toast.LENGTH_SHORT).show()

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.navFragment, FragmentInicioU())
                            .commit()

                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Error al vaciar el carrito", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error al registrar el pedido", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Error al leer el carrito", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun cargarDirecciones() {
        if (uid.isEmpty()) {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Direcciones")
        val query = ref.orderByChild("uidUsuario").equalTo(uid)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaDirecciones.clear()
                for (snap in snapshot.children) {
                    val direccion = snap.getValue(Direccion::class.java)
                    if (direccion != null) {
                        direccion.id = snap.key ?: ""
                        listaDirecciones.add(direccion)
                    }
                }
                adaptadorDirecciones.notifyDataSetChanged()

                if (listaDirecciones.isEmpty()) {
                    binding.recyclerDirecciones.visibility = View.GONE
                    binding.tvSinDirecciones.visibility = View.VISIBLE
                } else {
                    binding.recyclerDirecciones.visibility = View.VISIBLE
                    binding.tvSinDirecciones.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar direcciones", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarDireccionFirebase(idDireccion: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Direcciones").child(idDireccion)
        ref.removeValue()
    }
}
