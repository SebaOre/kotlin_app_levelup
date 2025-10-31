package com.example.kotlin_app_levelup.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListInt(list: List<Int>?): String =
        list?.joinToString(",") ?: ""

    @TypeConverter
    fun toListInt(csv: String?): List<Int> =
        csv?.takeIf { it.isNotBlank() }?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
}
