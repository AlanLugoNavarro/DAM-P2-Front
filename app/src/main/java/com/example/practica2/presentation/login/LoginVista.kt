package com.example.practica2.presentation.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import com.example.practica2.Objetos.SesionPrefs
import com.example.practica2.R.drawable.ic_back
import com.example.practica2.services.ApiService
import com.example.practica2.models.UserModel
import com.example.practica2.models.dto.LoginDTO
import com.example.practica2.presentation.inicial.LoadingOverlay
import com.example.practica2.presentation.registro.esCorreoValido
import com.example.practica2.ui.theme.Black
import com.example.practica2.ui.theme.Gray
import com.example.practica2.ui.theme.SelectedField
import com.example.practica2.ui.theme.UnselectedField
import com.example.practica2.ui.theme.Yellow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Composable
fun LoginVista(navHostController: NavHostController){
    var correo by remember {mutableStateOf("")}
    var contraseña by remember {mutableStateOf("")}
    var error by remember { mutableStateOf(false)}
    var descerror by remember { mutableStateOf("")}
    var exito by remember {mutableStateOf(false)}
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var carga by remember { mutableStateOf(false) }

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

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
                    carga = true
                    navHostController.navigate("inicial")
                })
            Spacer(modifier = Modifier.weight(1f))
        }
        Text("Inicio de Sesión", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Correo", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextField(value = correo,
            onValueChange = {correo = it},
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Contraseña", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextField(value = contraseña,
            onValueChange = {contraseña = it},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (correo.isEmpty() || contraseña.isEmpty()) {
                error = true
                descerror = "Debe rellenar todos los campos"
                return@Button
            }

            if (!esCorreoValido(correo.lowercase())) {
                error = true
                descerror = "El correo no es válido"
                return@Button
            }

            if (contraseña.length < 8) {
                error = true
                descerror = "La contraseña debe tener al menos 8 caracteres"
                return@Button
            }

            iniciarSesion(correo, contraseña, apiService) { resultado ->
                if (resultado == 1) {
                    Log.d("API", "CORREO Y CONTRASEÑA CORRECTOS: ${resultado}")
                    exito = true
                    carga = true
                    coroutineScope.launch {
                        SesionPrefs.guardarCorreo(context, correo)
                        navHostController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                } else {
                    Log.d("API", "CREDENCIALES INCORRECTAS: ${resultado}")
                    error = true
                    descerror = "Credenciales incorrectas"
                }
            }

        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)){
            Text("Iniciar Sesión", color = Black)
        }

        if(error){
            AlertDialog(
                onDismissRequest = {error = false},
                title = { Text("Error") },
                text = { Text(descerror) },
                confirmButton = { }
            )
        }
        if(exito){
            AlertDialog(
                onDismissRequest = {exito = false},
                title = {Text("Exito")},
                text = {Text("Sesion Iniciada Correctamente")},
                confirmButton = { }
            )
        }
    }
}

fun iniciarSesion(correo: String, contraseña: String, apiService: ApiService, onResultado: (Int) -> Unit) {
    Log.d("LOGIN", "Enviando solicitud con: $correo / $contraseña")
    val loginData = LoginDTO(correo, contraseña)
    val user = UserModel(
        nombre = "",
        apellido = "",
        correo = correo,
        contraseña = contraseña,
        imagenBase64 = ""
    )

    val call = apiService.login(loginData)
    call.enqueue(object : Callback<Int> {
        override fun onResponse(call: Call<Int>, response: Response<Int>) {
            val resultado = response.body() ?: 0
            onResultado(resultado)
        }

        override fun onFailure(call: Call<Int>, t: Throwable) {
            Log.e("LOGIN", "Fallo en la petición: ${t.message}")
            onResultado(0)
        }
    })
}
