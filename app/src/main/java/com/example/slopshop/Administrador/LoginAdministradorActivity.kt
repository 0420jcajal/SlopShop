package com.example.slopshop.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.databinding.ActivityLoginAdministradorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginAdministradorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAdministradorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginA.setOnClickListener {
            validarInfo()
        }

        binding.txtRegistrarA.setOnClickListener {
            startActivity(Intent(applicationContext, RegistroAdministradorActivity::class.java))
        }

    }

    private var email= ""
    private var contrasena=""
    private fun validarInfo() {
        email = binding.etEmailA.text.toString().trim()
        contrasena = binding.etContrasenaA.text.toString().trim()

        if (email.isEmpty()){
            binding.etEmailA.error= "Ingresa el email"
            binding.etEmailA.requestFocus()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailA.error= "Email no válido"
            binding.etEmailA.requestFocus()
        } else if (contrasena.isEmpty()){
            binding.etContrasenaA.error= "Ingresa la contraseña"
            binding.etContrasenaA.requestFocus()
        } else {
            loginVendedor()
        }
    }

    private fun loginVendedor() {
        progressDialog.setMessage("Realizando el login")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityAdministrador::class.java))
                finishAffinity()
                Toast.makeText(this, "Bienvenid@ de nuevo", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "No se pudo iniciar sesión debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}