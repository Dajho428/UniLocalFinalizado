package com.android.uniLocal.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.bd.Categorias
import com.android.uniLocal.databinding.ActivityListaModeradorBinding
import com.android.uniLocal.modelo.Lugar
import com.android.uniLocal.R
import com.android.uniLocal.bd.Comentarios
import com.android.uniLocal.modelo.Categoria
import com.android.uniLocal.modelo.Comentario
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LugarAdapter(private var lista:ArrayList<Lugar>): RecyclerView.Adapter<LugarAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ActivityListaModeradorBinding.inflate( LayoutInflater.from(parent.context), parent, false )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind( lista[position] )
    }

    override fun getItemCount() = lista.size

    inner class ViewHolder(private var view:ActivityListaModeradorBinding):RecyclerView.ViewHolder(view.root), View.OnClickListener{

        private var codigoLugar:String = ""

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(lugar: Lugar){
            view.nombreLugar.text = lugar.nombre
            view.direccionLugar.text = lugar.direccion

            val estado = lugar.estaAbierto()

            if(estado == "Abierto"){
                view.estadoLugar.setTextColor( ContextCompat.getColor(itemView.context, R.color.color_green_light) )
                view.horarioLugar.text = "Cierra a las ${lugar.obtenerHoraCierre()}:00"
            }else{
                view.estadoLugar.setTextColor( ContextCompat.getColor(itemView.context, R.color.red ) )
                view.horarioLugar.visibility = View.GONE
            }

            val comentarios: ArrayList<Comentario> = ArrayList()
            Firebase.firestore
                .collection("lugares")
                .document(lugar.key)
                .collection("comentarios")
                .get()
                .addOnSuccessListener { c ->
                    for (doc in c) {
                        comentarios.add(doc.toObject(Comentario::class.java))
                    }
                    val calificacion = obtenerCalificacionLugar(comentarios)
//                    Log.e("Calificacion",calificacion.toString())
                    for (i in 0..calificacion-1){
                        (view.calificacion[i] as TextView).setTextColor(ContextCompat.getColor(view.calificacion.context,R.color.yellow))
                    }
                }

            view.estadoLugar.text = lugar.estaAbierto()
            Glide.with(view.root.context)
                .load(lugar.imagenes[0])
                .into(view.imgLugar)
            Firebase.firestore
                .collection("categorias")
                .document(lugar.idCategoria)
                .get()
                .addOnSuccessListener {
                    view.iconoLugar.text = it.toObject(Categoria::class.java)!!.icono
                }
            codigoLugar = lugar.key
        }

        override fun onClick(p0: View?) {
            val intent = Intent( view.root.context, DetalleLugarActivity::class.java )
            intent.putExtra("codigo", codigoLugar)
            view.root.context.startActivity(intent)
        }

        fun obtenerCalificacionLugar(comentarios:ArrayList<Comentario>):Int{
            var promedio = 0
            if(comentarios.size >0) {
                val suma = comentarios.stream().map { c -> c.calificacion }
                    .reduce { suma, valor -> suma + valor }.get()
                promedio = suma / comentarios.size
            }
            return promedio
        }

    }
}