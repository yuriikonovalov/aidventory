package com.aidventory.core.backup.json

import com.aidventory.core.backup.Backup
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class MoshiJsonBackupConverter @Inject constructor() : JsonBackupConverter {
    private val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .build()

    override fun toJson(backup: Backup): String {
        val adapter = moshi.adapter(Backup::class.java)
        return adapter.toJson(backup)
    }

    override fun toContentJson(backupContent: Backup.Content): String {
        val adapter = moshi.adapter(Backup.Content::class.java)
        return adapter.toJson(backupContent)
    }

    override fun fromJson(json: String): Backup? {
        val adapter = moshi.adapter(Backup::class.java)
        return adapter.fromJson(json)
    }

    class LocalDateAdapter {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        @ToJson
        fun toJson(localDate: LocalDate?): String {
            return localDate?.format(formatter) ?: "null"
        }

        @FromJson
        fun fromJson(localDate: String): LocalDate? {
            return if (localDate == "null") {
                null
            } else {
                formatter.parse(localDate, LocalDate::from)
            }
        }
    }
}


