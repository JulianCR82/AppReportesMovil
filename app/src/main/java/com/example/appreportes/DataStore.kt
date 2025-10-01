package com.example.appreportes

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión de DataStore
val Context.dataStore by preferencesDataStore("user_prefs")

class DataStore(private val context: Context) {

    companion object {
        //se crean claves unicas para guardar y leer los valores en el DataStore
        val NICKNAME = stringPreferencesKey("nickname") //guarda un string
        val ANONIMO = booleanPreferencesKey("anonimo") //guarda un Boolean
    }

    // Guardar nickname y anónimo
    //metodo suspend porque el acceso a datastore es asincrono
    suspend fun savePreferences(nickname: String, anonimo: Boolean) {
        context.dataStore.edit { prefs -> //se usa edit para modificar los valores almacenadas
            prefs[NICKNAME] = nickname
            prefs[ANONIMO] = anonimo //guarda el nickname y el valor de anonimo
        }
    }

    // Leer nickname
    val nicknameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NICKNAME] ?: "Anónimo"  //si no existe nada guardado, devuelve anonimo
        //como es flow se puede observar en tiempo real (se reactualiza cuando cambian los datos)
    }

    // Leer estado de anónimo
    val anonimoFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ANONIMO] ?: false //si no hay valor guardado, por defecto retorna false
    }
}
