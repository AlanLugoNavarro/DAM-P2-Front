package com.example.practica2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.practica2.Objetos.SesionPrefs
import com.example.practica2.ui.theme.Practica2Theme

class MainActivity : ComponentActivity() {
    // Variable que permite navegar entre pantallas
    private lateinit var navHostController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica2Theme {
                //inicializa el controlador de navegación
                navHostController = rememberNavController()
                //Verifica si hay una sesion activa
                VerificarSesion(navHostController)
                //Se llama a la funcion de navegacion
                NavigationWrapper(navHostController)
            }
        }
    }
}
//Composable permite construir la interfaz de forma declarativa
@Composable // Función que verifica si hay una sesión activa
fun VerificarSesion(navController: NavHostController) {
    // Instancia que permite acceder a funciones del sistema operativo
    val context = LocalContext.current
    //Funcion que ejecuta un bloque de codigo solo una vez al iniciar la pantalla
    // Si cambia lo que este en el parentesis se ejecuta el codigo, en este caso el unit hace que solo se ejecute una vez
    LaunchedEffect(Unit) {
        //Hace la consulta y guarda el resultado en correo, debe estar dentro de una corrutina
        SesionPrefs.leerCorreo(context).collect { correo ->
            if (!correo.isNullOrEmpty()) {
                // Se realiza la navegacion a la pantalla home
                navController.navigate("home") {
                    //Elimina la pantalla actual y todas las anteriores de la pila de navegacion
                    // (Para que no pueda volver apretando el boton de la flechita del celular)
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
}