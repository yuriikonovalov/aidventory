package com.aidventory.core.common

import java.time.format.DateTimeFormatter

object AppDateTimeFormatter {
    fun fullDate(): DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}