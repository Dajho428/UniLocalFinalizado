package com.android.uniLocal.fragmentos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.ERROR
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.uniLocal.R
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.activities.UserActivity
import com.android.uniLocal.bd.Categorias
import com.android.uniLocal.bd.Lugares
import com.android.uniLocal.databinding.FragmentInfoLugarBinding
import com.android.uniLocal.modelo.Ciudad
import com.android.uniLocal.modelo.EstadoUsuario
import com.android.uniLocal.modelo.Lugar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class InfoLugarFragment : Fragment(),OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    lateinit var binding:FragmentInfoLugarBinding
    private val defaultLocation = LatLng(4.550923, -75.6557201)
    private var lugar: Lugar? = null
    var usuario:FirebaseUser? = null
    lateinit var favoritos: ArrayList<String>
    private var codigoLugar:String? =""
    lateinit var gMap:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null){
            codigoLugar = requireArguments().getString("id_lugar")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoLugarBinding.inflate(inflater, container, false)
        usuario = FirebaseAuth.getInstance().currentUser
        if(codigoLugar!= ""){
            Firebase.firestore
                .collection("lugares")
                .document(codigoLugar!!)
                .get()
                .addOnSuccessListener {
                    val usuario = FirebaseAuth.getInstance().currentUser
                    lugar = it.toObject(Lugar::class.java)
                    cargarInformacion(lugar!!)
                    favoritos = ArrayList()
                    Firebase.firestore
                        .collection("usuarios")
                        .document(usuario!!.uid)
                        .collection("favoritos")
                        .get()
                        .addOnSuccessListener {f->
                            for(doc in f){
                                favoritos.add(doc.id)
                            }
                            val favorito = favoritos.firstOrNull{fa -> fa == lugar!!.key }
                            if(favorito != null){
                                binding.corazonFavorito.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_favorite_red_24))
                                binding.corazonFavorito.setOnClickListener{elimarFavoritos(lugar!!.key,usuario!!.uid)}
                            }else{
                                binding.corazonFavorito.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24))
                                binding.corazonFavorito.setOnClickListener{agregarFavoritos(lugar!!.key,usuario!!.uid)}
                            }
                        }
                }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa_ubicacion_lugar) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    fun eliminarLugar(){
        Firebase.firestore
            .collection("lugares")
            .document(codigoLugar!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(),getString(R.string.lugar_eliminado), Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(requireContext(), UserActivity::class.java))
            }
    }

    fun cargarInformacion(lugar:Lugar){
        Firebase.firestore
            .collection("ciudades")
            .document(lugar.idCiudad)
            .get()
            .addOnSuccessListener {
                val ciudad = it.toObject(Ciudad::class.java)
                binding.ubicacionLugar.text = ciudad!!.nombre
            }
        binding.descripcionLugar.text = lugar.descripcion
        binding.direccionLugar.text = lugar.direccion

        if(usuario!!.uid == lugar.idCreador){
            binding.eliminarLugar.visibility = View.VISIBLE
            binding.eliminarLugar.setOnClickListener { eliminarLugar() }
        }

        var telefonos = ""

        if(lugar.telefonos.isNotEmpty()) {
            for (tel in lugar.telefonos) {
                telefonos += "$tel, "
            }
            telefonos = telefonos.substring(0, telefonos.length - 2)
        }

        binding.telefonoLugar.text = telefonos

        var horarios = ""

        Log.e("Horario", lugar.horarios.toString())

        for( horario in lugar.horarios ){
            for(dia in horario.diaSemana){
                horarios += "$dia: ${horario.horaInicio}:00 - ${horario.horaCierre}:00\n"
            }
        }
        gMap.addMarker(MarkerOptions().position(LatLng(lugar!!.latitud.toDouble(), lugar!!.longitud.toDouble()))
            .title(lugar.nombre)
            .visible(true))!!
            .tag = lugar.key

        binding.horariosLugar.text = horarios

    }

    fun agregarFavoritos(codigoLugar:String, codigoUsuario:String){
        val fecha = HashMap<String, Date>()
        fecha.put("fecha", Date())
        Firebase.firestore
            .collection("usuarios")
            .document(codigoUsuario)
            .collection("favoritos")
            .document(codigoLugar)
            .set(fecha)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.lugar_fav), Toast.LENGTH_LONG)
                    .show()
                binding.corazonFavorito.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_favorite_red_24))
                binding.corazonFavorito.setOnClickListener{elimarFavoritos(codigoLugar, codigoUsuario)}
            }
    }

    fun elimarFavoritos(codigoLugar:String, codigoUsuario:String){
        Firebase.firestore
            .collection("usuarios")
            .document(codigoUsuario)
            .collection("favoritos")
            .document(codigoLugar)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.lugar_no_fav), Toast.LENGTH_LONG)
                    .show()
                binding.corazonFavorito.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_favorite_24))
                binding.corazonFavorito.setOnClickListener{agregarFavoritos(codigoLugar, codigoUsuario)}
            }
    }

    companion object{

        fun newInstance(codigoLugar:String):InfoLugarFragment{
            val args = Bundle()
            args.putString("id_lugar", codigoLugar)
            val fragmento = InfoLugarFragment()
            fragmento.arguments = args
            return fragmento
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.setOnInfoWindowClickListener (this)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15F))
    }


    override fun onInfoWindowClick(p0: Marker) {
        val intent = Intent(requireContext(), DetalleLugarActivity::class.java)
        intent.putExtra("codigo",p0.tag.toString())
        startActivity(intent)
    }


}