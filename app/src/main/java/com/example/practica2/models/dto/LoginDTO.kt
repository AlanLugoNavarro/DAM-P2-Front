package com.example.practica2.models.dto

// DTO Es Data Transfer Object, son clases que sirven exclusivamente para transportar datos
// Esta clase es usada para hacer el login con el back
data class LoginDTO(
    val correo: String,
    val contraseña: String
)
