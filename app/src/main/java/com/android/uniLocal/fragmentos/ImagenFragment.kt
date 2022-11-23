package com.android.uniLocal.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.uniLocal.R
import com.android.uniLocal.databinding.FragmentImagenBinding
import com.bumptech.glide.Glide


class ImagenFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentImagenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("url_image")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImagenBinding.inflate(inflater, container, false)

        Glide.with( this )
            .load(param1)
            .into(binding.urlImage)

        return binding.root
    }

    companion object {
        fun newInstance(param1: String) =
            ImagenFragment().apply {
                arguments = Bundle().apply {
                    putString("url_image",param1)
                }
            }
    }
}