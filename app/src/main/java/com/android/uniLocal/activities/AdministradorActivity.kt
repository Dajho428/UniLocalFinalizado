package com.android.uniLocal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.adapter.ModeradorAdapter
import com.android.uniLocal.databinding.ActivityAdministradorBinding
import com.android.uniLocal.fragmentos.FavoritosFragment
import com.android.uniLocal.fragmentos.InicioFragment
import com.android.uniLocal.fragmentos.ListaModeradoresAdministracionFragment
import com.android.uniLocal.fragmentos.MisLugaresFragment
import com.android.uniLocal.modelo.EstadoUsuario
import com.android.uniLocal.modelo.Rol
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class AdministradorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityAdministradorBinding
    private var MENU_INICIO= "menu_inicio"
    private var MENU_DESPLEGABLE= "menu_desplegable"
    private var MENU_GESTION_MOD = "gestion_mod"
    lateinit var adapter:ModeradorAdapter
    private var usuario: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdministradorBinding.inflate(layoutInflater)

        reemplazarFragmento(2, MENU_GESTION_MOD)

        binding.barraInferior.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_menu_desplegable -> abrirMenu()
                R.id.menu_mod -> reemplazarFragmento(2, MENU_GESTION_MOD)
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
        binding.navigationView.setNavigationItemSelectedListener(this)
        setContentView(binding.root)
    }

    fun reemplazarFragmento(valor: Int, nombre: String) {

        var fragmento: Fragment = when (valor) {
            1 -> InicioFragment()
            else -> ListaModeradoresAdministracionFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.contenidoPrincipal.id, fragmento)
            .addToBackStack(nombre)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navInicio -> reemplazarFragmento(1, MENU_INICIO)
            R.id.navMiPerfil ->irMiPerfil()
            R.id.navCategorias -> irCategoriasFiltro()
            R.id.navCerrarSesion -> cerrarSesion()
        }
        item.isChecked = true
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun irMiPerfil() {
        val intent = Intent(this, MiPerfilActivity::class.java)
        startActivity(intent)
    }
    fun irCategoriasFiltro(){
        val intent = Intent( this, CategoriasFiltroActivity::class.java )
        intent.putExtra("codigo", "")
        startActivity(intent)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val count = supportFragmentManager.backStackEntryCount

        if (count > 0) {
            val nombre = supportFragmentManager.getBackStackEntryAt(count - 1).name
            when (nombre) {
                MENU_DESPLEGABLE -> binding.navigationView.menu.getItem(0).isChecked = true
                MENU_GESTION_MOD -> binding.navigationView.menu.getItem(1).isChecked = true
                else -> binding.navigationView.menu.getItem(2).isChecked = true
            }
        }

    }

    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun abrirMenu() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
}