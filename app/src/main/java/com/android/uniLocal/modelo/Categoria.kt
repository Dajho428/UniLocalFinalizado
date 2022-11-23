package com.android.uniLocal.modelo

class Categoria() {

    var key:String = ""
    var nombre: String = ""
    var icono: String = ""

    constructor(nombre:String,icono:String):this(){
        this.nombre = nombre
        this.icono = icono
    }

    override fun toString(): String {
        return nombre
    }

}