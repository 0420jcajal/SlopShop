package com.example.slopshop.Administrador

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentPedidosA
import com.example.slopshop.Administrador.Bottom_Nav_Fragments_Administrador.FragmentProductosA
import com.example.slopshop.Administrador.Nav_Fragments_Administardor.FragmentInicioA
import com.example.slopshop.Administrador.Nav_Fragments_Administardor.FragmentResenas
import com.example.slopshop.Administrador.Nav_Fragments_Administardor.FragmentTiendaA
import com.example.slopshop.R
import com.example.slopshop.databinding.ActivityMainAdministradorBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivityAdministrador : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainAdministradorBinding
    private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth=FirebaseAuth.getInstance()
        comprobarSesion()

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

        cambiarFragmento(FragmentInicioA())
        binding.navigationView.setCheckedItem(R.id.opcionInicio_a)

    }

    private fun comprobarSesion() {
        if(firebaseAuth!!.currentUser==null){
            startActivity(Intent(applicationContext,RegistroAdministradorActivity::class.java))
        }else{
            Toast.makeText(applicationContext, "Bienvenido a SlopShop", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cambiarFragmento(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.opcionInicio_a->{
                cambiarFragmento(FragmentInicioA())
            }
            R.id.opcionTienda_a->{
                cambiarFragmento(FragmentTiendaA())
            }
            R.id.opcionReseñas_a->{
                cambiarFragmento(FragmentResenas())
            }
            R.id.opcionCerrarSesion_a->{
                Toast.makeText(applicationContext, "Cerraste sesión correctamente", Toast.LENGTH_SHORT).show()
            }
            R.id.opcionProductos_a->{
                cambiarFragmento(FragmentProductosA())
            }
            R.id.opcionPedidos_a->{
                cambiarFragmento(FragmentPedidosA())
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }
}
