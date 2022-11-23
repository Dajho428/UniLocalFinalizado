package com.android.uniLocal.modelo

class Ciudad() {

    var key:String = ""
    var nombre: String = ""

    constructor(nombre:String):this(){
        this.nombre = nombre
    }

    override fun toString(): String {
        return nombre
    }


}