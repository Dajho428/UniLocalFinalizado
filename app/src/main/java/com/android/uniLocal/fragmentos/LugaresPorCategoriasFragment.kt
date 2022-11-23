package com.android.uniLocal.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.uniLocal.R
import com.android.uniLocal.adapter.LugarAdapter
import com.android.uniLocal.databinding.FragmentLugaresPorCategoriasBinding
import com.android.uniLocal.modelo.Categoria
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LugaresPorCategoriasFragment : Fragment() {
    lateinit var binding:FragmentLugaresPorCategoriasBinding
    lateinit var lugares:ArrayList<Lugar>
    lateinit var lugarAdapter: LugarAdapter
    var codigoCategoria:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            codigoCategoria = requireArguments().getString("codigoCategoria")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLugaresPorCategoriasBinding.inflate(inflater, container, false)
        lugares = ArrayList()

        lugarAdapter = LugarAdapter(lugares)
        binding.listaLugaresPorCategorias.adapter = lugarAdapter
        binding.listaLugaresPorCategorias.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lugares.clear()
        Firebase.firestore
            .collection("lugares")
            .whereEqualTo("idCategoria", codigoCategoria)
            .whereEqualTo("estado" , EstadoLugar.ACEPTADO)
            .get()
            .addOnSuccessListener {
                for(doc in it ){
                    val lugar = doc.toObject(Lugar::class.java)
                    lugar.key = doc.id
                    lugares.add(lugar)
                }
                lugarAdapter.notifyDataSetChanged()
            }
    }


    companion object{

        fun newInstance(codigoCategoria:String):LugaresPorCategoriasFragment{
            val args = Bundle()
            args.putString("codigoCategoria", codigoCategoria)
            val fragmento = LugaresPorCategoriasFragment()
            fragmento.arguments = args
            return fragmento
        }

    }

}