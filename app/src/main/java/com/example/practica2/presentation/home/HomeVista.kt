    package com.example.practica2.presentation.home

    import android.graphics.BitmapFactory
    import android.util.Log
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.aspectRatio
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Divider
    import androidx.compose.material3.Text
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
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.Color.Companion.White
    import androidx.compose.ui.graphics.ImageBitmap
    import androidx.compose.ui.graphics.asImageBitmap
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavHostController
    import com.example.practica2.Objetos.SesionPrefs
    import com.example.practica2.services.ApiService
    import com.example.practica2.models.UserModel
    import com.example.practica2.presentation.inicial.LoadingOverlay
    import com.example.practica2.ui.theme.Black
    import com.example.practica2.ui.theme.Gray
    import com.example.practica2.ui.theme.Yellow
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    @Composable
    fun HomeVista(navHostController: NavHostController){
        val usuarioState = remember { mutableStateOf<UserModel?>(null) }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val correoUsuario by SesionPrefs.leerCorreo(context).collectAsState(initial = "")
        var isLoggingOut by remember { mutableStateOf(false) }
        var rolActual by remember { mutableStateOf(false) }


        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

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

        if(usuarioState.value == null){
            LoadingOverlay()
            return
        }
        if (isLoggingOut) {
            LoadingOverlay()
            return
        }
        Column(
            modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Gray, Black)))
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Home", color = White)
            Spacer(modifier = Modifier.height(16.dp))
            usuarioState.value?.let { usuario ->
                usuario.imagen?.toImageBitmap()?.let { imagenBitmap ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, White, RoundedCornerShape(12.dp))
                            .background(Color.DarkGray)
                    ) {
                        Image(
                            bitmap = imagenBitmap,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                usuario.imagen?.let {
                    TablaUsuario(
                        nombre = usuario.nombre,
                        apellido = usuario.apellido,
                        correo = usuario.correo,
                        imagen = it
                    )
                }
            }

            Button(
                onClick = {
                    isLoggingOut = true
                    navHostController.navigate("modificar/${correoUsuario}"){
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(top = 32.dp)
                    .fillMaxWidth()
                    .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Text("Modificar Datos", color = Black)
            }

            Button(
                onClick = {
                    isLoggingOut = true
                    coroutineScope.launch {
                        delay(300) // opcional, da tiempo a que se dibuje el overlay
                        navHostController.navigate("admin") {
                            popUpTo("home") { inclusive = true }
                        }
                    }

                },
                modifier = Modifier.padding(top = 32.dp)
                    .fillMaxWidth()
                    .padding(32.dp), enabled = rolActual, colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Text("Menú de Administrador", color = Black)
            }

            Button(
                onClick = {
                    isLoggingOut = true
                    coroutineScope.launch {
                        delay(300) // opcional, da tiempo a que se dibuje el overlay
                        SesionPrefs.borrarSesion(context)
                        navHostController.navigate("inicial") {
                            popUpTo("home") { inclusive = true }
                        }
                    }

                },
                modifier = Modifier.padding(top = 32.dp)
                    .fillMaxWidth()
                    .padding(32.dp), colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Text("Cerrar sesión", color = Black)
            }
        }
    }

    @Composable
    fun TablaUsuario(nombre: String, apellido: String, correo: String, imagen: ByteArray) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Fila("Nombre", nombre)
            Divider()
            Fila("Apellido", apellido)
            Divider()
            Fila("Correo", correo)
            Divider()

        }
    }

    @Composable
    fun Fila(titulo: String, valor: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = titulo, fontWeight = FontWeight.Bold, color = White)
            Text(text = valor, color = White)
        }
    }

    fun ByteArray.toImageBitmap(): ImageBitmap {
        val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
        return bitmap.asImageBitmap()
    }

