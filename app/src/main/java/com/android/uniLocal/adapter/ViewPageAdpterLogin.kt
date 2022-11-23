package com.android.uniLocal.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.uniLocal.fragmentos.ComentariosLugarFragment
import com.android.uniLocal.fragmentos.InfoLugarFragment
import com.android.uniLocal.fragmentos.LoginFragment
import com.android.uniLocal.fragmentos.RegistroFragment

class ViewPageAdpterLogin (var fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment.newInstance()
            else -> RegistroFragment.newInstance()
        }
    }
}