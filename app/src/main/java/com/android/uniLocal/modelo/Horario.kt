package com.android.uniLocal.modelo

class Horario() {
    var id: Int = 0

    constructor( diaSemana:ArrayList<DiaSemana>,  horaInicio: Int, horaCierre: Int):this(){
        this.diaSemana = diaSemana
        this.horaInicio = horaInicio
        this.horaCierre = horaCierre
    }

    var diaSemana: ArrayList<DiaSemana> = ArrayList()
    var horaInicio: Int = 0
    var horaCierre:Int = 0
    override fun toString(): String {
        return "Horario(diaSemana=$diaSemana, horaInicio=$horaInicio, horaCierre=$horaCierre)"
    }


}