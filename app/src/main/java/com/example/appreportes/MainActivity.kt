package com.example.appreportes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.databinding.DataBindingUtil
import com.example.appreportes.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {

    //variable para manejar las preferencias del usuario
    private lateinit var preferencesManager: DataStore

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inicializa Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        // Inicializa las preferencias ANTES de usar Compose
        preferencesManager = DataStore(this)

        //Carga la UI Compose dentro del ComposeView del XML
        binding.composeView.setContent {
            MaterialTheme {
                MainScreen(
                    userPrefs = preferencesManager,
                    onNavigateToReportes = { navegarAListaReportes() }
                )
            }
        }

    }

    // Navega a la actividad que muestra la lista de reportes
    private fun navegarAListaReportes() {
        val intent = Intent(this, ListaReportesActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userPrefs: DataStore, //gestor de preferencias
    onNavigateToReportes: () -> Unit //callback para navegar a la lisa de reportes
) {
    val context = LocalContext.current //obtenemos el contexto actual de la app
    val scope = rememberCoroutineScope() //creamos un scope de corrutinas para operaciones asincronas
    val scrollState = rememberScrollState() //estado para controlar el scroll de la pantalla

    // Estados para los campos del formulario
    var selectedTipo by remember { mutableStateOf("Seleccione tipo") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }

    // Lista de opciones para el tipo de maltrato
    val tiposMaltrato = listOf(
        "Seleccione tipo",
        "Físico",
        "Psicológico",
        "Escolar",
        "Animal"
    )

    // Referencia a Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("reportes") //apunta al nodo reportes

    // Definición de colores personalizados con tema morado
    val primaryPurple = Color(0xFF6A1B9A)
    val darkPurple = Color(0xFF4A148C)
    val lightPurple = Color(0xFF9C27B0)
    val accentPurple = Color(0xFFAB47BC)
    val backgroundGray = Color(0xFFF5F5F5)

    // Contenedor principal que ocupa toda la pantalla y permite scroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
            .verticalScroll(scrollState)
    ) {
        // Header superior con fondo morado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(darkPurple, primaryPurple)
                    )
                )
                .padding(vertical = 40.dp, horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sistema de Reportes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registro de Casos de Maltrato",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Formulario sin cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Complete el siguiente formulario",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkPurple,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Tipo de maltrato
            Text(
                text = "Tipo de Maltrato",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expandedMenu,
                onExpandedChange = { expandedMenu = it }
            ) {
                OutlinedTextField(
                    value = selectedTipo,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        unfocusedBorderColor = Color(0xFFCCCCCC),
                        focusedLabelColor = primaryPurple,
                        cursorColor = primaryPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    tiposMaltrato.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                selectedTipo = tipo
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            Text(
                text = "Descripción del Caso",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Describa detalladamente el caso...", color = Color(0xFF999999)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = primaryPurple
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ubicación
            Text(
                text = "Ubicación",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                placeholder = { Text("Ingrese barrio o ciudad", color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = primaryPurple
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen URL
            Text(
                text = "URL de Imagen (Opcional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                placeholder = { Text("https://ejemplo.com/imagen.jpg", color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = primaryPurple
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            Button(
                onClick = {
                    if (selectedTipo == "Seleccione tipo") {
                        Toast.makeText(context, "Seleccione un tipo de maltrato", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (descripcion.isEmpty() || ubicacion.isEmpty()) {
                        Toast.makeText(context, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val fechaHoraActual = sdf.format(Date())

                    scope.launch {
                        val nickname = userPrefs.nicknameFlow.first()
                        val esAnonimo = userPrefs.anonimoFlow.first()
                        val usuario = if (esAnonimo) "anónimo" else nickname

                        val id = UUID.randomUUID().toString()
                        val imagenTrimmed = imagenUrl.trim()

                        Log.d("MainActivity", "==== GUARDANDO REPORTE ====")
                        Log.d("MainActivity", "ID: $id")
                        Log.d("MainActivity", "Tipo: $selectedTipo")
                        Log.d("MainActivity", "Descripción: $descripcion")
                        Log.d("MainActivity", "Ubicación: $ubicacion")
                        Log.d("MainActivity", "Imagen URL: '$imagenTrimmed'")
                        Log.d("MainActivity", "Fecha: $fechaHoraActual")
                        Log.d("MainActivity", "Usuario: $usuario")

                        val reporte = Reporte(
                            id = id,
                            tipo = selectedTipo,
                            descripcion = descripcion,
                            ubicacion = ubicacion,
                            imagenUrl = if (imagenTrimmed.isNotEmpty()) imagenTrimmed else null,
                            fechaHora = fechaHoraActual,
                            usuario = usuario
                        )

                        ref.child(id).setValue(reporte).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Reporte guardado exitosamente", Toast.LENGTH_LONG).show()
                                selectedTipo = "Seleccione tipo"
                                descripcion = ""
                                ubicacion = ""
                                imagenUrl = ""
                            } else {
                                Toast.makeText(context, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryPurple
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Guardar Reporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Ver Reportes
            OutlinedButton(
                onClick = onNavigateToReportes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryPurple
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, primaryPurple)
            ) {
                Icon(
                    imageVector = Icons.Default.ViewList,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Ver Reportes Guardados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}