package com.android.uniLocal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.activities.DetallesModActivity
import com.android.uniLocal.databinding.ActivityListaModeradorBinding
import com.android.uniLocal.modelo.Lugar
import com.android.uniLocal.modelo.Usuario
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class ModeradorAdapter(private var lista:ArrayList<Usuario> ): RecyclerView.Adapter<ModeradorAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate( R.layout.moderador_item, parent, false )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ModeradorAdapter.ViewHolder, position: Int) {
        holder.bind( lista[position] )
    }

    override fun getItemCount() = lista.size

    inner class ViewHolder(var itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{

        private var codigo:String = ""
        var nombre:TextView = itemView.findViewById(R.id.nombre_moderador)
        var imagen:CircleImageView = itemView.findViewById(R.id.imagen_moderador)
        var correo:TextView = itemView.findViewById(R.id.correo_moderador)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(moderador:Usuario){
           if(moderador.imagen != ""){
               Glide.with(nombre.context)
                   .load(moderador.imagen)
                   .into(imagen)
           }
           codigo = moderador.key
           nombre.text = moderador.nombre
           correo.text = moderador.correo
        }

        override fun onClick(p0: View?) {
            val intent = Intent(nombre.context, DetallesModActivity::class.java)
            intent.putExtra("code", codigo)
            nombre.context.startActivity(intent)
        }

    }
}