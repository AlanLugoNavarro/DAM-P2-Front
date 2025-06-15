package com.example.practica2.presentation.inicial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practica2.ui.theme.Black
import com.example.practica2.ui.theme.Gray
import com.example.practica2.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InicialVista(navHostController: NavHostController){
    // Permite realizar corrutinas desde eventos como botones o gestos
    val coroutineScope = rememberCoroutineScope()
    // Crea una variable reactiva que es recordad mientras el Composable este en memoria
    // el by es para no tener que poner carga.value == false
    // La variable carga maneja el estado de la pantalla de carga
    var carga by remember { mutableStateOf(false) }
    if (carga) {
        // Muestra la pantalla de carga
        LoadingOverlay()
        return
    }
    // Column es un contenedor que apila los elementos en el eje Y
    Column(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Gray, Black))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // Crea un espacio vertical de 50 dp de altura
        Spacer(modifier = Modifier.height(50.dp))
        Text("Practica 2", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Button(onClick = {
            carga = true
            //Se ejecuta la corrutina
            coroutineScope.launch {
                delay(300) // da tiempo a que el loader se dibuje
                // Viaja a la pantalla Login
                navHostController.navigate("login")
            }
         },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
            Text(text = "Iniciar Sesion", color = Black)
        }
        Button(onClick = {
            carga = true
            coroutineScope.launch {
                delay(300)
                navHostController.navigate("registro")
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
            Text(text = "Registrarse", color = Black)
        }
    }
}

@Composable
fun LoadingOverlay() {
    // El contenedor Box apila los elementos en el eje Z
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        // El CircularProgressIndicator muestra un indicador de progreso circular
        CircularProgressIndicator(color = White)
    }
}
