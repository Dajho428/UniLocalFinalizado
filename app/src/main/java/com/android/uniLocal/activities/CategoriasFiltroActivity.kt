package com.android.uniLocal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.adapter.CategoriaAdapter
import com.android.uniLocal.databinding.ActivityCategoriasFiltroBinding
import com.android.uniLocal.fragmentos.FavoritosFragment
import com.android.uniLocal.fragmentos.InicioFragment
import com.android.uniLocal.fragmentos.LugaresPorCategoriasFragment
import com.android.uniLocal.fragmentos.MisLugaresFragment
import com.android.uniLocal.modelo.Categoria
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CategoriasFiltroActivity : AppCompatActivity() {
    lateinit var binding:ActivityCategoriasFiltroBinding
    lateinit var categorias:ArrayList<Categoria>
    private var LUAGRES_CATEGORIA = "Lugares_por_categoria"
    var codigoCategoria:String? = ""
    lateinit var categoriaAdapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriasFiltroBinding.inflate(layoutInflater)
        categorias = ArrayList()

        codigoCategoria = intent.extras!!.getString("codigo")

        if(codigoCategoria != ""){
            reemplazarFragmento(1, LUAGRES_CATEGORIA)
        }

        categoriaAdapter = CategoriaAdapter(categorias, this)
        binding.listaCategorias.adapter = categoriaAdapter
        binding.listaCategorias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        setContentView(binding.root)
    }


    fun reemplazarFragmento(valor: Int,nombre:String) {

        var fragmento: Fragment = when (valor) {
            1 -> LugaresPorCategoriasFragment.newInstance(codigoCategoria!!)
            else -> Fragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.contenidoPrincipal.id, fragmento)
            .addToBackStack(nombre)
            .commit()
    }


    override fun onResume() {
        super.onResume()
        categorias.clear()
        Firebase.firestore
            .collection("categorias")
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    val categoria = doc.toObject(Categoria::class.java)
                    categoria.key = doc.id
                    categorias.add(categoria)
                }
                categoriaAdapter.notifyDataSetChanged()
            }
    }

    fun terminar(){

    }

    companion object{

        fun newInstance(){
        }

    }
}