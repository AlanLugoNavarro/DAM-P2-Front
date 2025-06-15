package com.example.practica2.presentation.inicial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.practica2.Objetos.SesionPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun LoadingVista(navController: NavHostController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val correo = SesionPrefs.leerCorreo(context).first()
        delay(300)
        if (!correo.isNullOrEmpty()) {
            navController.navigate("home") {
                popUpTo("loading") { inclusive = true }
            }
        } else {
            navController.navigate("inicial") {
                popUpTo("loading") { inclusive = true }
            }
        }
    }
    LoadingOverlay()
}