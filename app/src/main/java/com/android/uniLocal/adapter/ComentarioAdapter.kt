package com.android.uniLocal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.modelo.Comentario
import com.android.uniLocal.modelo.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ComentarioAdapter (var comentariosUsuario:ArrayList<Comentario>): RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.comentarios_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ComentarioAdapter.ViewHolder, position: Int) {
        holder.bind(comentariosUsuario[position])
    }

    override fun getItemCount() = comentariosUsuario.size

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val nickname: TextView = itemView.findViewById(R.id.usuario_nickname)
        val fecha: TextView = itemView.findViewById(R.id.fecha_comentario)
        val contenido: TextView = itemView.findViewById(R.id.contenido_comentario)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(comentario: Comentario) {
            Firebase.firestore
                .collection("usuarios")
                .document(comentario.idUsuario)
                .get()
                .addOnSuccessListener { u ->
                    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val usuario = u.toObject(Usuario::class.java)
                    nickname.text = usuario!!.nickname
                    fecha.text = simpleDateFormat.format(comentario.fecha.time)
                    contenido.text = comentario.texto
                }
        }

        override fun onClick(p0: View?) {

        }
    }
}