package com.example.practica2.presentation.home

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.practica2.Objetos.SesionPrefs
import com.example.practica2.R
import com.example.practica2.R.drawable.ic_back
import com.example.practica2.services.ApiService
import com.example.practica2.models.UserModel
import com.example.practica2.models.dto.ModifyUserDTO
import com.example.practica2.presentation.inicial.LoadingOverlay
import com.example.practica2.presentation.registro.contieneNumeros
import com.example.practica2.presentation.registro.convertirImagenBase64
import com.example.practica2.presentation.registro.esCorreoValido
import com.example.practica2.ui.theme.Black
import com.example.practica2.ui.theme.Gray
import com.example.practica2.ui.theme.SelectedField
import com.example.practica2.ui.theme.UnselectedField
import com.example.practica2.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun ModificarVista(navHostController: NavHostController, correoBuscar: String){
    var nombre by remember {mutableStateOf("")}
    var apellido by remember {mutableStateOf("")}
    var correo by remember {mutableStateOf("")}
    var contraseña by remember {mutableStateOf("")}
    var imageUri by remember { mutableStateOf<Any?>(null) }
    var descerror by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var exito by remember {mutableStateOf(false)}
    val correoUsuario by SesionPrefs.leerCorreo(context).collectAsState(initial = "")
    val usuarioState = remember { mutableStateOf<UserModel?>(null) }
    val usuarioState2 = remember { mutableStateOf<UserModel?>(null) }
    var originalImageFuente by remember { mutableStateOf<Any?>(null) }
    var correoActual by remember { mutableStateOf("") }
    var rolActual by remember { mutableStateOf(0) }
    var rolPrimero by remember { mutableStateOf(0) }
    var imagenCargada by remember { mutableStateOf(false) }
    var admin by remember { mutableStateOf(false) }

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    LaunchedEffect(correoBuscar) {
        if (correoBuscar?.isNotBlank() == true) {
            val call = correoBuscar?.let { apiService.getUsuario(it) }
            if (call != null) {
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            usuarioState.value = response.body()
                            rolPrimero = usuarioState.value?.rol!!
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.e("API", "Error al obtener usuario: ${t.message}")
                    }
                })
            }
        }
    }

    LaunchedEffect(correoUsuario) {
        if (correoUsuario?.isNotBlank() == true) {
            val call = correoUsuario?.let { apiService.getUsuario(it) }
            if (call != null) {
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            usuarioState2.value = response.body()
                            if(usuarioState2.value?.rol == 1)
                                admin = true
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.e("API", "Error al obtener usuario: ${t.message}")
                    }
                })
            }
        }
    }

    if(usuarioState.value == null){
        LoadingOverlay()
        return
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
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
                    if(correoUsuario != correoBuscar){
                        navHostController.navigate("admin") {
                            popUpTo("modificar") { inclusive = true }
                        }
                    }else{
                        navHostController.navigate("home") {
                            popUpTo("modificar") { inclusive = true }
                        }
                    }
                })
            Spacer(modifier = Modifier.weight(1f))
        }
        Text("Modificar Datos", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
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
        if(admin){
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rol", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = rolActual == 0,
                    onClick = { rolActual = 0 }
                )

                Text("Usuario", color = Color.White)

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = rolActual == 1,
                    onClick = { rolActual = 1 }
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
            if(!imagenCargada){
                usuarioState.value?.let { usuario ->
                    usuario.imagen?.toImageBitmap()?.let { imagenBitmap ->
                        imageUri = imagenBitmap
                        originalImageFuente = imagenBitmap
                        correoActual = usuario.correo
                        rolActual = usuario.rol!!
                        imagenCargada = true
                    }
                }
            }
            when (val fuente = imageUri) {
                is Uri -> Image(
                    painter = rememberAsyncImagePainter(fuente),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(100.dp)
                )
                is ImageBitmap -> Image(
                    bitmap = fuente,
                    contentDescription = "Imagen recuperada",
                    modifier = Modifier.size(100.dp)
                )
                else -> Image(
                    painter = painterResource(id = R.drawable.usuario),
                    contentDescription = "Imagen por defecto",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.isEmpty() && apellido.isEmpty() && correo.isEmpty() && contraseña.isEmpty() && imageUri == originalImageFuente && rolActual == rolPrimero) {
                error = true
                descerror = "Debe modificar almenos un campo"
                return@Button
            }

            if (!esCorreoValido(correo.lowercase()) && !correo.isEmpty()) {
                error = true
                descerror = "El correo no es válido"
                return@Button
            }

            if (contraseña.length < 8 && !contraseña.isEmpty()) {
                error = true
                descerror = "La contraseña debe tener al menos 8 caracteres"
                return@Button
            }

            if ((contieneNumeros(nombre.lowercase()) && !nombre.isEmpty()) || (contieneNumeros(apellido.lowercase()) && !apellido.isEmpty())) {
                error = true
                descerror = "El nombre y el apellido no pueden contener números"
                return@Button
            }
                val imagenBase64 = when(val fuente = imageUri){
                    is Uri -> convertirImagenBase64(fuente, context)
                    else -> null
                }
                val user = ModifyUserDTO(
                    nombre = nombre,
                    apellido = apellido,
                    nuevoCorreo = correo,
                    correoActual = correoBuscar,
                    contraseña = contraseña,
                    imagenBase64 = imagenBase64,
                    rol = rolActual
                )
                val call = apiService.modificarUsuario(user)
                call.enqueue(object : Callback<Int> {
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        if (response.isSuccessful) {
                            Log.d("API", "Usuario Modificado: ${response.body()}")
                            exito = true
                            coroutineScope.launch {
                                if(correo.isEmpty())
                                    if(!admin)
                                        SesionPrefs.guardarCorreo(context, correoBuscar)
                                else
                                    if(!admin)
                                        SesionPrefs.guardarCorreo(context, correo)
                                delay(300)
                                if(admin){
                                    navHostController.navigate("admin") {
                                        popUpTo("modificar") { inclusive = true }
                                    }
                                }else{
                                    navHostController.navigate("home") {
                                        popUpTo("modificar") { inclusive = true }
                                    }
                                }

                            }
                            // Puedes mostrar un mensaje de éxito aquí
                        } else {
                            Log.e("API", "Error al modificar usuario: ${response.code()}")
                            error = true
                            descerror = "Error en el registro"
                        }
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        Log.e("API", "Fallo en la petición: ${t.message}")
                        error = true
                        descerror = "No se pudo conectar con el servidor"
                    }
                })
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)) {
            Text("Modificar", color = Black)
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
                text = {Text("Usuario modificado correctamente")},
                confirmButton = { }
            )
        }
    }
}