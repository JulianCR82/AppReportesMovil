package com.example.appreportes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class RegistroActivity : ComponentActivity() {

    private lateinit var preferencesManager: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inicializarComponentes()
        mostrarPantallaInicio()
    }

    private fun inicializarComponentes() {
        // Esto nos permite guardar y leer datos como el nickname del usuario
        preferencesManager = DataStore(this)
    }

    private fun mostrarPantallaInicio() {
        setContent {
            MaterialTheme {
                InicioScreen(
                    userPrefs = preferencesManager,
                    onNavigateToMain = { irAPantallaPrincipal() }
                )
            }
        }
    }

    private fun irAPantallaPrincipal() {
        // Creamos un Intent para iniciar la MainActivity
        val intentMain = Intent(this, MainActivity::class.java)
        startActivity(intentMain)
        finalizarActividad()
    }

    private fun finalizarActividad() {
        finish()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    userPrefs: DataStore,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estado para almacenar el nickname que el usuario escribe
    // remember mantiene el valor durante recomposiciones
    var nickname by remember { mutableStateOf("") }

    // Por defecto es false (no anónimo)
    var esAnonimo by remember { mutableStateOf(false) }

    val primaryPurple = Color(0xFF7B1FA2)
    val lightPurple = Color(0xFF9C27B0)
    val darkPurple = Color(0xFF6A1B9A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header superior con morado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            darkPurple,
                            primaryPurple,
                            lightPurple
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Logo circular
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    shadowElevation = 0.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(65.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sistema de Reportes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Registra tus incidentes de manera segura",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Formulario directo sin card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 36.dp, bottom = 32.dp)
        ) {
            // Campo de texto
            Text(
                text = "Nickname",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = {
                    Text(
                        "Ingrese su nickname",
                        color = Color(0xFFBDBDBD)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !esAnonimo,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (esAnonimo) Color(0xFF9E9E9E) else lightPurple
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = lightPurple,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    disabledBorderColor = Color(0xFFF0F0F0),
                    disabledTextColor = Color(0xFFBDBDBD),
                    cursorColor = lightPurple,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color(0xFFFAFAFA)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Opción anónima
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (esAnonimo)
                            lightPurple.copy(alpha = 0.12f)
                        else
                            Color.White
                    )
                    .padding(16.dp)
            ) {
                Checkbox(
                    checked = esAnonimo,
                    onCheckedChange = {
                        esAnonimo = it
                        if (it) nickname = ""
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = lightPurple,
                        uncheckedColor = Color(0xFF9E9E9E)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Prefiero no decirlo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (esAnonimo) primaryPurple else Color(0xFF424242)
                    )
                    Text(
                        text = "Continua de forma anónima",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón de ingreso
            Button(
                onClick = {
                    if (nickname.trim().isEmpty() && !esAnonimo) {
                        Toast.makeText(
                            context,
                            "Ingrese un nombre o active el modo anónimo",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    scope.launch {
                        userPrefs.savePreferences(
                            if (esAnonimo) "Anónimo" else nickname.trim(),
                            esAnonimo
                        )
                        onNavigateToMain()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightPurple
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ingresar al Sistema",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))


        }
    }
}