package com.android.uniLocal.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.uniLocal.R
import com.android.uniLocal.bd.Usuarios
import com.android.uniLocal.databinding.ActivityRegistroBinding
import com.android.uniLocal.modelo.Rol
import com.android.uniLocal.modelo.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistroActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistroBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()

        binding.btnRegistro.setOnClickListener { registrar() }

    }

    fun registrar() {

        val nombre = binding.nombreUsuario.text.toString()
        val nickname = binding.nicknameUsuario.text.toString()
        val correo = binding.emailUsuario.text.toString()
        val password = binding.passwordUsuario.text.toString()

        setDialog(true)

        if (nombre.isEmpty()) {
            binding.nombreLayout.error = getString(R.string.debes_llenar)
        } else {
            binding.nombreLayout.error = null
        }

        if (nickname.isEmpty()) {
            binding.nicknameLayout.error = getString(R.string.debes_llenar)
        } else {
            binding.nicknameLayout.error = null
        }

        if (correo.isEmpty()) {
            binding.emailLayout.error = getString(R.string.debes_llenar)
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.debes_llenar)
        } else {
            binding.passwordLayout.error = null
        }



        if (correo.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty() && nickname.isNotEmpty()) {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(correo,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val usuario = FirebaseAuth.getInstance().currentUser
                        if(usuario != null){
                            verificarEmail(usuario)
                            val usuarioNuevo = Usuario( nombre, nickname, correo , Rol.CLIENTE)
                            Firebase.firestore
                                .collection("usuarios")
                                .document(usuario.uid)
                                .set(usuarioNuevo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, getString(R.string.registro_correcto_user), Toast.LENGTH_LONG).show()
                                    setDialog(false)
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }.addOnFailureListener {u->
                                    Toast.makeText(this, u.message.toString(), Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }.addOnFailureListener {
                    setDialog(false)
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

    private fun verificarEmail(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener(this){
            if(it.isSuccessful){
                Toast.makeText(baseContext, getString(R.string.correo_enviado), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(baseContext, getString(R.string.correo_error), Toast.LENGTH_LONG).show()
            }
        }
    }

}