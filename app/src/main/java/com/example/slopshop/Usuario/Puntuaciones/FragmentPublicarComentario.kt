package com.example.slopshop.Usuario.Puntuaciones

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentPublicarComentarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FragmentPublicarComentario : Fragment(R.layout.fragment_publicar_comentario) {

    private var _binding: FragmentPublicarComentarioBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.database.reference }

    private val productId: String by lazy {
        requireArguments().getString(ARG_PRODUCTO_ID) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPublicarComentarioBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setupProgressDialog()
        setupUI()
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Espere por favor...")
            setCanceledOnTouchOutside(false)
        }
    }

    private fun setupUI() {

        binding.ratingBar.setOnRatingBarChangeListener { _: RatingBar, rating: Float, _: Boolean ->
            binding.tvRatingValue.text = String.format("%.1f", rating)
        }

        binding.btnPublicar.setOnClickListener {
            publicarComentario()
        }
    }

    private fun publicarComentario() {
        val titulo = binding.etTitulo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val puntuacion = binding.ratingBar.rating
        val uid = auth.currentUser?.uid

        if (titulo.isEmpty()) {
            binding.etTitulo.error = "Introduce un título"
            return
        }
        if (descripcion.isEmpty()) {
            binding.etDescripcion.error = "Introduce una descripción"
            return
        }
        if (uid == null) {
            Toast.makeText(requireContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog.setMessage("Publicando valoración del producto...")
        progressDialog.show()


        val valoracionRef = db.child("Valoraciones").push()
        val valoracionId = valoracionRef.key ?: System.currentTimeMillis().toString()

        val valoracionData = mapOf(
            "id"          to valoracionId,
            "titulo"      to titulo,
            "descripcion" to descripcion,
            "puntuacion"  to puntuacion,
            "uidUsuario"  to uid,
            "productId"   to productId,
            "timestamp"   to System.currentTimeMillis()
        )

        valoracionRef
            .setValue(valoracionData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Valoración publicada", Toast.LENGTH_SHORT).show()

                binding.etTitulo.setText("")
                binding.etDescripcion.setText("")
                binding.ratingBar.rating = 0f
                binding.tvRatingValue.text = "0"
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCTO_ID = "PRODUCT_ID"

        fun newInstance(productId: String): FragmentPublicarComentario {
            return FragmentPublicarComentario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCTO_ID, productId)
                }
            }
        }
    }
}
