package com.aidventory.core.database.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class LocalDateConverter {

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            formatter.parse(it, LocalDate::from)
        }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(formatter)
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}