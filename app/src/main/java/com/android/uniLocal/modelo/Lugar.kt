package com.android.uniLocal.modelo

import java.util.*
import kotlin.collections.ArrayList

class Lugar() {
    var key: String = ""
    var nombre:String = ""
    var descripcion:String = ""
    var idCreador:String = ""
    var estado:EstadoLugar = EstadoLugar.SIN_REVISAR
    var idCategoria:String = ""
    var idModerador:String = ""
    var direccion:String = ""
    var latitud:Float = 0F
    var longitud:Float = 0F
    var idCiudad:String = ""

    constructor(nombre:String, descripcion:String, idCreador:String, estadoLugar: EstadoLugar, idCategoria: String, direccion:String,idCiudad: String ):this(){
        this.nombre = nombre
        this.descripcion = descripcion
        this.idCreador = idCreador
        this.estado = estadoLugar
        this.idCategoria = idCategoria
        this.direccion = direccion
        this.idCiudad = idCiudad
    }

    var imagenes:ArrayList<String> = ArrayList()
    var telefonos:ArrayList<String> = ArrayList()
    var fecha: Date = Date()
    var horarios:ArrayList<Horario> = ArrayList()

    fun estaAbierto():String{

        val fecha = Calendar.getInstance()
        val dia = fecha.get(Calendar.DAY_OF_WEEK)
        val hora = fecha.get(Calendar.HOUR_OF_DAY)
        //val minuto = fecha.get(Calendar.MINUTE)

        var mensaje = "Cerrado"

        for(horario in horarios){
            if( horario.diaSemana.contains( DiaSemana.values()[dia-1] ) && hora < horario.horaCierre && hora > horario.horaInicio  ){
                mensaje = "Abierto"
                break
            }
        }

        return mensaje
    }

    fun obtenerHoraCierre():String{

        val fecha = Calendar.getInstance()
        val dia = fecha.get(Calendar.DAY_OF_WEEK)

        var mensaje = ""

        for(horario in horarios){
            if( horario.diaSemana.contains( DiaSemana.values()[dia-1] ) ){
                mensaje = horario.horaCierre.toString()
                break
            }
        }

        return mensaje
    }

    fun obtenerCalificacionPromedio(comentarios:ArrayList<Comentario>):Int{
        var promedio = 0

        if(comentarios.size > 0) {
            val suma = comentarios.stream().map { c -> c.calificacion }
                .reduce { suma, valor -> suma + valor }.get()

            promedio = suma/comentarios.size
        }

        return promedio
    }

    override fun toString(): String {
        return "Lugar( nombre='$nombre', descripcion='$descripcion', idCreador=$idCreador, estado=$estado, idCategoria=$idCategoria, latitud=$latitud, longitud=$longitud, idCiudad=$idCiudad, imagenes=$imagenes, telefonos=$telefonos, fecha=$fecha, horarios=$horarios)"
    }
}