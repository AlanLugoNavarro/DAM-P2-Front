package com.example.practica2.presentation.admin

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practica2.R.drawable.ic_back
import com.example.practica2.services.ApiService
import com.example.practica2.models.dto.UsuarioDTO
import com.example.practica2.ui.theme.Black
import com.example.practica2.ui.theme.Gray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import com.example.practica2.presentation.home.toImageBitmap
import com.example.practica2.presentation.inicial.LoadingOverlay
import com.example.practica2.ui.theme.Yellow

@Composable
fun AdminVista(navHostController: NavHostController){
    val usuariosList = remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    var carga by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getUsuarios()

        call.enqueue(object : Callback<List<UsuarioDTO>> {
            override fun onResponse(
                call: Call<List<UsuarioDTO>>,
                response: Response<List<UsuarioDTO>>
            ) {
                if (response.isSuccessful) {
                    usuariosList.value = response.body() ?: emptyList()
                    isLoading.value = false
                } else {
                    Log.e("API", "Error de respuesta: ${response.code()}")
                    isLoading.value = false
                }
            }

            override fun onFailure(call: Call<List<UsuarioDTO>>, t: Throwable) {
                Log.e("API", "Error al obtener usuarios: ${t.message}")
                isLoading.value = false
            }
        })
    }

    if(isLoading.value){
        LoadingOverlay()
        return
    }

    if (carga) {
        LoadingOverlay()
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Gray, Black)))
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Row(){
            Icon(painter = painterResource(id = ic_back),
                contentDescription = "",
                tint = White,
                modifier = Modifier.padding(vertical = 24.dp).size(24.dp).clickable {
                    navHostController.navigate("home")
                })
        }
        Text("Vista de administrador", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Button(onClick = {
            carga = true
            navHostController.navigate("registro")
        }, colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
            Text("Crear usuario", color = Black)
        }
        Spacer(modifier = Modifier.weight(1f))
        LazyColumn {
            items(usuariosList.value) { usuario ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {

                    usuario.imagen?.toImageBitmap()?.let { imagenBitmap ->
                    Image(
                        bitmap = imagenBitmap,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp) // o el tamaño que prefieras
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row{
                        Column(){
                            Text("Nombre: ${usuario.nombre}", color = Color.White)
                            Text("Apellido: ${usuario.apellido}", color = Color.White)
                            Text("Correo: ${usuario.correo}", color = Color.White)
                            Text("Rol: ${if (usuario.rol == 1) "Admin" else "Usuario"}", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = {
                            carga = true
                            navHostController.navigate("modificar/${usuario.correo}") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
                            Text("Modificar", color = Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:8080/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val apiService = retrofit.create(ApiService::class.java)
                            val call = apiService.eliminarUsuario(usuario.correo)
                            isLoading.value = true
                            call.enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Log.d("API", "Usuario eliminado con éxito")
                                        usuariosList.value = usuariosList.value.filterNot { it.correo == usuario.correo }
                                        isLoading.value = false
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("API", "Error: ${t.message}")
                                    isLoading.value = false
                                }
                            })

                        }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Eliminar", color = Black)
                        }
                    }
                }
            }
        }
    }
}