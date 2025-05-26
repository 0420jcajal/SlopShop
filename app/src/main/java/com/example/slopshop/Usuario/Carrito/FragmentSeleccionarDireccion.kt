package com.example.slopshop.Usuario.Carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slopshop.Adaptador.AdaptadorDirecciones
import com.example.slopshop.Entidades.Direccion
import com.example.slopshop.R
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
                // Acción al pulsar dirección → por ejemplo: ir a pantalla de pago
                Toast.makeText(requireContext(), "Seleccionaste: ${direccion.calle}", Toast.LENGTH_SHORT).show()
                // TODO: Continuar con proceso de compra
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
