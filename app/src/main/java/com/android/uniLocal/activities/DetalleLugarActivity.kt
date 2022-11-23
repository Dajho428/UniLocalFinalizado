package com.android.uniLocal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.uniLocal.R
import com.android.uniLocal.adapter.ImagenesLocalPagerAdapter
import com.android.uniLocal.adapter.ViewPagerAdapter
import com.android.uniLocal.databinding.ActivityDetalleLugarBinding
import com.android.uniLocal.modelo.Lugar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetalleLugarActivity : AppCompatActivity() {
    lateinit var binding:ActivityDetalleLugarBinding
    private var codigoLugar:String?= ""
    private var lugar: Lugar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLugarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        codigoLugar = intent.extras!!.getString("codigo")
        Log.e("codigo", codigoLugar!!)

        if(codigoLugar != ""){
            Firebase.firestore
                .collection("lugares")
                .document(codigoLugar!!)
                .get()
                .addOnSuccessListener {
                    lugar = it.toObject(Lugar::class.java)
                    lugar!!.key = it.id
                    binding.nombreLugarDetalle.text=lugar!!.nombre
                    binding.viewPager.adapter = ViewPagerAdapter(this, codigoLugar)
                    binding.imagenLugar.adapter = ImagenesLocalPagerAdapter(this, lugar!!.imagenes)
                    TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
                        when (pos) {
                            0 -> tab.text = getString(R.string.info_lugar)
                            1 -> tab.text = getString(R.string.comentarios)
                        }
                    }.attach()
                }
        }
    }
}