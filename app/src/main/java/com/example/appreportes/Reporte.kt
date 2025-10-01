package com.example.appreportes

data class Reporte(
    val id: String = "",
    val tipo: String? = null,
    val descripcion: String? = null,
    val ubicacion: String? = null,
    val imagenUrl: String? = null,
    val fechaHora: String? = null,
    val usuario: String = "anónimo"

)



