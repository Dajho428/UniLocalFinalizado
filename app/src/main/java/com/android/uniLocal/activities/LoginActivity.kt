package com.android.uniLocal.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.uniLocal.R
import com.android.uniLocal.bd.Ciudades
import com.android.uniLocal.databinding.ActivityLoginBinding
import com.android.uniLocal.modelo.Rol
import com.android.uniLocal.modelo.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()

        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null){
            makeRedirection(usuario)
        }else{
            binding.recuperarContrasena.setOnClickListener { recuperarContrasena() }
            binding.btnLogin.setOnClickListener { login() }
            binding.btnRegistro.setOnClickListener { irPantallaRegistro() }
            setContentView(binding.root)
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

    fun irPantallaRegistro() {
        startActivity(Intent(this, RegistroActivity::class.java))

    }

    fun recuperarContrasena() {
        startActivity(Intent(this, OlvidarContrasenaActivity::class.java))

    }

    fun login() {

        val correo = binding.emailUsuario.text.toString()
        var password = binding.passwordUsuario.text.toString()

        if (correo.isEmpty()) {
            binding.emailLayout.isErrorEnabled = true
            binding.emailLayout.error = getString(R.string.es_obligatorio)
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.es_obligatorio)
        } else {
            binding.passwordLayout.error = null
        }

        if (correo.isNotEmpty() && password.isNotEmpty()) {
            setDialog(true)
            var usuario: Usuario? = null
            Firebase.firestore
                .collection("usuarios")
                .whereEqualTo("correo", correo)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        usuario = doc.toObject(Usuario::class.java)
                        usuario!!.key = doc.id
                        break
                    }

                    try {
                        if (usuario != null) {
                            FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(correo, password)
                                .addOnCompleteListener { u ->
                                    if (u.isSuccessful) {
                                        val userLogin = FirebaseAuth.getInstance().currentUser
                                        if (userLogin != null) {
                                            Firebase.firestore
                                                .collection("usuarios")
                                                .document(userLogin.uid)
                                                .get()
                                                .addOnSuccessListener { u ->
                                                    makeRedirection(userLogin)
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            getString(R.string.datos_incorrectos),
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        setDialog(true)
                                    }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        getString(R.string.datos_incorrectos),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    setDialog(true)
                                }
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.datos_incorrectos),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            getString(R.string.datos_incorrectos),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        setDialog(true)
                    }
                }
        }
    }

    fun makeRedirection(user: FirebaseUser) {
        Firebase.firestore
            .collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { u ->
                val rol = u.toObject(Usuario::class.java)!!.rol
                val intent = when (rol) {
                    Rol.ADMINISTRADOR -> Intent(this, AdministradorActivity::class.java)
                    Rol.CLIENTE -> Intent(this, UserActivity::class.java)
                    Rol.MODERADOR -> Intent(this, ModeradorActivity::class.java)
                }
                setDialog(false)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Log.e("USUARIO", it.message.toString())
            }
    }

}