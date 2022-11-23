package com.android.uniLocal.fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.uniLocal.databinding.FragmentRegistroBinding

class RegistroFragment : Fragment() {

    lateinit var binding: FragmentRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{

        fun newInstance():RegistroFragment{
            val args = Bundle()
            val fragmento = RegistroFragment()
            fragmento.arguments = args
            return fragmento
        }

    }
}