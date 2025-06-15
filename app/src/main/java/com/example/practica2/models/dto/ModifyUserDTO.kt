package com.example.practica2.models.dto

import android.util.Base64
import com.google.gson.annotations.SerializedName

data class ModifyUserDTO(
    val correoActual: String,
    val nuevoCorreo: String,
    val nombre: String,
    val apellido: String,
    val rol: Int,
    val contraseña: String,
    @SerializedName("imagen")
    val imagenBase64: String? = null
) {
    val imagen: ByteArray?
        get() = imagenBase64?.let {
            if (it.isNotBlank()) Base64.decode(it, Base64.DEFAULT) else null
        }
}