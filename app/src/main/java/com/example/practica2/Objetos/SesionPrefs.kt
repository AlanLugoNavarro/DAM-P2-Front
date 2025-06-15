package com.example.practica2.Objetos

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea un archivo XML con el nombre user_prefs que guarda información de sesión
val Context.dataStore by preferencesDataStore(name = "user_prefs")

object SesionPrefs {
    // Se define el identificador correo para guardar la información de sesión
    private val CORREO_KEY = stringPreferencesKey("correo")

    suspend fun guardarCorreo(context: Context, correo: String) {
        context.dataStore.edit { prefs ->
            prefs[CORREO_KEY] = correo
        }
    }

    fun leerCorreo(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[CORREO_KEY]
        }
    }

    suspend fun borrarSesion(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(CORREO_KEY)
        }
    }
}

