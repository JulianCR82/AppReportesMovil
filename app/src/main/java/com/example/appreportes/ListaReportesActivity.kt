package com.example.appreportes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaReportesActivity : AppCompatActivity() {

    //declaracion de las variables
    private lateinit var rvReportes: RecyclerView
    private lateinit var reporteAdapter: ReporteAdapter
    private lateinit var firebaseRef: DatabaseReference
    private val reportes = mutableListOf<Reporte>()
    private var databaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_reportes)

        //metodos
        inicializarVistas()
        configurarRecyclerView()
        inicializarFirebase()
        cargarReportesDesdeFirebase()
    }

    private fun inicializarVistas() {
        rvReportes = findViewById(R.id.recyclerViewReportes)
    }

    private fun configurarRecyclerView() {
        //se crea en linearlayout
        rvReportes.layoutManager = LinearLayoutManager(this)
        //Se crea el ReporteAdapter, pasándole la lista y un callback para borrar reportes.
        reporteAdapter = ReporteAdapter(reportes) { reporteSeleccionado ->
            borrarReporte(reporteSeleccionado)
        }
        //Se asigna el adaptador al RecyclerView
        rvReportes.adapter = reporteAdapter
    }

    private fun inicializarFirebase() {
        //Obtiene referencia al nodo dentro de Firebase
        firebaseRef = FirebaseDatabase.getInstance().getReference("reportes")
    }

    private fun cargarReportesDesdeFirebase() {
        databaseListener?.let { firebaseRef.removeEventListener(it) }

        val listener = object : ValueEventListener {
            // Se ejecuta cuando los datos se cargan o cambian en Firebase
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                procesarDatosRecibidos(dataSnapshot)
            }
            // Se ejecuta si hay un error al acceder a Firebase (permisos, conexión, etc.)
            override fun onCancelled(databaseError: DatabaseError) {
                mostrarMensajeError("Error al cargar datos: ${databaseError.message}")
            }
        }

        databaseListener = listener
        // Activamos el listener en Firebase - ahora escuchará cambios en tiempo real
        firebaseRef.addValueEventListener(listener)
    }

    // Procesa los datos que vienen de Firebase y los convierte en una lista de reportes
    private fun procesarDatosRecibidos(dataSnapshot: DataSnapshot) {
        // Creamos una lista temporal vacía para almacenar los reportes
        val nuevaListaReportes = mutableListOf<Reporte>()

        // Recorremos cada nodo hijo dentro del snapshot (cada reporte en Firebase)
        dataSnapshot.children.forEach { nodo ->
            val reporteObtenido = nodo.getValue(Reporte::class.java)
            reporteObtenido?.let {
                // Obtenemos o corregimos el ID del reporte
                val idFinal = obtenerIdValido(it.id, nodo.key)
                // Creamos una copia del reporte con el ID correcto y lo agregamos a la lista
                nuevaListaReportes.add(it.copy(id = idFinal))
            }
        }
        // Actualizamos la interfaz con la nueva lista de reportes
        actualizarInterfaz(nuevaListaReportes)
    }

    // Valida y retorna un ID válido para el reporte
    private fun obtenerIdValido(idReporte: String, claveNodo: String?): String {
        return if (idReporte.isBlank()) claveNodo ?: "" else idReporte
    }

    // Actualiza el RecyclerView con la nueva lista de reportes
    private fun actualizarInterfaz(nuevosReportes: List<Reporte>) {
        runOnUiThread {
            reporteAdapter.actualizarDatos(nuevosReportes)
        }
    }

    private fun borrarReporte(reporte: Reporte) {
        when {
            // Si el ID está vacío, mostramos error
            reporte.id.isBlank() -> {
                mostrarMensajeError("El reporte no tiene ID válido")
            }
            // Si el ID es válido, procedemos con la eliminación
            else -> {
                eliminarDeFirebase(reporte.id)
            }
        }
    }

    // Elimina el reporte de Firebase usando su ID
    private fun eliminarDeFirebase(idReporte: String) {
        // Accedemos al nodo hijo con el ID del reporte y lo eliminamos
        firebaseRef.child(idReporte).removeValue()
            .addOnSuccessListener {
                mostrarMensajeExito("Reporte eliminado")
            }
            .addOnFailureListener { excepcion ->
                mostrarMensajeError("Error al eliminar: ${excepcion.message}")
            }
    }

    private fun mostrarMensajeExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        limpiarListener()
    }

    private fun limpiarListener() {
        databaseListener?.let {
            firebaseRef.removeEventListener(it)
        }
        databaseListener = null
    }
}