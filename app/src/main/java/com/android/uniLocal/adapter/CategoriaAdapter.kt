package com.android.uniLocal.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.uniLocal.R
import com.android.uniLocal.activities.CategoriasFiltroActivity
import com.android.uniLocal.activities.DetalleLugarActivity
import com.android.uniLocal.modelo.Categoria
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class CategoriaAdapter (var categorias:ArrayList<Categoria>, var context: Context): RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.categoria_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategoriaAdapter.ViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount() = categorias.size

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val iconoCategoria: TextView = itemView.findViewById(R.id.icono_categoria)
        var codigoCategoria:String = ""

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(categoria: Categoria) {
            codigoCategoria = categoria.key
            iconoCategoria.text = categoria.icono
        }

        override fun onClick(p0: View?) {
            val intent = Intent( iconoCategoria.context, CategoriasFiltroActivity::class.java )
            intent.putExtra("codigo", codigoCategoria)
            (context as CategoriasFiltroActivity).finish()
            iconoCategoria.context.startActivity(intent)
        }
    }
}