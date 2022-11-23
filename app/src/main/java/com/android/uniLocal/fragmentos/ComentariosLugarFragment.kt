package com.android.uniLocal.fragmentos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.adapter.ComentarioAdapter
import com.android.uniLocal.adapter.LugarAdapter
import com.android.uniLocal.bd.Comentarios
import com.android.uniLocal.databinding.FragmentComentariosLugarBinding
import com.android.uniLocal.modelo.Comentario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ComentariosLugarFragment : Fragment() {

    lateinit var binding:FragmentComentariosLugarBinding
    var lista:ArrayList<Comentario> = ArrayList()
    private var codigoLugar:String? = ""
    private var colorPorDefecto:Int = 0
    var estrellas: Int = 0
    var usuario: FirebaseUser? = null
    lateinit var adapter: ComentarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null){
            codigoLugar = requireArguments().getString("id_lugar")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComentariosLugarBinding.inflate(inflater, container, false)
        lista = ArrayList()
        adapter = ComentarioAdapter(lista)

        if (codigoLugar != ""){
            colorPorDefecto = binding.s1.textColors.defaultColor
            usuario = FirebaseAuth.getInstance().currentUser
            generarComentarios()
        }

        for(i in 0 until binding.estrellasComentario.childCount){
            (binding.estrellasComentario[i] as TextView).setOnClickListener { presionarEstrella(i)}
        }
        binding.publicarComentario.setOnClickListener { hacerComentario() }

        return binding.root
    }

    fun generarComentarios(){
        Firebase.firestore
            .collection("lugares")
            .document(codigoLugar!!)
            .collection("comentarios")
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    val comentario = doc.toObject(Comentario::class.java)
                    lista.add(comentario)
                    binding.listaComentarios.adapter= adapter
                    binding.listaComentarios.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)

                }
            }
    }


    fun hacerComentario(){
        binding.sinComentarios.visibility = View.GONE
        val contenido = binding.contenidoComentario.text.toString()
        if(contenido.isNotEmpty() && estrellas > 0 ) {
            Log.e("Estrellas", estrellas.toString())
            val comentario = Comentario(contenido, usuario!!.uid, estrellas)
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Firebase.firestore
                    .collection("lugares")
                    .document(codigoLugar!!)
                    .collection("comentarios")
                    .add(comentario)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), getString(R.string.comentario_eviado), Toast.LENGTH_LONG)
                            .show()
                        lista.add(comentario)
                        adapter.notifyDataSetChanged()
                        limpiarFormulario()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }else{
            Toast.makeText(requireContext(), getString(R.string.marcar_fav), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun limpiarFormulario(){
        binding.contenidoComentario.setText("")
        borrarSeleccion()
        estrellas = 0
    }

    private fun presionarEstrella(pos:Int){
        estrellas=pos+1
        borrarSeleccion()
        for(i in 0..pos){
            (binding.estrellasComentario[i] as TextView).setTextColor(ContextCompat.getColor(requireContext(),R.color.yellow))
        }
    }

    private fun borrarSeleccion(){
        for(i in 0 until binding.estrellasComentario.childCount){
            (binding.estrellasComentario[i] as TextView).setTextColor(colorPorDefecto)
        }
    }

    companion object{

        fun newInstance(codigoLugar:String):ComentariosLugarFragment{
            val args = Bundle()
            args.putString("id_lugar", codigoLugar)
            val fragmento = ComentariosLugarFragment()
            fragmento.arguments = args
            return fragmento
        }

    }

}