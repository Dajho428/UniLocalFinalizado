package com.android.uniLocal.adapter


import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.activities.ModeradorActivity
import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Lugar
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LugarRevisadoAdapter(private var lugares:ArrayList<Lugar>): RecyclerView.Adapter<LugarRevisadoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate( R.layout.lugar_revisado_item, parent, false )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LugarRevisadoAdapter.ViewHolder, position: Int) {
        holder.bind( lugares[position] )
    }

    override fun getItemCount() = lugares.size

    inner class ViewHolder(var itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{

        private var codigo:String = ""
        var nombre:TextView = itemView.findViewById(R.id.nombre_lugar)
        var boton:Button = itemView.findViewById(R.id.btn_accion)
        var estado:TextView = itemView.findViewById(R.id.revision)
        var imagen:ImageView = itemView.findViewById(R.id.img_lugar)


        init {
            itemView.setOnClickListener(this)
        }

        fun bind(lugar: Lugar){
            Glide.with(nombre.context)
                .load(lugar.imagenes[0])
                .into(imagen)
            codigo = lugar.key
            nombre.text = lugar.nombre
            estado.text = lugar.estado.toString()
            if (lugar.estado == EstadoLugar.RECHAZADO){
                estado.setTextColor(nombre.context.getColor(R.color.red))
                boton.text = "Aceptar"
                boton.setOnClickListener { cambiarEstadoAceptado(lugar, nombre.context) }
                boton.setBackgroundColor(nombre.context.getColor(R.color.color_green_light))
                boton.setTextColor(nombre.context.getColor(R.color.black))
            }else{
                estado.setTextColor(nombre.context.getColor(R.color.color_green_light))
                boton.setOnClickListener { cambiarEstadoRechazado(lugar, nombre.context) }
                boton.text = "Rechazar"
                boton.setBackgroundColor(nombre.context.getColor(R.color.red))
                boton.setTextColor(nombre.context.getColor(R.color.black))
            }
        }

        override fun onClick(p0: View?) {
            val intent = Intent( nombre.context, DetalleLugarActivity::class.java )
            intent.putExtra("codigo", codigo)
            nombre.context.startActivity(intent)
        }

    }

    fun cambiarEstadoAceptado(lugar:Lugar, context:Context){
        Firebase.firestore
            .collection("lugares")
            .document(lugar.key)
            .get()
            .addOnSuccessListener {
                val lugarNuevo = it.toObject(Lugar::class.java)
                lugarNuevo!!.key = it.id
                lugarNuevo!!.idModerador = lugar.idModerador
                lugarNuevo!!.estado = EstadoLugar.ACEPTADO
                Firebase.firestore
                    .collection("lugares")
                    .document(lugar.key)
                    .set(lugarNuevo)
                    .addOnSuccessListener {
                        Toast.makeText(context, context.getString(R.string.lugar_aceptado), Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, ModeradorActivity::class.java))
                    }
            }
    }

    fun cambiarEstadoRechazado(lugar:Lugar, context:Context){
        Firebase.firestore
            .collection("lugares")
            .document(lugar.key)
            .get()
            .addOnSuccessListener {
                val lugarNuevo = it.toObject(Lugar::class.java)
                lugarNuevo!!.key = it.id
                lugarNuevo!!.idModerador = lugar.idModerador
                lugarNuevo!!.estado = EstadoLugar.RECHAZADO
                Firebase.firestore
                    .collection("lugares")
                    .document(lugar.key)
                    .set(lugarNuevo)
                    .addOnSuccessListener {
                        Toast.makeText(context, context.getString(R.string.lugar_rechazado), Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, ModeradorActivity::class.java))
                    }
            }
    }
}