package com.android.uniLocal.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.uniLocal.R
import com.android.uniLocal.adapter.ViewPageAdpterLogin
import com.android.uniLocal.adapter.ViewPagerAdapter
import com.android.uniLocal.databinding.ActivityMainBinding
import com.android.uniLocal.databinding.FragmentRegistroBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity :AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPagerMain.adapter = ViewPageAdpterLogin(this)
        TabLayoutMediator(binding.tabsMain, binding.viewPagerMain) { tab, pos ->
            when (pos) {
                0 -> tab.text = getString(R.string.txt_iniciar_sesion)
                1 -> tab.text = getString(R.string.txt_registrarse)
            }
        }.attach()
    }
}