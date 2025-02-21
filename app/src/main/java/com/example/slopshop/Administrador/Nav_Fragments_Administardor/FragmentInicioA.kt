package com.example.slopshop.Administrador.Nav_Fragments_Administardor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentPedidosA
import com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentProductosA
import com.example.slopshop.Administrador.Productos.AgregarProductoActivity
import com.example.slopshop.R
import com.example.slopshop.databinding.FragmentInicioABinding

class FragmentInicioA : Fragment() {

    private lateinit var binding: FragmentInicioABinding
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        mContext= context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInicioABinding.inflate(inflater, container, false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.opcionProductos_a->{
                    cambiarFragmento(FragmentProductosA())
                }
                R.id.opcionPedidos_a->{
                    cambiarFragmento(FragmentPedidosA())
                }
            }
            true
        }

        cambiarFragmento(FragmentProductosA())
        binding.bottomNavigation.selectedItemId = R.id.opcionProductos_a

        binding.addFab.setOnClickListener{
            Toast.makeText(mContext,
                "Accediendo a la creación de producto",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(context, AgregarProductoActivity::class.java))
        }

        return binding.root
    }

    private fun cambiarFragmento(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }

}