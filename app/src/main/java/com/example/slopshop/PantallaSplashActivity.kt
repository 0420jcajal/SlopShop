package com.example.slopshop

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.slopshop.Administrador.MainActivityAdministrador
import com.example.slopshop.Usuario.MainActivityUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PantallaSplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        verBienvenida()
    }
    private fun verBienvenida(){
        object : CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                comprobarTipoUsuario()
            }

        }.start()

    }

    private fun comprobarTipoUsuario(){
        val firebaseUser  = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, MainActivityAdministrador::class.java))
        }else{
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipoUsuario = snapshot.child("tipoUsuario").value
                        if(tipoUsuario == "administrador"){
                            startActivity(Intent(this@PantallaSplashActivity, MainActivityAdministrador::class.java))
                            finishAffinity()
                        }else if (tipoUsuario == "usuario"){
                            startActivity(Intent(this@PantallaSplashActivity, MainActivityUsuario::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}