package com.example.slopshop.Usuario.Nav_Fragments_Usuario

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.slopshop.R
import com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario.FragmentCatalogoU
import com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario.FragmentPedidosU
import com.example.slopshop.databinding.FragmentInicioUBinding


class FragmentInicioU : Fragment() {

    private lateinit var binding: FragmentInicioUBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInicioUBinding.inflate(inflater, container, false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.opcionCatalogo_u->{
                    cambiarFragment(FragmentCatalogoU())
                }
                R.id.opcionMisPedidos_u->{
                    cambiarFragment(FragmentPedidosU())
                }
            }
            true
        }

        cambiarFragment(FragmentCatalogoU())
        binding.bottomNavigation.selectedItemId = R.id.opcionCatalogo_u

        return binding.root
    }

    private fun cambiarFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment,fragment)
            .commit()
    }

}