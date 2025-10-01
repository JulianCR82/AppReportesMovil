package com.example.appreportes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun Denuncia(
    reporte: Reporte, //recibe un objeto con los datos de la denuncia, tipo, usuario, descripcion etc
    onEliminarClick: () -> Unit, //callback para manejar el evento cuando se presiona eliminar
    modifier: Modifier = Modifier //permite personalizar el diseño
) {
    // Paleta de colores morados
    val primaryPurple = Color(0xFF6A1B9A)
    val darkPurple = Color(0xFF4A148C)
    val lightPurple = Color(0xFF9C27B0)
    val accentPurple = Color(0xFFAB47BC)

    Column( //contenedor principal, fondo blanco, borde gris y esquinas redondeadas
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = primaryPurple,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = reporte.tipo?.uppercase() ?: "SIN TIPO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = if (reporte.usuario != "anónimo") reporte.usuario else "Anónimo",
                fontSize = 13.sp,
                color = Color(0xFF666666),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider y separación
        //Añade una línea gris clara que separa secciones.
        // Con Spacer se le da mejora visual entre bloques.
        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFF0F0F0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        reporte.descripcion?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Descripción:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkPurple,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Ubicación
        reporte.ubicacion?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Ubicación:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkPurple,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Fecha
        reporte.fechaHora?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Fecha:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkPurple,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Imagen si existe
        if (!reporte.imagenUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = reporte.imagenUrl,
                contentDescription = "Evidencia",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Divider antes del botón
        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFF0F0F0)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón eliminar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            androidx.compose.material3.TextButton(
                onClick = onEliminarClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Eliminar",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}