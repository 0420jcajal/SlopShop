package com.example.slopshop.Usuario.Productos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.slopshop.Adaptador.AdaptadorComentario
import com.example.slopshop.Entidades.Comentario
import com.example.slopshop.R
import com.example.slopshop.Usuario.Puntuaciones.FragmentPublicarComentario
import com.example.slopshop.databinding.FragmentComentariosProductoUBinding
import com.google.firebase.database.*

class FragmentComentariosProductoU : Fragment() {
    private var productId: String? = null
    private lateinit var binding: FragmentComentariosProductoUBinding
    private lateinit var listaComentarios: ArrayList<Comentario>
    private lateinit var adaptadorComentario: AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComentariosProductoUBinding.inflate(inflater, container, false)


        listaComentarios = arrayListOf()
        adaptadorComentario = AdaptadorComentario(requireContext(), listaComentarios)

        binding.recyclerComentarios.adapter = adaptadorComentario
        binding.recyclerComentarios.layoutManager = LinearLayoutManager(requireContext())


        productId?.let {
            cargarComentarios(it)
            cargarImagenProducto(it)
        }

        binding.btnPublicar.setOnClickListener {
            productId?.let { id ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.navFragment,
                        FragmentPublicarComentario.newInstance(id)
                    )
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                Toast.makeText(requireContext(), "Ha habido un error al cargar los comentarios", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun cargarComentarios(productId: String) {
        val refComentarios = FirebaseDatabase.getInstance()
            .getReference("Valoraciones")
            .orderByChild("productId")
            .equalTo(productId)

        refComentarios.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaComentarios.clear()
                for (comentarioSnap in snapshot.children) {
                    val comentario = comentarioSnap.getValue(Comentario::class.java)
                    comentario?.let { listaComentarios.add(it) }
                }
                adaptadorComentario.notifyDataSetChanged()

                val promedio = if (listaComentarios.isNotEmpty()) {
                    listaComentarios.map { it.puntuacion }.average().toFloat()
                } else 0f

                binding.ratingPromedio.rating = promedio
                binding.tvPromedioValor.text = String.format("%.1f", promedio)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun cargarImagenProducto(productId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
            .child(productId).child("Imagenes Producto")
            .limitToFirst(1)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (uri in snapshot.children) {
                    val imagenUrl = uri.child("imagenUrl").getValue(String::class.java)
                    if (!imagenUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imagenUrl)
                            .placeholder(R.drawable.icono_producto)
                            .into(binding.imagenProducto)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar imagen del producto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_PRODUCT_ID = "productId"
        @JvmStatic
        fun newInstance(productId: String) =
            FragmentComentariosProductoU().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_ID, productId)
                }
            }
    }
}