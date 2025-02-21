package com.example.slopshop.Usuario

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Administrador.MainActivityAdministrador
import com.example.slopshop.Constantes
import com.example.slopshop.R
import com.example.slopshop.databinding.ActivityRegistroUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroUsuarioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarU.setOnClickListener {
            validarInfo()
        }
    }

    private var nombre =""
    private var email=""
    private var contrasena=""
    private var confirmarContrasena=""
    private fun validarInfo() {
        //RECUPERAMOS INFO
        nombre = binding.etNombreU.text.toString().trim()
        email = binding.etEmailU.text.toString().trim()
        contrasena = binding.etContrasenaU.text.toString().trim()
        confirmarContrasena = binding.etConfirmarContrasenaU.text.toString().trim()
//EXCEPCIONES
        if(nombre.isEmpty()){
            binding.etNombreU.error= "Nombre completo requerido"
            binding.etNombreU.requestFocus()
        }else if (email.isEmpty()){
            binding.etEmailU.error = "Email requerido"
            binding.etEmailU.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailU.error = "Email no valido"
            binding.etEmailU.requestFocus()
        } else if (contrasena.isEmpty()){
            binding.etContrasenaU.error= "La contraseña no puede estar en blanco"
            binding.etContrasenaU.requestFocus()
        } else if(contrasena.length<6){
            binding.etContrasenaU.error= "La contraseña debe tener 6 carates mínimo"
            binding.etContrasenaU.requestFocus()
        } else if(confirmarContrasena.isEmpty()){
            binding.etConfirmarContrasenaU.error = "Por favor confirme la contraseña"
            binding.etConfirmarContrasenaU.requestFocus()
        } else if(contrasena!=confirmarContrasena){
            binding.etConfirmarContrasenaU.error = "Las contraseñas no coinciden"
            binding.etConfirmarContrasenaU.requestFocus()
        }else{
            registrarCliente()
        }

    }

    private fun registrarCliente() {
        progressDialog.setMessage("Creando la cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "El registro no se realizo correctamente por a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoBD(){
        progressDialog.setMessage("Guardando la informacion...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombre
        val emailBD = email
        val tiempoBD= Constantes().obtenerTiempo()

        val datosUsuario = HashMap<String, Any>()

        datosUsuario["uid"]= "$uidBD"
        datosUsuario["nombre"]= "$nombreBD"
        datosUsuario["email"]= "$emailBD"
        datosUsuario["tipoUsuario"]= "cliente"
        datosUsuario["imagen"]=""
        datosUsuario["tiempoRegisto"] = tiempoBD

        val references= FirebaseDatabase.getInstance().getReference("Usuarios")
        references.child(uidBD!!)
            .setValue(datosUsuario)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityUsuario::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "La base no se registro correctamente debido a ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Auth", "Error al iniciar sesión: ${e.message}")
            }

    }

}
