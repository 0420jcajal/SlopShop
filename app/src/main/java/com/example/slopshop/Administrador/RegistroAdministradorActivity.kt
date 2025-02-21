package com.example.slopshop.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Constantes
import com.example.slopshop.R
import com.example.slopshop.databinding.ActivityRegistroAdministradorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroAdministradorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroAdministradorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarA.setOnClickListener {
            validarInforfacion()
        }
    }
    private var nombre= ""
    private var email= ""
    private var contrasena=""
    private var confirmarContrasena=""
    private fun validarInforfacion() {
//RECUPERAMOS INFO
        nombre = binding.etNombreA.text.toString().trim()
        email = binding.etEmailA.text.toString().trim()
        contrasena = binding.etContrasenaA.text.toString().trim()
        confirmarContrasena = binding.etConfirmarContrasenaA.text.toString().trim()
//EXCEPCIONES
        if(nombre.isEmpty()){
            binding.etNombreA.error= "Nombre completo requerido"
            binding.etNombreA.requestFocus()
        }else if (email.isEmpty()){
            binding.etEmailA.error = "Email requerido"
            binding.etEmailA.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailA.error = "Email no valido"
            binding.etEmailA.requestFocus()
        } else if (contrasena.isEmpty()){
            binding.etContrasenaA.error= "La contraseña no puede estar en blanco"
            binding.etContrasenaA.requestFocus()
        } else if(contrasena.length<6){
            binding.etContrasenaA.error= "La contraseña debe tener 6 carates mínimo"
            binding.etContrasenaA.requestFocus()
        } else if(confirmarContrasena.isEmpty()){
            binding.etConfirmarContrasenaA.error = "Por favor confirme la contraseña"
            binding.etConfirmarContrasenaA.requestFocus()
        } else if(contrasena!=confirmarContrasena){
            binding.etConfirmarContrasenaA.error = "Las contraseñas no coinciden"
            binding.etConfirmarContrasenaA.requestFocus()
        } else{
            registrarVendedor()
        }
    }

    private fun registrarVendedor() {
        progressDialog.setMessage("Creando la cuenta del admin")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
            .addOnSuccessListener {
                insertarInfoEnBD()
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "El registro no se realizo correctamente por a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoEnBD(){
        progressDialog.setMessage("Guardando la informacion...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombre
        val emailBD = email
        val tiempoBD= Constantes().obtenerTiempo()

        val datosAdmin = HashMap<String, Any>()

        datosAdmin["uid"]= "$uidBD"
        datosAdmin["nombre"]= "$nombreBD"
        datosAdmin["email"]= "$emailBD"
        datosAdmin["tipoUsuario"]= "administrador"
        datosAdmin["tiempoRegisto"] = tiempoBD

        val references= FirebaseDatabase.getInstance().getReference("Usuarios")
        references.child(uidBD!!)
            .setValue(datosAdmin)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityAdministrador::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "La base no se registro correctamente debido a ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Auth", "Error al iniciar sesión: ${e.message}")
            }

    }
}