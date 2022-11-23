package com.android.uniLocal.bd

import com.android.uniLocal.modelo.EstadoLugar
import com.android.uniLocal.modelo.Horario
import com.android.uniLocal.modelo.Lugar
import kotlin.collections.ArrayList

object Lugares {

    private val lista:ArrayList<Lugar> = ArrayList()

    init {

//        val horario1 = Horario(1, Horarios.obtenerTodos(), 12, 20)
//        val horario2 = Horario(2, Horarios.obtenerEntreSemana(), 9, 12)
//        val horario3 = Horario(3, Horarios.obtenerFinSemana(), 14, 23)
//
//
//        val lugar1 = Lugar("Café ABC", "Excelente café para compartir", "", EstadoLugar.ACEPTADO, "", "Calle 123", "")
//        lugar1.latitud = 73.3434f
//        lugar1.longitud = -40.4345f
//        lugar1.horarios.add(horario2)
//
//        val lugar2 = Lugar("Bar City Pub", "Bar en la ciudad de Armenia", "", EstadoLugar.ACEPTADO, "", "Calle 12 # 23-1", "")
//        lugar2.latitud = 4.551234f
//        lugar2.longitud = -75.655657f
//        lugar2.horarios.add(horario1)
//
//        val lugar3 = Lugar("Restaurante Mi Cuate", "Comida Mexicana para todos", "", EstadoLugar.ACEPTADO, "", "Calle 452", "")
//        lugar3.latitud = 4.550549f
//        lugar3.longitud =  -75.653554f
//        lugar3.horarios.add(horario1)
//
//        val lugar4 = Lugar("BBC Norte Pub", "Cervezas BBC muy buenas", "", EstadoLugar.ACEPTADO, "", "Calle 53", "")
//        lugar4.latitud = 4.547533f
//        lugar4.longitud =  -75.655657f
//        lugar4.horarios.add(horario3)
//
//        val lugar5 = Lugar("Hotel barato", "Hotel para ahorrar mucho dinero", "", EstadoLugar.ACEPTADO, "", "Calle 23 # 34-1", "")
//        lugar5.latitud = 4.550806f
//        lugar5.longitud = -75.658961f
//        lugar5.horarios.add( horario1 )
//
//        val lugar6 = Lugar("Hostal Hippie", "Alojamiento para todos y todas", "", EstadoLugar.SIN_REVISAR, "", "Carrera 123", "")
//        lugar6.latitud = 4.555234f
//        lugar6.longitud =  -75.657438f
//        lugar6.horarios.add( horario2 )
//
//        lista.add( lugar1 )
//        lista.add( lugar2 )
//        lista.add( lugar3 )
//        lista.add( lugar4 )
//        lista.add( lugar5 )
//        lista.add( lugar6 )

    }

    fun listarPorEstado(estado:EstadoLugar):ArrayList<Lugar>{
        return lista.filter { l -> l.estado == estado }.toCollection(ArrayList())
    }

    fun obtener(id:String?): Lugar?{
        return lista.firstOrNull { l -> l.key == id }
    }

    fun buscarNombre(nombre:String): ArrayList<Lugar> {
        return lista.filter { l -> l.nombre.lowercase().contains(nombre.lowercase()) && l.estado == EstadoLugar.ACEPTADO }.toCollection(ArrayList())
    }


}