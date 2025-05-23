package com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slopshop.Adaptador.AdaptadorProducto
import com.example.slopshop.Administrador.Productos.FragmentVerYEditarProducto
import com.example.slopshop.Entidades.Producto
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentProductosABinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentProductosA : Fragment() {

    private lateinit var binding : FragmentProductosABinding
    private lateinit var mContext : Context
    private lateinit var listaProductos : ArrayList<Producto>
    private lateinit var adaptadorProducto: AdaptadorProducto

    private var currentPage = 0
    private var tamañoPagina = 4

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentProductosABinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productosRV.layoutManager = GridLayoutManager(mContext, 2)
        adaptadorProducto = AdaptadorProducto(mContext, ArrayList())
        binding.productosRV.adapter = adaptadorProducto

        binding.productosRV.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = binding.productosRV.getChildViewHolder(view)
                    val position = holder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val productoSeleccionado = listaProductos[position]

                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.navFragment,
                                FragmentVerYEditarProducto.newInstance(productoSeleccionado.id)
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            override fun onChildViewDetachedFromWindow(view: View) {}
        })

        binding.btnPrev.setOnClickListener{
            if(currentPage>0){
                currentPage--
                actualizarPagina()
            }
        }
        binding.btnNext.setOnClickListener{
                currentPage++
                actualizarPagina()
        }

        listarProductos()
    }

    private fun actualizarPagina() {
        val from = currentPage * tamañoPagina
        val to = minOf(from + tamañoPagina, listaProductos.size)
        val sublista = if (from<listaProductos.size)
                listaProductos.subList(from, to)
        else
            emptyList()

        val itemsPagina = ArrayList(sublista)
        adaptadorProducto = AdaptadorProducto(mContext, itemsPagina)
        binding.productosRV.adapter = adaptadorProducto

        val totalPaginas = (listaProductos.size + tamañoPagina - 1)/tamañoPagina
        binding.txtPage.text= getString(
            R.string.paginaProducto,
            currentPage + 1,
            totalPaginas
        )

        binding.btnPrev.isEnabled = currentPage > 0
        binding.btnNext.isEnabled = currentPage + 1 < totalPaginas
    }

    private fun listarProductos() {
        listaProductos = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (uri in snapshot.children){
                    val modeloProducto = uri.getValue(Producto::class.java)
                    listaProductos.add(modeloProducto!!)
                }

                if (isAdded) {
                    currentPage = 0
                    actualizarPagina()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                //TODO LOG
            }

        })
    }

}