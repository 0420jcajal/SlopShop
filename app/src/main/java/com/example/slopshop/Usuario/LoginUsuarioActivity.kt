package com.example.slopshop.Usuario

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Administrador.MainActivityAdministrador
import com.example.slopshop.Constantes
import com.example.slopshop.R
import com.example.slopshop.SeleccionarTipoActivity
import com.example.slopshop.databinding.ActivityLoginUsuarioBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginUsuarioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginUsuarioBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleSignIn: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        val inicioSesionGoogle= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignIn = GoogleSignIn.getClient(this, inicioSesionGoogle)



        binding.btnLoginU.setOnClickListener {
            validarInfo()
        }

        binding.btnLoginGoogle.setOnClickListener{
            googleLogin()
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
                progressDialog.dismiss()
                Toast.makeText(this, "No se pudo iniciar sesion debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun googleLogin() {

        val singInIntent = googleSignIn.signInIntent
        googleSingInARL.launch(singInIntent)

    }

    private val googleSingInARL = registerForActivityResult(

        ActivityResultContracts.StartActivityForResult()){resultado->

        if(resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                    val cuenta = task.getResult(ApiException::class.java)
                    autenticacionGoogle(cuenta.idToken)

            }catch (e : Exception){
                Toast.makeText(this, "Ha habido un problema al iniciar sesión con google:  ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "La operacion se ha cancelado inesperadamente", Toast.LENGTH_SHORT).show()
        }


    }

    private fun autenticacionGoogle(idToken: String?) {

        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { resultadoAuth ->
                if(resultadoAuth.additionalUserInfo!!.isNewUser){

                    registrarGoogleBD()
                }else{
                    startActivity(Intent(this, MainActivityUsuario::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener las credenciales del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registrarGoogleBD() {
        progressDialog.setMessage("Registrando Usuario")

        val uid = firebaseAuth.uid
        val nombreUsuario = firebaseAuth.currentUser?.displayName
        val emailUsuario = firebaseAuth.currentUser?.email
        val tiempoRegistro = Constantes().obtenerTiempo()

        val datosUsuario = HashMap<String, Any>()

        datosUsuario["uid"]="${uid}"
        datosUsuario["nombre"]="${nombreUsuario}"
        datosUsuario["email"]="${emailUsuario}"
        datosUsuario["tiempoRegistro"]="${tiempoRegistro}"
        datosUsuario["imagen"]=""
        datosUsuario["tipoUsuario"]="cliente"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid!!)
            .setValue(datosUsuario)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityUsuario::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al registrar el usuario en la BD: ${e.message}", Toast.LENGTH_SHORT).show()
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
