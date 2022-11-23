package com.android.uniLocal.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.activities.CrearLugarActivity
import com.android.uniLocal.adapter.LugarAdapter
import com.android.uniLocal.bd.Lugares
import com.android.uniLocal.databinding.FragmentMisLugaresBinding
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MisLugaresFragment : Fragment() {

    lateinit var binding: FragmentMisLugaresBinding
    lateinit var lista:ArrayList<Lugar>
    lateinit var adapter:LugarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMisLugaresBinding.inflate(inflater, container, false)

        binding.btnNuevoLugar.setOnClickListener { irACrearLugar() }
        lista = ArrayList()

        adapter = LugarAdapter(lista)
        binding.listaMisLugares.adapter = adapter
        binding.listaMisLugares.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

    private fun irACrearLugar(){
        startActivity( Intent(activity, CrearLugarActivity::class.java) )
    }

    override fun onResume() {
        super.onResume()
        lista.clear()
        val usuario = FirebaseAuth.getInstance().currentUser
        Firebase.firestore
            .collection("lugares")
            .whereEqualTo("idCreador",usuario!!.uid)
            .whereEqualTo("estado", EstadoLugar.ACEPTADO)
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    val lugar = doc.toObject(Lugar::class.java)
                    lugar.key = doc.id
                    lista.add(lugar)
                }
                adapter.notifyDataSetChanged()
            }

    }
}