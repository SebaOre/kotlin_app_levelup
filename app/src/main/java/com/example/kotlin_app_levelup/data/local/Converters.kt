package com.example.kotlin_app_levelup.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListInt(list: List<Int>?): String =
        list?.joinToString(",") ?: ""

    @TypeConverter
    fun toListInt(csv: String?): List<Int> =
        csv?.takeIf { it.isNotBlank() }?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString("|")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return if (data.isEmpty()) emptyList()
        else data.split("|")
    }
}
