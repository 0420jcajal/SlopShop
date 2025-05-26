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
import com.example.slopshop.Usuario.Utils.UbicacionesData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FragmentAgregarDireccion : Fragment() {


    private lateinit var spinnerPais: Spinner
    private lateinit var spinnerProvincia: Spinner
    private lateinit var spinnerCiudad: Spinner
    private lateinit var etCalle: EditText
    private lateinit var etPiso: EditText
    private lateinit var btnAgregar: Button


    private lateinit var dataUbicaciones: UbicacionesData

    private lateinit var paises: List<String>
    private lateinit var provincias: Map<String, List<String>>
    private lateinit var ciudades: Map<String, List<String>>

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference()

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

        dataUbicaciones = UbicacionesData

        paises = dataUbicaciones.paises
        provincias = dataUbicaciones.provincias
        ciudades = dataUbicaciones.ciudades

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
            val uid = auth.currentUser?.uid

            if (pais == "Seleccione país" || provincia == "Seleccione provincia" || ciudad == "Seleccione ciudad") {
                Toast.makeText(requireContext(), "Por favor seleccione todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar dirección")
                .setMessage("¿Agregar esta dirección?\n\n$calle, $piso\n$ciudad, $provincia, $pais")
                .setPositiveButton("Sí") { _, _ ->
                    val direccionRef = db.child("Direcciones").push()
                    val direccionId = direccionRef.key ?: System.currentTimeMillis().toString()

                    val direccionData = mapOf(
                        "id" to direccionId,
                        "uidUsuario" to uid,
                        "pais" to pais,
                        "provincia" to provincia,
                        "ciudad" to ciudad,
                        "calle" to calle,
                        "piso" to piso,
                        "timestamp" to System.currentTimeMillis()
                    )

                    direccionRef.setValue(direccionData)
                        .addOnSuccessListener {

                            Toast.makeText(requireContext(), "Dirección guardada correctamente", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.navFragment, FragmentCarritoU())
                                .addToBackStack(null)
                                .commit()
                            //TODO


                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al guardar dirección: ${e.message}", Toast.LENGTH_LONG).show()
                        }
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