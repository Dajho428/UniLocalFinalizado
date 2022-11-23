package com.android.uniLocal.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.uniLocal.R
import com.android.uniLocal.databinding.ActivityCrearModeradorBinding
import com.android.uniLocal.modelo.Rol
import com.android.uniLocal.modelo.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CrearModeradorActivity : AppCompatActivity() {
    lateinit var binding:ActivityCrearModeradorBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearModeradorBinding.inflate(layoutInflater)
        binding.btnCrearMod.setOnClickListener { registrarModerador() }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()
        setContentView(binding.root)
    }

    fun registrarModerador(){
        val nombre = binding.moderadorNombre.text.toString()
        val nickname = binding.nicknameModerador.text.toString()
        val correo = binding.moderadorCorreo.text.toString()
        val password = binding.moderadorPassword.text.toString()

        setDialog(true)

        if(nombre.isEmpty()){
            binding.nombreLayout.error = getString(R.string.debes_llenar)
        }else{
            binding.nombreLayout.error = null
        }

        if(nickname.isEmpty()){
            binding.nicknameLayout.error = getString(R.string.debes_llenar)
        }else{
            binding.nicknameLayout.error = null
        }

        if(correo.isEmpty()){
            binding.correoLayout.error = getString(R.string.debes_llenar)
        }else{
            binding.correoLayout.error = null
        }

        if(password.isEmpty()){
            binding.passwordLayout.error = getString(R.string.debes_llenar)
        }else{
            binding.passwordLayout.error = null
        }

        if(nombre.isNotEmpty() && nickname.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty() ){
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(correo,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val moderador = FirebaseAuth.getInstance().currentUser
                        if(moderador != null){
                            verificarEmail(moderador)
                            val moderadorNuevo = Usuario( nombre, nickname, correo, Rol.MODERADOR)
                            Firebase.firestore
                                .collection("usuarios")
                                .document(moderador.uid)
                                .set(moderadorNuevo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, getString(R.string.registro_correcto), Toast.LENGTH_LONG).show()
                                    setDialog(false)
                                    startActivity(Intent(this, AdministradorActivity::class.java))
                                    finish()
                                }
                        }
                    }
                }.addOnFailureListener {
                    setDialog(false)
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                }
        }else{
            setDialog(false)
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