package com.android.uniLocal.fragmentos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.android.uniLocal.R
import com.android.uniLocal.activities.ResultadoBusqueda
import com.android.uniLocal.databinding.ActivityAdministradorBinding.inflate
import com.android.uniLocal.databinding.FragmentBarraBusquedaBinding
import com.android.uniLocal.databinding.FragmentInicioBinding


class BarraBusquedaFragment : Fragment() {
    lateinit var binding: FragmentBarraBusquedaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBarraBusquedaBinding.inflate(inflater, container, false)
        binding.textoBusqueda.setOnEditorActionListener { textView, i, keyEvent ->
            if( i == EditorInfo.IME_ACTION_SEARCH){

                val busqueda = binding.textoBusqueda.text.toString()

                if(busqueda.isNotEmpty()) {
                    val intent = Intent(activity, ResultadoBusqueda::class.java)
                    intent.putExtra("texto", busqueda)
                    startActivity(intent)
                }

            }
            true
        }
        return binding.root
    }
}