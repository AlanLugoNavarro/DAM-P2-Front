package com.example.practica2.models

import com.google.gson.annotations.SerializedName
import android.util.Base64

data class UserModel(
    val id: Int? = null,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contraseña: String,
    val rol: Int? = null,
    @SerializedName("imagen") // Indica que si en el JSON llega un elemento "imagen", lo recibira la variable ImagenBase64
    val imagenBase64: String? = null
) {
    val imagen: ByteArray?
        get() = imagenBase64?.let { // Si no esta vacia convierte el base64 en un byte array
            if (it.isNotBlank()) Base64.decode(it, Base64.DEFAULT) else null
        }

}

