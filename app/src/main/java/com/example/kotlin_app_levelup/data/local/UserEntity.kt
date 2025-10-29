package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val edad: Int,
    val contrasena: String,
    val registrado: Boolean = false
)
