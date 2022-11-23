package com.android.uniLocal.activities

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.adapter.LugarAdapter
import com.android.uniLocal.bd.Lugares
import com.android.uniLocal.databinding.ActivityModeradorBinding
import com.android.uniLocal.fragmentos.HistorialModeradorFragment
import com.android.uniLocal.fragmentos.InicioFragment
import com.android.uniLocal.fragmentos.ListaModeradoresAdministracionFragment
import com.android.uniLocal.fragmentos.NuevosLugaresFragment
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ModeradorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var binding: ActivityModeradorBinding
    private var MENU_INICIO= "Inicio"
    private var MENU_DESPLEGABLE= "menu_desplegable"
    private var MENU_PENDIENTES = "pendientes"
    private var MENU_HISTORIAL = "historial"
    private var usuario: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reemplazarFragmento(2, MENU_PENDIENTES)

        binding.barraInferior.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_menu_desplegable -> abrirMenu()
                R.id.menu_lugares_nuevos -> reemplazarFragmento(2, MENU_PENDIENTES)
                R.id.menu_historial -> reemplazarFragmento(3, MENU_HISTORIAL)
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
                    header.findViewById<TextView>(R.id.txt_correo_usuario).text = it.toObject(
                        Usuario::class.java)!!.correo
                }
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    fun reemplazarFragmento(valor: Int, nombre: String) {

        var fragmento: Fragment = when (valor) {
            1 -> InicioFragment()
            2 -> NuevosLugaresFragment()
            else -> HistorialModeradorFragment()
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
                MENU_DESPLEGABLE -> binding.navigationView.menu.getItem(0).isChecked = true
                MENU_PENDIENTES -> binding.navigationView.menu.getItem(1).isChecked = true
                else -> binding.navigationView.menu.getItem(2).isChecked = true
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navCerrarSesion -> cerrarSesion()
            R.id.navCategorias -> irCategoriasFiltro()
            R.id.navMiPerfil -> irMiPerfil()
            R.id.navInicio -> reemplazarFragmento(1, MENU_INICIO)
        }
        item.isChecked = true
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun irCategoriasFiltro(){
        val intent = Intent( this, CategoriasFiltroActivity::class.java )
        intent.putExtra("codigo", "")
        startActivity(intent)
    }

    fun irMiPerfil() {
        val intent = Intent(this, MiPerfilActivity::class.java)
        startActivity(intent)
    }

    fun abrirMenu() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

}