package com.example.slopshop.Usuario

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.slopshop.R
import com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario.FragmentCatalogoU
import com.example.slopshop.Usuario.Bottom_Nav_Fragments_Usuario.FragmentPedidosU
import com.example.slopshop.Usuario.Nav_Fragments_Usuario.FragmentInicioU
import com.example.slopshop.Usuario.Nav_Fragments_Usuario.FragmentMiPerfilU
import com.example.slopshop.databinding.ActivityMainUsuarioBinding
import com.google.android.material.navigation.NavigationView

class MainActivityUsuario : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.abrir_drawer,
            R.string.cerrar_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cambiarFragment(FragmentInicioU())

    }

    private fun cambiarFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.opcionInicio_u->{
                cambiarFragment(FragmentInicioU())
            }
            R.id.opcionPerfil_u->{
                cambiarFragment(FragmentMiPerfilU())
            }
            R.id.opcionCerrarSesion_u->{
                Toast.makeText(applicationContext, "Cerraste sesión correctamente", Toast.LENGTH_SHORT).show()
            }
            R.id.opcionCatalogo_u->{
                cambiarFragment(FragmentCatalogoU())
            }
            R.id.opcionMisPedidos_u->{
                cambiarFragment(FragmentPedidosU())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}