package com.example.slopshop.Usuario

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Administrador.MainActivityAdministrador
import com.example.slopshop.R
import com.example.slopshop.SeleccionarTipoActivity
import com.example.slopshop.databinding.ActivityLoginUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginUsuarioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginUsuarioBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginU.setOnClickListener {
            validarInfo()
        }

        binding.txtRegistrarU.setOnClickListener {
            startActivity(Intent(this@LoginUsuarioActivity, RegistroUsuarioActivity::class.java))
        }

    }
    private var email=""
    private var contrasena=""
    private fun validarInfo() {
        email = binding.etEmailU.text.toString().trim()
        contrasena = binding.etContrasenaU.text.toString().trim()

        if (email.isEmpty()){
            binding.etEmailU.error= "Ingresa el email"
            binding.etEmailU.requestFocus()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailU.error= "Email no válido"
            binding.etEmailU.requestFocus()
        } else if (contrasena.isEmpty()){
            binding.etContrasenaU.error= "Ingresa la contraseña"
            binding.etContrasenaU.requestFocus()
        } else {
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        progressDialog.setMessage("Realizando el login")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener {
                progressDialog.dismiss()
                comprobarTipoUsuario()
                Toast.makeText(this, "Bienvenid@ de nuevo", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "No se pudo iniciar sesion debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
    private fun comprobarTipoUsuario(){
        val firebaseUser  = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, SeleccionarTipoActivity::class.java))
        }else{
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipoUsuario = snapshot.child("tipoUsuario").value
                        if(tipoUsuario == "administrador"){
                            startActivity(Intent(this@LoginUsuarioActivity, MainActivityAdministrador::class.java))
                            finishAffinity()
                        }else if (tipoUsuario == "cliente"){
                            startActivity(Intent(this@LoginUsuarioActivity, MainActivityUsuario::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}
