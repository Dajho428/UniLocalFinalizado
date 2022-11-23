package com.android.uniLocal.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.uniLocal.fragmentos.ImagenFragment

class ImagenesLocalPagerAdapter (var fragment: FragmentActivity, private var imagenes:ArrayList<String>): FragmentStateAdapter(fragment) {
    override fun getItemCount() = imagenes.size

    override fun createFragment(position: Int): Fragment {

        when(position){
            position -> return ImagenFragment.newInstance(imagenes[position])
        }
        return Fragment()

    }
}