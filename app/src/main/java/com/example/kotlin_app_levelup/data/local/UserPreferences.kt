package com.example.kotlin_app_levelup.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

// Crea una instancia única de DataStore
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // === CLAVES DE DATOS ===
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_AGE_KEY = intPreferencesKey("user_age")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    // === GUARDAR USUARIO ===
    suspend fun saveUser(name: String, email: String, age: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_AGE_KEY] = age
        }
    }

    // === LEER USUARIO ===
    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY] ?: ""
    }

    val userAgeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[USER_AGE_KEY] ?: 0
    }

    // === LOGIN / LOGOUT ===
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN_KEY] ?: false
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    // === BORRAR TODO ===
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
    val userImageFlow = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey("user_image")] ?: ""
    }

    suspend fun saveUserImage(imageUri: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("user_image")] = imageUri
        }
    }
    suspend fun saveUserImageForUser(email: String, imageUri: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("user_image_$email")] = imageUri
        }
    }

    val userImageFlowForCurrentUser: Flow<String> = userEmailFlow.flatMapLatest { email ->
        context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("user_image_$email")] ?: ""
        }
    }


}
