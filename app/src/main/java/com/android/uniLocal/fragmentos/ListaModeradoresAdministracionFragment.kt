package com.android.uniLocal.fragmentos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.activities.CrearModeradorActivity
import com.android.uniLocal.adapter.ModeradorAdapter
import com.android.uniLocal.databinding.FragmentListaModeradoresAdministracionBinding
import com.android.uniLocal.modelo.EstadoUsuario
import com.android.uniLocal.modelo.Rol
import com.android.uniLocal.modelo.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ListaModeradoresAdministracionFragment : Fragment() {
    lateinit var binding: FragmentListaModeradoresAdministracionBinding
    lateinit var moderadores:ArrayList<Usuario>
    lateinit var adapter: ModeradorAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaModeradoresAdministracionBinding.inflate(inflater, container, false)
        moderadores = ArrayList()

        adapter = ModeradorAdapter(moderadores)
        binding.btnCrearMod.setOnClickListener { irCrearModerador() }
        binding.listaModeradores.adapter = adapter
        binding.listaModeradores.layoutManager  = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        return binding.root
    }

    fun irCrearModerador(){
        startActivity(Intent(requireContext(), CrearModeradorActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        moderadores.clear()
        Firebase.firestore
            .collection("usuarios")
            .whereEqualTo("estado", EstadoUsuario.ACEPTADO)
            .whereEqualTo("rol", Rol.MODERADOR)
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    val moderador = doc.toObject(Usuario::class.java)
                    moderador.key = doc.id
                    moderadores.add(moderador)
                }
                adapter.notifyDataSetChanged()
            }
    }

}