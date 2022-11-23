package com.android.uniLocal.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.uniLocal.R
import com.android.uniLocal.bd.Categorias
import com.android.uniLocal.bd.Ciudades
import com.android.uniLocal.bd.Lugares
import com.android.uniLocal.databinding.ActivityCrearLugarBinding
import com.android.uniLocal.fragmentos.DialogoHorariosFragment
import com.android.uniLocal.modelo.Categoria
import com.android.uniLocal.modelo.Ciudad
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.bumptech.glide.Glide
import com.android.uniLocal.modelo.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CrearLugarActivity : AppCompatActivity(), DialogoHorariosFragment.onHorarioCreadoListener,OnMapReadyCallback {

    lateinit var binding: ActivityCrearLugarBinding
    lateinit var gMap:GoogleMap
    var longitud:Double ? = null
    var latitud: Double?= null
    var posCiudad:Int = -1
    lateinit var horarios: ArrayList<Horario>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var codigoArchivo: Int = 0
    var posCategoria:Int = -1
    var imagenesLugar:ArrayList<String> = ArrayList()
    var usuario:FirebaseUser? = null
    private var tienePermisoCamara = false
    private val defaultLocation = LatLng(4.548099, -75.661451)
    lateinit var ciudades:ArrayList<Ciudad>
    lateinit var categorias:ArrayList<Categoria>
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearLugarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        horarios = ArrayList()
        ciudades = ArrayList()
        categorias = ArrayList()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialogo_carga)
        dialog = builder.create()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ) {
            onActivityResult(it.resultCode, it)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa_crear_lugar) as SupportMapFragment
        mapFragment.getMapAsync(this)

        usuario = FirebaseAuth.getInstance().currentUser
        if(usuario!= null){
            cargarCiudades()
            cargarCategorias()
            binding.btnTomarFoto.setOnClickListener {tomarFoto() }
            binding.btnSelArchivo.setOnClickListener { seleccionarFoto() }
            binding.btnAsignarHorario.setOnClickListener { mostrarDialogo()}
            binding.btnCrearLugar.setOnClickListener { crearNuevoLugar() }
        }

    }

    fun mostrarDialogo(){
        val dialog = DialogoHorariosFragment()
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogoTitulo)
        dialog.listener = this
        dialog.show(supportFragmentManager, "Agregar")
    }

    fun cargarCiudades(){
        Firebase.firestore
            .collection("ciudades")
            .get()
            .addOnSuccessListener {
                for(doc in it ){
                    val ciudad = doc.toObject(Ciudad::class.java)
                    ciudad.key = doc.id
                    ciudades.add(ciudad)
                }
                Log.e("ciudades", ciudades.toString())
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudades)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.ciudadLugar.adapter = adapter

                binding.ciudadLugar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        posCiudad = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) { }
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,15f))

        gMap.setOnMapClickListener {
            if(longitud == null && latitud == null){
                longitud = 0.0
                latitud = 0.0
            }
            longitud = it.longitude
            latitud = it.latitude

            gMap.clear()
            gMap.addMarker(MarkerOptions().position(it).title(getString(R.string.lugar_aca)))}
    }

    fun cargarCategorias(){
        Firebase.firestore
            .collection("categorias")
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    var categoria = doc.toObject(Categoria::class.java)
                    categoria.key = doc.id
                    categorias.add(categoria)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categoriaLugar.adapter = adapter

                binding.categoriaLugar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        posCategoria = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) { }
                }
            }
    }

    fun crearNuevoLugar(){

        val nombre = binding.nombreLugar.text.toString()
        val descripcion = binding.descripcionLugar.text.toString()
        val telefono = binding.telefonoLugar.text.toString()
        val direccion = binding.direccionLugar.text.toString()
        val idCiudad = ciudades[posCiudad].key
        val idCategoria = categorias[posCategoria].key

        setDialog(false)

        if( nombre.isEmpty() ){
            binding.nombreLayout.error = getString(R.string.es_obligatorio)
        }else{
            binding.nombreLayout.error = null
        }

        if( descripcion.isEmpty() ){
            binding.descripcionLayout.error = getString(R.string.es_obligatorio)
        }else{
            binding.descripcionLayout.error = null
        }

        if( direccion.isEmpty() ){
            binding.direccionLayout.error = getString(R.string.es_obligatorio)
        }else{
            binding.direccionLayout.error = null
        }

        if( telefono.isEmpty() ){
            binding.telefonoLayout.error = getString(R.string.es_obligatorio)
        }else{
            binding.telefonoLayout.error = null
        }

        if(nombre.isNotEmpty() && descripcion.isNotEmpty() && telefono.isNotEmpty() && horarios.isNotEmpty() && direccion.isNotEmpty() && idCiudad != "" && idCategoria != "" && longitud != null && latitud != null && imagenesLugar.isNotEmpty())   {

            val nuevoLugar = Lugar(nombre, descripcion, usuario!!.uid, EstadoLugar.SIN_REVISAR, idCategoria, direccion, idCiudad)

            nuevoLugar.longitud = longitud!!.toFloat()
            nuevoLugar.latitud = latitud!!.toFloat()
            nuevoLugar.imagenes = imagenesLugar

            val telefonos: ArrayList<String> = ArrayList()
            telefonos.add(telefono)

            nuevoLugar.telefonos = telefonos
            nuevoLugar!!.horarios = horarios

            if(usuario!=null){
                Firebase.firestore
                    .collection("lugares")
                    .add(nuevoLugar)
                    .addOnSuccessListener {
                        nuevoLugar.key = it.id
                        Toast.makeText(this, getString(R.string.lugar_creado), Toast.LENGTH_LONG)
                            .show()
                        setDialog(false)
                        binding.nombreLugar.text = null
                        binding.descripcionLugar.text = null
                        binding.telefonoLugar.text = null
                        binding.direccionLugar.text = null
                        startActivity(Intent(this, UserActivity::class.java))
                    }.addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG)
                            .show()
                        setDialog(false)
                    }
            }
        }else{
            Toast.makeText(this, getString(R.string.ingresar_datos), Toast.LENGTH_LONG)
                .show()
            setDialog(false)
        }

    }

    fun tomarFoto(){
        getCameraPermission()
        if(tienePermisoCamara){
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    resultLauncher.launch(takePictureIntent)
                    codigoArchivo = 1
                }
            }
        }

    }

    fun seleccionarFoto(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        codigoArchivo = 2
        resultLauncher.launch(i)
    }

    private fun onActivityResult(resultCode:Int, result: ActivityResult){
        if( resultCode == Activity.RESULT_OK ){
            setDialog(true)
            val fecha = Date()
            val datatime = "/p-${fecha.time}.jpg"
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child(datatime)
            if( codigoArchivo == 1 ){
                val data = result.data?.extras
                if( data?.get("data") is Bitmap){
                    val imageBitmap = data?.get("data") as Bitmap
                    val baos = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    storageRef.putBytes(data).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            dibujarImagen(it)
                        }
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
            }else if( codigoArchivo == 2 ){
                val data = result.data
                if(data!=null){
                    val selectedImageUri: Uri? = data.data
                    if(selectedImageUri!=null){
                        storageRef.putFile(selectedImageUri).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener {
                                dibujarImagen(it)
                            }
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun dibujarImagen(url: Uri){
        setDialog(false)
        imagenesLugar.add(url.toString())

        var imagen = ImageView(baseContext)
        imagen.layoutParams = LinearLayout.LayoutParams(300, 310)
        binding.imagenesSeleccionadas.addView(imagen)

        Glide.with( baseContext )
            .load(url.toString())
            .into(imagen)
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

    override fun elegirHorario(horario: Horario) {
        horarios.add(horario)
    }

    private fun getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA
            ) ==
            PackageManager.PERMISSION_GRANTED) {
            tienePermisoCamara = true
        } else {
            requestPermissions( arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

}