package com.example.slopshop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Administrador.LoginAdministradorActivity
import com.example.slopshop.Usuario.LoginUsuarioActivity
import com.example.slopshop.databinding.ActivitySeleccionarTipoBinding

class SeleccionarTipoActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeleccionarTipoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySeleccionarTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tipoAdmin.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginAdministradorActivity::class.java))
        }
        binding.tipoUsuario.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginUsuarioActivity::class.java))
        }
    }
}