package com.example.slopshop.Usuario.Carrito

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.slopshop.R


class FragmentAgregarDireccion : Fragment() {


    private lateinit var spinnerPais: Spinner
    private lateinit var spinnerProvincia: Spinner
    private lateinit var spinnerCiudad: Spinner
    private lateinit var etCalle: EditText
    private lateinit var etPiso: EditText
    private lateinit var btnAgregar: Button


    private val paises = listOf("Seleccione país", "España", "México")
    private val provincias = mapOf(
        "España" to listOf("Seleccione provincia", "Madrid", "Barcelona"),
        "México" to listOf("Seleccione provincia", "CDMX", "Guadalajara")
    )
    private val ciudades = mapOf(
        "Madrid" to listOf("Seleccione ciudad", "Madrid Centro", "Alcobendas"),
        "Barcelona" to listOf("Seleccione ciudad", "Badalona", "Hospitalet"),
        "CDMX" to listOf("Seleccione ciudad", "Benito Juárez", "Coyoacán"),
        "Guadalajara" to listOf("Seleccione ciudad", "Zapopan", "Tlaquepaque")
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_direccion, container, false)

        spinnerPais = view.findViewById(R.id.spinnerPais)
        spinnerProvincia = view.findViewById(R.id.spinnerProvincia)
        spinnerCiudad = view.findViewById(R.id.spinnerCiudad)
        etCalle = view.findViewById(R.id.etCalle)
        etPiso = view.findViewById(R.id.etPiso)
        btnAgregar = view.findViewById(R.id.btnAgregarDireccion)

        cargarPaises()

        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val paisSeleccionado = paises[position]
                if (provincias.containsKey(paisSeleccionado)) {
                    cargarProvincias(provincias[paisSeleccionado]!!)
                } else {
                    cargarProvincias(listOf("Seleccione provincia"))
                }
                cargarCiudades(listOf("Seleccione ciudad")) // limpiar ciudades
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerProvincia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val provinciaSeleccionada = spinnerProvincia.selectedItem.toString()
                if (ciudades.containsKey(provinciaSeleccionada)) {
                    cargarCiudades(ciudades[provinciaSeleccionada]!!)
                } else {
                    cargarCiudades(listOf("Seleccione ciudad"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnAgregar.setOnClickListener {
            val pais = spinnerPais.selectedItem.toString()
            val provincia = spinnerProvincia.selectedItem.toString()
            val ciudad = spinnerCiudad.selectedItem.toString()
            val calle = etCalle.text.toString().trim()
            val piso = etPiso.text.toString().trim()

            if (pais == "Seleccione país" || provincia == "Seleccione provincia" || ciudad == "Seleccione ciudad") {
                Toast.makeText(requireContext(), "Por favor seleccione todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar dirección")
                .setMessage("¿Agregar esta dirección?\n\n$calle, $piso\n$ciudad, $provincia, $pais")
                .setPositiveButton("Sí") { _, _ ->
                    Toast.makeText(requireContext(), "Se agregó correctamente la dirección", Toast.LENGTH_SHORT).show()

                    //TODO NAVEGAR VUELTA
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }

    private fun cargarPaises() {
        spinnerPais.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paises).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun cargarProvincias(lista: List<String>) {
        spinnerProvincia.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lista).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun cargarCiudades(lista: List<String>) {
        spinnerCiudad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lista).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}