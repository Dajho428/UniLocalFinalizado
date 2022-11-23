package com.android.uniLocal.fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.uniLocal.activities.LoginActivity
import com.android.uniLocal.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var binding:FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{

        fun newInstance():LoginFragment{
            val args = Bundle()
            val fragmento = LoginFragment()
            fragmento.arguments = args
            return fragmento
        }

    }
}