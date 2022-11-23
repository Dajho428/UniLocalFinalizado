package com.android.uniLocal.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.adapter.LugarRevisadoAdapter
import com.android.uniLocal.databinding.FragmentHistorialModeradorBinding
import com.android.uniLocal.modelo.Lugar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistorialModeradorFragment : Fragment() {
    lateinit var binding:FragmentHistorialModeradorBinding
    lateinit var listaLugaresRevisados: ArrayList<Lugar>
    var moderador: FirebaseUser? = null
    lateinit var adapterLugarRevisado: LugarRevisadoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistorialModeradorBinding.inflate(inflater, container, false)
        listaLugaresRevisados = ArrayList()
        moderador = FirebaseAuth.getInstance().currentUser
        if(moderador!= null){
            Firebase.firestore
                .collection("lugares")
                .whereEqualTo("idModerador",moderador!!.uid )
                .get()
                .addOnSuccessListener {
                    for (doc in it){
                        val lugar = doc.toObject(Lugar::class.java)
                        lugar.key = doc.id
                        listaLugaresRevisados.add(lugar)
                    }
                    adapterLugarRevisado = LugarRevisadoAdapter(listaLugaresRevisados)
                    binding.listaRegistro.adapter = adapterLugarRevisado
                    binding.listaRegistro.layoutManager  = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                }
        }
        return binding.root
    }

}