package com.aidventory.core.domain.entities

import android.content.Context
import com.aidventory.core.utils.mapValueToStringResValue

data class SupplyUse(
    val id: Int,
    val name: String,
    val isDefault: Boolean
) {
    fun displayName(context: Context): String {
        return if (isDefault) {
            mapValueToStringResValue(context, name)
        } else {
            name
        }
    }
}
