package com.android.uniLocal.fragmentos

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.adapter.LugarAdapter
import com.android.uniLocal.databinding.FragmentListaModeradoresAdministracionBinding
import com.android.uniLocal.databinding.FragmentNuevosLugaresBinding
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class NuevosLugaresFragment : Fragment() {
   lateinit var binding: FragmentNuevosLugaresBinding
    lateinit var listaLugares: ArrayList<Lugar>
    lateinit var adapterLista: LugarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listaLugares = ArrayList()
        binding = FragmentNuevosLugaresBinding.inflate(inflater, container, false)
        adapterLista = LugarAdapter(listaLugares)
        binding.listaLugares.adapter = adapterLista
        binding.listaLugares.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        crearEventoSwipe()
        return binding.root
    }

    fun crearEventoSwipe(){
        val simpleCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var pos = viewHolder.adapterPosition
                val lugar = listaLugares[pos]

                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        aceptarLugar(lugar!!.key, pos)
                        listaLugares.remove(lugar)
                        adapterLista.notifyItemRemoved(pos)

                    }
                    ItemTouchHelper.RIGHT -> {

                        denegarLugar(lugar!!.key, pos)
                        listaLugares.remove(lugar)
                        adapterLista.notifyItemRemoved(pos)

                    }

                }

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_green_light
                        )
                    )
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    .addSwipeLeftLabel("Aceptar")
                    .addSwipeRightLabel("Rechazar")
                    .create()
                    .decorate()
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.listaLugares)
    }

    fun deshacerAccion(key:String,pos:Int){
        Firebase.firestore
            .collection("lugares")
            .document(key)
            .get()
            .addOnSuccessListener {
                val lugarNuevo = it.toObject(Lugar::class.java)
                lugarNuevo!!.key = it.id
                lugarNuevo!!.idModerador = ""
                lugarNuevo!!.estado = EstadoLugar.SIN_REVISAR
                Firebase.firestore
                    .collection("lugares")
                    .document(key)
                    .set(lugarNuevo)
                    .addOnSuccessListener {
                        Snackbar.make(binding.listaLugares, getString(R.string.lugar_no_revisado), Snackbar.LENGTH_LONG)
                        listaLugares.add(pos, lugarNuevo!!)
                        adapterLista.notifyItemInserted(pos)

                    }
            }
    }

    fun aceptarLugar(key:String,pos:Int){
        val usuario = FirebaseAuth.getInstance().currentUser
        Firebase.firestore
            .collection("lugares")
            .document(key)
            .get()
            .addOnSuccessListener {
                val lugarNuevo = it.toObject(Lugar::class.java)
                lugarNuevo!!.key = it.id
                lugarNuevo!!.idModerador = usuario!!.uid
                lugarNuevo!!.estado = EstadoLugar.ACEPTADO
                Firebase.firestore
                    .collection("lugares")
                    .document(key)
                    .set(lugarNuevo)
                    .addOnSuccessListener {
                        Snackbar.make(binding.listaLugares, getString(R.string.lugar_aceptado), Snackbar.LENGTH_LONG)
                            .setAction("Deshacer", View.OnClickListener {
                                deshacerAccion(key, pos)
                            }).show()
                    }
            }
    }

    fun denegarLugar(key:String,pos:Int){
        val usuario = FirebaseAuth.getInstance().currentUser
        Firebase.firestore
            .collection("lugares")
            .document(key)
            .get()
            .addOnSuccessListener {
                val lugarNuevo = it.toObject(Lugar::class.java)
                lugarNuevo!!.key = it.id
                lugarNuevo!!.idModerador = usuario!!.uid
                lugarNuevo!!.estado = EstadoLugar.RECHAZADO
                Firebase.firestore
                    .collection("lugares")
                    .document(key)
                    .set(lugarNuevo)
                    .addOnSuccessListener {
                        Snackbar.make(
                            binding.listaLugares,
                            getString(R.string.lugar_rechazado),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(getString(R.string.deshacer), View.OnClickListener {
                                deshacerAccion(key, pos)
                            }).show()
                    }
            }
    }

    override fun onResume() {
        super.onResume()
        listaLugares.clear()
        Firebase.firestore
            .collection("lugares")
            .whereEqualTo("estado", EstadoLugar.SIN_REVISAR)
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    val lugar = doc.toObject(Lugar::class.java)
                    lugar.key = doc.id
                    listaLugares.add(lugar)
                }
                adapterLista.notifyDataSetChanged()
            }
    }

}