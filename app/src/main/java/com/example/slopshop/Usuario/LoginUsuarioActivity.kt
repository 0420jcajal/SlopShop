package com.example.slopshop.Usuario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.R
import com.example.slopshop.databinding.ActivityLoginUsuarioBinding

class LoginUsuarioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtRegistrarU.setOnClickListener {
            startActivity(Intent(this@LoginUsuarioActivity, RegistroUsuarioActivity::class.java))
        }

    }
}