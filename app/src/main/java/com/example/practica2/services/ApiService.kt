package com.example.practica2.services

import com.example.practica2.models.UserModel
import com.example.practica2.models.dto.LoginDTO
import com.example.practica2.models.dto.ModifyUserDTO
import com.example.practica2.models.dto.UsuarioDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
// Interfaz que permite realizar las consultas
interface ApiService {
    @POST("usuario/saveUser")
    fun saveUser(@Body user: UserModel): Call<UserModel> // Envia un userModel por el cuerpo de la peticion y recibe un UserModel

    @GET("usuario/comprobarCorreo")
    fun comprobarCorreo(@Query("correo") correo: String): Call<Int> // Envia el correo

    @POST("usuario/login")
    fun login(@Body user: LoginDTO): Call<Int> // Se envia un LoginDTO y se recibe un Int

    @GET("usuario/usuario/{correo}")
    fun getUsuario(@Path("correo") correo: String): Call<UserModel> // Obtiene al Usuario con el correo enviado

    @PUT("usuario/modificar")
    fun modificarUsuario(@Body user: ModifyUserDTO): Call<Int> // Modifica al usuario

    @GET("usuario/lista")
    fun getUsuarios(): Call<List<UsuarioDTO>> // Obtiene la lista de usuarios

    @DELETE("usuario/eliminar/{correo}")
    fun eliminarUsuario(@Path("correo") correo: String): Call<Void> // Elimina al usuario con el correo enviado
}