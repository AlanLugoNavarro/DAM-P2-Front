package com.example.practica2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.practica2.presentation.admin.AdminVista
import com.example.practica2.presentation.home.HomeVista
import com.example.practica2.presentation.home.ModificarVista
import com.example.practica2.presentation.inicial.InicialVista
import com.example.practica2.presentation.inicial.LoadingVista
import com.example.practica2.presentation.login.LoginVista
import com.example.practica2.presentation.registro.RegistroVista

@Composable
fun NavigationWrapper(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = "loading") {
        composable("inicial") {
            InicialVista(navHostController)
        }

        composable("registro"){
            RegistroVista(navHostController)
        }

        composable("login"){
            LoginVista(navHostController)
        }

        composable("home"){
            HomeVista(navHostController)
        }

        composable("loading"){
            LoadingVista(navHostController)
        }

        composable("modificar/{correo}"){ backStackEntry ->
            val correo = backStackEntry.arguments?.getString("correo")
            if(correo != null)
                ModificarVista(navHostController, correo)
        }

        composable("admin"){
            AdminVista(navHostController)
        }
    }
}
