package com.example.appreportes

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class ReporteAdapter(

    private val listaReportes: MutableList<Reporte>, //lista de reportes que se van a mostrar
    private val alEliminar: (Reporte) -> Unit  // Función callback que se ejecuta al eliminar un reporte
) : RecyclerView.Adapter<ReporteAdapter.ViewHolderReporte>() {

    inner class ViewHolderReporte(val vistaCompose: ComposeView) : RecyclerView.ViewHolder(vistaCompose)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReporte {
        val contexto = parent.context
        val nuevaVistaCompose = ComposeView(contexto)
        return ViewHolderReporte(nuevaVistaCompose)
    }

    override fun onBindViewHolder(holder: ViewHolderReporte, position: Int) {
        val reporteActual = obtenerReporteEnPosicion(position)
        configurarVistaReporte(holder, reporteActual)
    }

    private fun obtenerReporteEnPosicion(posicion: Int): Reporte {
        return listaReportes[posicion]
    }

    private fun configurarVistaReporte(holder: ViewHolderReporte, reporte: Reporte) {
        holder.vistaCompose.setContent {
            //se llama al composable denuncua
            Denuncia(
                reporte = reporte,
                onEliminarClick = { ejecutarEliminacion(reporte) }
            )
        }
    }

    private fun ejecutarEliminacion(reporte: Reporte) {
        alEliminar(reporte)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount(): Int = listaReportes.size

    fun actualizarDatos(nuevaLista: List<Reporte>) {
        limpiarYAgregarNuevosDatos(nuevaLista)
        notificarCambios()
    }

    // Limpia la lista actual y agrega todos los elementos de la nueva lista.
    private fun limpiarYAgregarNuevosDatos(nuevaLista: List<Reporte>) {
        listaReportes.clear()
        listaReportes.addAll(nuevaLista)
    }

    // Notifica al RecyclerView que la lista completa cambió y debe redibujarse.
    private fun notificarCambios() {
        notifyDataSetChanged()
    }
}