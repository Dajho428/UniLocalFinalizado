package com.android.uniLocal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.uniLocal.R
import com.android.uniLocal.bd.Usuarios
import com.android.uniLocal.databinding.ActivityNavHeaderBinding
import com.android.uniLocal.modelo.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class NavHeader : AppCompatActivity() {

    lateinit var binding: ActivityNavHeaderBinding
    private var codigoUsuario:Int = 0
    private var usuario:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val sp=getSharedPreferences("sesion",Context.MODE_PRIVATE)
        binding= ActivityNavHeaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        codigoUsuario=intent.extras!!.getInt("codigo_usuario")
        usuario= FirebaseAuth.getInstance().currentUser
        cargarUsuario()
           }
    fun cargarUsuario(){

        if (usuario!=null){
            Firebase.firestore
                .collection("usuarios")
                .document(usuario!!.uid)
                .get()
                .addOnSuccessListener {
                    binding.txtNombreMenu.text=it.toObject(Usuario::class.java)!!.nombre
                    binding.txtCorreoUsuario.text=it.toObject(Usuario::class.java)!!.correo

                }
        }
    }

}