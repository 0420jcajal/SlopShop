package com.example.slopshop.Administrador.Puntuaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.slopshop.Adaptador.AdaptadorComentario
import com.example.slopshop.Administrador.Productos.FragmentEditarProducto
import com.example.slopshop.Entidades.Comentario
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentComentariosProductoABinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentComentariosProductoA : Fragment() {

    private var productId: String? = null
    private var _binding: FragmentComentariosProductoABinding? = null
    private val binding get() = _binding!!

    private val listaComentarios = ArrayList<Comentario>()
    private lateinit var adaptadorComentario: AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComentariosProductoABinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adaptadorComentario = AdaptadorComentario(requireContext(), listaComentarios)
        binding.recyclerComentarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adaptadorComentario
        }


        productId?.let {
            cargarComentarios(it)
            cargarImagenProducto(it)
        } ?: run {
            Toast.makeText(requireContext(), "ID de producto no válido", Toast.LENGTH_SHORT).show()
        }

        binding.btnIrAEditar.setOnClickListener {
            productId?.let { id ->
                val fragmentEditar = FragmentEditarProducto.newInstance(id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.navFragment, fragmentEditar)
                    .addToBackStack(null)
                    .commit()
            } ?: Toast.makeText(requireContext(), "ID de producto no válido", Toast.LENGTH_SHORT).show()
        }
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
                    comentarioSnap.getValue(Comentario::class.java)?.let {
                        listaComentarios.add(it)
                    }
                }
                adaptadorComentario.notifyDataSetChanged()

                val promedio = if (listaComentarios.isNotEmpty()) {
                    listaComentarios.map { it.puntuacion }.average().toFloat()
                } else 0f

                binding.ratingPromedio.rating = promedio
                binding.tvPromedioValor.text = String.format("%.1f", promedio)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarImagenProducto(productId: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("Productos")
            .child(productId)
            .child("Imagenes Producto")
            .limitToFirst(1)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uri = snapshot.children.firstOrNull()
                val imagenUrl = uri?.child("imagenUrl")?.getValue(String::class.java)
                if (!imagenUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imagenUrl)
                        .placeholder(R.drawable.icono_producto)
                        .into(binding.imagenProducto)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar imagen del producto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCT_ID = "productId"

        @JvmStatic
        fun newInstance(productId: String): FragmentComentariosProductoA {
            val fragment = FragmentComentariosProductoA()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }
}
