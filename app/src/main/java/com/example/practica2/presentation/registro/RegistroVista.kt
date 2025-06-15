package com.example.practica2.presentation.registro

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.practica2.R
import com.example.practica2.R.drawable.ic_back
import com.example.practica2.services.ApiService
import com.example.practica2.models.UserModel
import com.example.practica2.ui.theme.Black
import com.example.practica2.ui.theme.Gray
import com.example.practica2.ui.theme.SelectedField
import com.example.practica2.ui.theme.UnselectedField
import com.example.practica2.ui.theme.Yellow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Base64
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.practica2.Objetos.SesionPrefs
import com.example.practica2.presentation.inicial.LoadingOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistroVista(navHostController: NavHostController){
    var nombre by remember {mutableStateOf("")}
    var apellido by remember {mutableStateOf("")}
    var correo by remember {mutableStateOf("")}
    var contraseña by remember {mutableStateOf("")}
    // Uri es una clase que representa una referencia a una direccion de un recurso
    // Puede ser una imagen, un video, un archivo, una direccion web o una ruta interna
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var error by remember { mutableStateOf(false) }
    var descerror by remember { mutableStateOf("") }
    var exito by remember {mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    var carga by remember { mutableStateOf(false) }
    var rolActual by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf(0) }
    // Obtiene el correo guardado en la sesion activa
    val correoUsuario by SesionPrefs.leerCorreo(context).collectAsState(initial = "")
    // Variable que almacenara la información del usuario
    val usuarioState = remember { mutableStateOf<UserModel?>(null) }

    val retrofit = Retrofit.Builder() // Crea una instancia de Retrofit
        .baseUrl("http://10.0.2.2:8080/") // Indica la direccion del backend
        .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos kotlin con Gson
        .build() // Construye el objeto retrofit

    val apiService = retrofit.create(ApiService::class.java) // Crea una instancia del servicio

    val launcher = rememberLauncherForActivityResult( // Crea un launcher para iniciar una actividad externa y lo recuerda mientras el Compose este activo
        contract = ActivityResultContracts.GetContent() // abre el selector de imagenes
    ) { uri: Uri? -> // Si lo seleccionado es una imagen se obtiene su Uri
        imageUri = uri // El URI es el concepto general que incluye el URL (Uniform Resource Locator) y el URN (Uniform Resource Name)
    }

    LaunchedEffect(correoUsuario) {
        if (correoUsuario?.isNotBlank() == true) {
            val call = correoUsuario?.let { apiService.getUsuario(it) }
            if (call != null) {
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            usuarioState.value = response.body()
                            if(usuarioState.value?.rol == 1)
                                rolActual = true
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.e("API", "Error al obtener usuario: ${t.message}")
                    }
                })
            }
        }
    }

    if(carga){
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
                    if(!rolActual){
                        navHostController.navigate("inicial") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }else{
                        navHostController.navigate("admin") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }
                })
            Spacer(modifier = Modifier.weight(1f))
        }
        Text("Registro", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nombre", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextField(value = nombre,
            onValueChange = {nombre = it},
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Apellido", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextField(value = apellido,
            onValueChange = {apellido = it},
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ))
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
        if(rolActual){
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rol", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = rol == 0,
                    onClick = { rol = 0 }
                )

                Text("Usuario", color = Color.White)

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = rol == 1,
                    onClick = { rol = 1 }
                )
                Text("Admin", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Imagen", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically){
            Button(onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .weight(1f)
                    .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
                Text("Seleccionar Imagen", color = Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = if(imageUri != null){ rememberAsyncImagePainter(imageUri)}else
                {painterResource(id = R.drawable.usuario)},
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
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

            if (contieneNumeros(nombre.lowercase()) || contieneNumeros(apellido.lowercase())) {
                error = true
                descerror = "El nombre y el apellido no pueden contener números"
                return@Button
            }

            buscarCorreo(correo.lowercase(), apiService) { resultado ->
                if (resultado == 1) {
                    error = true
                    descerror = "El correo ya existe"
                } else {
                    Log.d("API", "USUARIO CON CORREO NO EXISTENTE: ${resultado}")
                    val user = UserModel(
                        nombre = nombre,
                        apellido = apellido,
                        correo = correo,
                        contraseña = contraseña,
                        rol = rol,
                        imagenBase64 = imageUri?.let { convertirImagenBase64(it, context) }
                    )
                    val call = apiService.saveUser(user)
                    call.enqueue(object : Callback<UserModel> {
                        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                            if (response.isSuccessful) {
                                Log.d("API", "Usuario guardado: ${response.body()}")
                                exito = true
                                carga = true
                                coroutineScope.launch {
                                    SesionPrefs.guardarCorreo(context, correo)
                                    delay(300)
                                    if(!rolActual){
                                        navHostController.navigate("home") {
                                            popUpTo("registro") { inclusive = true }
                                        }
                                    }else{
                                        navHostController.navigate("admin") {
                                            popUpTo("registro") { inclusive = true }
                                        }
                                    }

                                }
                            } else {
                                Log.e("API", "Error al registrar usuario: ${response.code()}")
                                error = true
                                descerror = "Error en el registro"
                            }
                        }

                        override fun onFailure(call: Call<UserModel>, t: Throwable) {
                            Log.e("API", "Fallo en la petición: ${t.message}")
                            error = true
                            descerror = "No se pudo conectar con el servidor"
                        }
                    })
                }
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
            Text("Registrarse", color = Black)
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
                text = {Text("Usuario registrado correctamente")},
                confirmButton = { }
            )
        }
    }
}

fun convertirImagenBase64(uri: Uri, context: Context): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    return if (bytes != null) Base64.encodeToString(bytes, Base64.DEFAULT) else ""
}

fun esCorreoValido(email: String): Boolean {
    val regex = "^[\\w.-]+@[\\w-]+\\.[a-z]{2,4}$".toRegex(RegexOption.IGNORE_CASE)
    return regex.matches(email)
}

fun contieneNumeros(texto: String): Boolean {
    return texto.any { it.isDigit() }
}

fun buscarCorreo(correo: String, apiService: ApiService, onResultado: (Int) -> Unit) {
    val call = apiService.comprobarCorreo(correo)
    call.enqueue(object : Callback<Int> {
        override fun onResponse(call: Call<Int>, response: Response<Int>) {
            val resultado = response.body() ?: 0 // Si es `null`, devuelve `0`
            Log.d("API", "Resultado obtenido: $resultado") // Verifica lo que recibe
            onResultado(resultado)
        }

        override fun onFailure(call: Call<Int>, t: Throwable) {
            Log.e("API", "Error en la petición: ${t.message}")
            onResultado(0)
        }
    })
}