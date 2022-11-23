package com.android.uniLocal.modelo

class Usuario() {
    var key: String = ""
    var rol: Rol = Rol.CLIENTE
    var nombre: String = ""
    var imagen: String = ""
    var nickname:String = ""
    var correo: String  = ""
    var estado: EstadoUsuario = EstadoUsuario.ACEPTADO

    constructor(nombre:String, nickname:String, correo:String, rol: Rol):this(){
        this.nombre = nombre
        this.nickname = nickname
        this.correo = correo
        this.rol = rol
    }

    override fun toString(): String {
        return "Usuario(nickname='$nickname') ${super.toString()}"
    }
}