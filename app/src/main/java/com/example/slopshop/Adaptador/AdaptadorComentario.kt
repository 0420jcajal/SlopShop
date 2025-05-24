package com.example.slopshop.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Entidades.Comentario
import com.example.slopshop.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorComentario(
    private val context: Context,
    private val listaComentarios: ArrayList<Comentario>
) : RecyclerView.Adapter<AdaptadorComentario.HolderComentario>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComentario {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false)
        return HolderComentario(view)
    }

    override fun getItemCount(): Int = listaComentarios.size

    override fun onBindViewHolder(holder: HolderComentario, position: Int) {
        val comentario = listaComentarios[position]

        holder.titulo.text = comentario.titulo
        holder.descripcion.text = comentario.descripcion
        holder.ratingBar.rating = comentario.puntuacion

        val uidUsuario = comentario.uidUsuario
        val refUsuario = FirebaseDatabase.getInstance().getReference("Usuarios").child(uidUsuario)
        refUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = snapshot.child("nombre").value?.toString() ?: "Usuario"
                holder.nombreUsuario.text = nombre
            }

            override fun onCancelled(error: DatabaseError) {
                holder.nombreUsuario.text = "Usuario"
            }
        })

        // TODO: Carga imagen de usuario con Glide o Picasso si tienes URL en Comentario

    }

    inner class HolderComentario(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenUsuario: ImageView = itemView.findViewById(R.id.imagenUsuario)
        val titulo: TextView = itemView.findViewById(R.id.tituloComentario)
        val descripcion: TextView = itemView.findViewById(R.id.textoComentario)
        val ratingBar: RatingBar = itemView.findViewById(R.id.estrellasComentario)
        val nombreUsuario: TextView = itemView.findViewById(R.id.nombreUsuario) // ← NUEVO

    }
}
