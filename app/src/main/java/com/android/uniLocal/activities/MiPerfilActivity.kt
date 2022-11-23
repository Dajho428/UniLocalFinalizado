package com.android.uniLocal.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.android.uniLocal.R
import com.android.uniLocal.databinding.ActivityMiPerfilBinding
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MiPerfilActivity : AppCompatActivity() {
    lateinit var binding:ActivityMiPerfilBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var codigoArchivo: Int = 0
    var imagen:String = ""
    lateinit var dialog: Dialog
    var usuario:FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)

        usuario = FirebaseAuth.getInstance().currentUser
        if(usuario!= null){
            cargarInformacionPerfil()
        }
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ) {
            onActivityResult(it.resultCode, it)
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()

        binding.btnElegirImagen.setOnClickListener { seleccionarFoto() }

        setContentView(binding.root)
    }

    fun cargarInformacionPerfil(){
        Firebase.firestore
            .collection("usuarios")
            .document(usuario!!.uid)
            .get()
            .addOnSuccessListener {
                  val usuario = it.toObject(Usuario::class.java)
                  usuario!!.key = it.id
                  val imagenActual = binding.imagenSeleccionadaPerfil
                  if(usuario.imagen != ""){
                      Glide.with( baseContext )
                          .load(usuario.imagen)
                          .into(imagenActual)
                  }
                  binding.btnGuardarCambios.setOnClickListener { actualizarInformacionUsuario(usuario) }
                  binding.nombreUsuarioLayout.hint= usuario!!.nombre
                  binding.campoNickname.hint = usuario!!.nickname
            }
    }

    private fun onActivityResult(resultCode:Int, result: ActivityResult){
        if( resultCode == Activity.RESULT_OK ){
            setDialog(true)
            val fecha = Date()
            val datatime = "/p-${fecha.time}.jpg"
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child(datatime)
            if( codigoArchivo == 2 ){
                val data = result.data
                if(data!=null){
                    val selectedImageUri: Uri? = data.data
                    if(selectedImageUri!=null){
                        storageRef.putFile(selectedImageUri).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener {
                                dibujarImagen(it)
                                setDialog(false)
                            }
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG).show()
                            setDialog(false)
                        }
                    }
                }
            }
        }
    }

    fun actualizarInformacionUsuario(usuario: Usuario?){
        var nombre = binding.nombreUsuarioSesion.text.toString()
        var nickname = binding.nicknameUsuario.text.toString()

        setDialog(true)

        if(nombre.isEmpty()){
            nombre = binding.nombreUsuarioLayout.hint.toString()
        }

        if(nickname.isEmpty()){
            nickname = binding.campoNickname.hint.toString()
        }

        if (imagen == ""){
            imagen = usuario!!.imagen
        }

        if(nombre.isNotEmpty() && nickname.isNotEmpty() && imagen != ""){
            val usuarioActualizado = Usuario(nombre, nickname, usuario!!.correo, usuario.rol )
            usuarioActualizado.estado = usuario.estado
            usuarioActualizado.imagen = imagen
            Firebase.firestore
                .collection("usuarios")
                .document(usuario.key)
                .set(usuarioActualizado)
                .addOnSuccessListener {
                    setDialog(false)
                    Toast.makeText(this, getString(R.string.perfil_actualizado), Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
        }

    }

    fun dibujarImagen(url: Uri){
        setDialog(false)
        imagen = url.toString()
        val image = binding.imagenSeleccionadaPerfil

        Glide.with( baseContext )
            .load(url)
            .into(image)
    }

    fun seleccionarFoto(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        codigoArchivo = 2
        resultLauncher.launch(i)
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

}