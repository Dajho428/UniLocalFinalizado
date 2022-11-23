package com.android.uniLocal.fragmentos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.android.uniLocal.R
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.activities.ResultadoBusqueda
import com.android.uniLocal.databinding.FragmentInicioBinding
import com.android.uniLocal.modelo.EstadoUsuario
import com.android.uniLocal.modelo.Lugar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class InicioFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{
    private var tienePermiso = false
    private val defaultLocation = LatLng(4.550923, -75.6557201)
    lateinit var binding: FragmentInicioBinding
    lateinit var gMap:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa_google) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.uiSettings.isZoomControlsEnabled = true
        try {
            if (tienePermiso) {
                gMap.isMyLocationEnabled = true
                gMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                gMap.isMyLocationEnabled = false
                gMap.uiSettings.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        Firebase.firestore
            .collection("lugares")
            .whereEqualTo("estado",EstadoUsuario.ACEPTADO)
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    var lugar = doc.toObject(Lugar::class.java)
                    lugar.key = doc.id
                    gMap.addMarker(MarkerOptions().position(LatLng(lugar!!.latitud.toDouble(), lugar!!.longitud.toDouble()))
                        .title(lugar.nombre)
                        .visible(true))!!
                        .tag = lugar.key
                }
            }.addOnFailureListener{
                Log.e("FALLO",it.message.toString())
            }
        gMap.setOnInfoWindowClickListener (this)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15F))
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED) {
            tienePermiso = true
        } else {
            requestPermissions( arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun obtenerUbicacion() {
        try {
            if (tienePermiso) {
                val ubicacionActual =
                    LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                ubicacionActual.addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        val ubicacion = it.result
                        if (ubicacion != null) {
                            val latLng = LatLng(ubicacion.latitude, ubicacion.longitude)
                            gMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(latLng, 12F)
                            )
                            gMap.addMarker(MarkerOptions().position(latLng).title(getString(R.string.ubicado_Aca)))
                        }
                    } else {
                        gMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(defaultLocation,
                            15F))
                        gMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }



    override fun onInfoWindowClick(p0: Marker) {
        val intent = Intent(requireContext(), DetalleLugarActivity::class.java)
        intent.putExtra("codigo",p0.tag.toString())
        startActivity(intent)
    }

}