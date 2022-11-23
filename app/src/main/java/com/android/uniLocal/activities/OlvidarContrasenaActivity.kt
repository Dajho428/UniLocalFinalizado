package com.android.uniLocal.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.uniLocal.R
import com.android.uniLocal.databinding.ActivityOlvidarContrasenaBinding
import com.google.firebase.auth.FirebaseAuth

class OlvidarContrasenaActivity : AppCompatActivity() {
    lateinit var binding: ActivityOlvidarContrasenaBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlvidarContrasenaBinding.inflate(layoutInflater)
        binding.enviarCorreoRecuperacion.setOnClickListener { reestrablecerContrasena() }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()
        setContentView(binding.root)
    }

    fun reestrablecerContrasena(){
        setDialog(true)
       val correo = binding.correo.text.toString()
       if(correo.isNotEmpty()){
           FirebaseAuth.getInstance()
               .sendPasswordResetEmail(correo)
               .addOnSuccessListener {
                   Toast.makeText(this,getString(R.string.revisar_correo), Toast.LENGTH_LONG)
                       .show()
                   setDialog(false)
                   startActivity(Intent(this, LoginActivity::class.java))
                   finish()
               }.addOnFailureListener {
                   Toast.makeText(this,getString(R.string.correo_no_existe), Toast.LENGTH_LONG)
                       .show()
                   setDialog(false)
               }
       }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

}