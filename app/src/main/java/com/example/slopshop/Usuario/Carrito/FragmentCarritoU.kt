package com.example.slopshop.Usuario.Carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorCarrito
import com.example.slopshop.Entidades.Carrito
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentCarritoUBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentCarritoU : Fragment() {

    private lateinit var binding: FragmentCarritoUBinding
    private lateinit var listaCarrito: ArrayList<Carrito>
    private lateinit var adaptadorCarrito: AdaptadorCarrito
    private val uid: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarritoUBinding.inflate(inflater, container, false)

        listaCarrito = arrayListOf()
        adaptadorCarrito = AdaptadorCarrito(requireContext(), listaCarrito)
        binding.recyclerCarrito.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCarrito.adapter = adaptadorCarrito

        cargarCarrito()

        binding.btnComprar.setOnClickListener {
            if (listaCarrito.isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{

                mostrarDialogoConfirmacion()

            }



        }

        return binding.root
    }

    private fun cargarCarrito() {
        if (uid.isEmpty()) {
            Toast.makeText(requireContext(), "No se encontró usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Carritos").child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCarrito.clear()
                var total = 0.0
                for (snap in snapshot.children) {
                    val producto = snap.getValue(Carrito::class.java)
                    if (producto != null) {
                        listaCarrito.add(producto)
                        total += producto.precio_unitario * producto.cantidad
                    }
                }
                adaptadorCarrito.notifyDataSetChanged()
                binding.tvTotal.text = String.format("Total: %.2f €", total)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error cargando carrito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoConfirmacion() {
        val builder = StringBuilder()

        for (item in listaCarrito) {
            builder.append("${item.nombre} x${item.cantidad}\n")
        }

        val total = listaCarrito.sumOf { it.precio_unitario * it.cantidad }

        val mensaje = """${builder.toString()}
            
TOTAL: %.2f €
                         
                         
¿Está seguro de continuar?""".trimIndent().format(total)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmación de compra")
            .setMessage(mensaje)
            .setPositiveButton("Sí, continuar") { _, _ ->
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.navFragment, FragmentSeleccionarDireccion())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.findViewById<View>(android.R.id.message)?.let {
            (it as? android.widget.TextView)?.textSize = 16f
        }
    }
}
