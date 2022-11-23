package com.android.uniLocal.bd

import com.android.uniLocal.modelo.Categoria

object Categorias {

    private val categorias: ArrayList<Categoria> = ArrayList()

    init {

        categorias.add(Categoria( "Restaurante", "\uf2e7"))
        categorias.add(Categoria( "Hotel", "\uF594"))
        categorias.add(Categoria( "Almacen", "\uf54f"))
        categorias.add(Categoria( "Café", "\uF0F4"))
        categorias.add(Categoria( "Bar","\uf864"))
    }

    fun listar():ArrayList<Categoria>{
        return categorias
    }

}