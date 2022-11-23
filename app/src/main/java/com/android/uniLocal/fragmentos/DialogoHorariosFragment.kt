package com.android.uniLocal.fragmentos

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.android.uniLocal.bd.Horarios
import com.android.uniLocal.databinding.FragmentDialogoHorariosBinding
import com.android.uniLocal.modelo.DiaSemana
import com.android.uniLocal.modelo.Horario


class DialogoHorariosFragment : DialogFragment() {

    lateinit var binding: FragmentDialogoHorariosBinding
    lateinit var listener: DialogoHorariosFragment.onHorarioCreadoListener
    var diaSeleccionado: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogoHorariosBinding.inflate(inflater,container, false)
        cargarDiasSemana()
        binding.agregarHorario.setOnClickListener { agregarHorario()}
        return binding.root
    }


    fun agregarHorario(){
        val dia = diaSeleccionado
        val horaInicio = binding.horaInicio.text.toString()
        val horaCierre = binding.horaFin.text.toString()

        if(dia!= -1 && horaInicio.isNotEmpty() && horaCierre.isNotEmpty() ){
            val horarios: ArrayList<DiaSemana> = ArrayList()
            horarios.add(DiaSemana.values()[dia])
            val horario = Horarios.agregarHorario(Horario(horarios, horaInicio.toInt(),horaCierre.toInt()))
            listener.elegirHorario(horario)
            dismiss()
        }
    }

    fun cargarDiasSemana(){
        var list = DiaSemana.values()
        var adapter= ArrayAdapter(requireContext(), R.layout.simple_spinner_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.diaSemana.adapter= adapter
        binding.diaSemana.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                diaSeleccionado = p2
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    interface onHorarioCreadoListener{
        fun elegirHorario(horario: Horario)
    }

}