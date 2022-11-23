package com.android.uniLocal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.uniLocal.R
import com.android.uniLocal.databinding.ActivityDetallesModBinding
import com.android.uniLocal.modelo.EstadoUsuario
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetallesModActivity : AppCompatActivity() {
    lateinit var binding:ActivityDetallesModBinding
    var codigoModerador: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesModBinding.inflate(layoutInflater)

        codigoModerador = intent.extras!!.getString("code")
        if(codigoModerador!= ""){
            cargarInformacionModerador()
            binding.btnEliminarModerador.setOnClickListener { eliminarModerador() }
        }
        setContentView(binding.root)
    }

    fun eliminarModerador(){
        Firebase.firestore
            .collection("usuarios")
            .document(codigoModerador!!)
            .get()
            .addOnSuccessListener {
                val moderadorEliminado = it.toObject(Usuario::class.java)
                moderadorEliminado!!.estado = EstadoUsuario.RECHAZADO
                Firebase.firestore
                    .collection("usuarios")
                    .document(codigoModerador!!)
                    .set(moderadorEliminado)
                    .addOnSuccessListener {
                        Toast.makeText(this,getString(R.string.moderador_eliminado), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, AdministradorActivity::class.java))
                        finish()
                    }
            }
    }

    fun cargarInformacionModerador(){
        Firebase.firestore
            .collection("usuarios")
            .document(codigoModerador!!)
            .get()
            .addOnSuccessListener {
                val moderadorDatos = it.toObject(Usuario::class.java)
                moderadorDatos!!.key = it.id
                if(moderadorDatos.imagen != ""){
                    Glide.with(this)
                        .load(moderadorDatos.imagen)
                        .into(binding.imageMod)
                }
                binding.nombre.text = moderadorDatos.nombre
                binding.nombreMod.text = moderadorDatos.nombre
                binding.nickname.text = moderadorDatos.nickname
                binding.correo.text = moderadorDatos.correo
            }
    }

}