package com.android.uniLocal.bd

import com.android.uniLocal.modelo.Ciudad

object Ciudades {
    private val lista: ArrayList<Ciudad> = ArrayList()

    init {
        lista.add(Ciudad( "Armenia"))
        lista.add(Ciudad( "Periera"))
        lista.add(Ciudad( "Bogota"))
        lista.add(Ciudad( "Calarca"))
        lista.add(Ciudad( "Manizales"))
    }

    fun listar(): ArrayList<Ciudad>{
        return lista;
    }


}