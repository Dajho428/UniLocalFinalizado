package com.android.uniLocal.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.android.uniLocal.R
import com.android.uniLocal.fragmentos.*
import com.android.uniLocal.databinding.ActivityUserBinding
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class UserActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityUserBinding
    private var MENU_INICIO = "inicio"
    private var MENU_MIS_LUGARES = "mis_lugares"
    private var MENU_FAVORITOS = "favoritos"
    private var codigoUsuario: Int = 0
    private var usuario: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)

        reemplazarFragmento(1, MENU_INICIO)

        binding.barraInferior.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_menu_desplegable -> abrirMenu()
                R.id.menu_mis_lugares -> reemplazarFragmento(2,MENU_MIS_LUGARES)
                R.id.menu_favoritos -> reemplazarFragmento(3,MENU_FAVORITOS)
            }
            true
        }
        val header = binding.navigationView.getHeaderView(0)
        usuario = FirebaseAuth.getInstance().currentUser
        if(usuario!=null) {
            Firebase.firestore
                .collection("usuarios")
                .document(usuario!!.uid)
                .get()
                .addOnSuccessListener {
                    val imagen = header.findViewById<CircleImageView>(R.id.imagen_perfil_actual_usuario)
                    val imagenGuardada = it.toObject(Usuario::class.java)!!.imagen
                    if(imagenGuardada!= ""){
                        Glide.with( baseContext )
                            .load(it.toObject(Usuario::class.java)!!.imagen)
                            .into(imagen)
                    }
                    header.findViewById<TextView>(R.id.txt_nombre_menu).text = it.toObject(Usuario::class.java)!!.nombre
                    header.findViewById<TextView>(R.id.txt_correo_usuario).text = it.toObject(Usuario::class.java)!!.correo
                }
        }
        binding.navigationView.setNavigationItemSelectedListener (this)
        setContentView(binding.root)

    }


    fun reemplazarFragmento(valor: Int, nombre: String) {

        var fragmento: Fragment = when (valor) {
            1 ->InicioFragment()
            2 -> MisLugaresFragment()
            else -> FavoritosFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.contenidoPrincipal.id, fragmento)
            .addToBackStack(nombre)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val count = supportFragmentManager.backStackEntryCount

        if (count > 0) {
            val nombre = supportFragmentManager.getBackStackEntryAt(count - 1).name
            when (nombre) {
                MENU_INICIO -> binding.navigationView.menu.getItem(0).isChecked = true
                MENU_MIS_LUGARES -> binding.navigationView.menu.getItem(1).isChecked = true
                else -> binding.navigationView.menu.getItem(2).isChecked = true
            }
        }
    }

    fun irCategoriasFiltro(){
        val intent = Intent( this, CategoriasFiltroActivity::class.java )
        intent.putExtra("codigo", "")
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navCerrarSesion -> cerrarSesion()
            R.id.navMiPerfil -> irMiPerfil()
            R.id.navCategorias -> irCategoriasFiltro()
            R.id.navInicio -> reemplazarFragmento(1, MENU_INICIO)
        }
        item.isChecked = true
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }


    fun irMiPerfil() {
        val intent = Intent(this, MiPerfilActivity::class.java)
        startActivity(intent)
    }

    fun abrirMenu() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

}