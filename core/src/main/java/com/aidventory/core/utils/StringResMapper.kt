package com.aidventory.core.utils

import android.content.Context
import com.aidventory.core.R

internal fun mapValueToStringResValue(context: Context, value: String): String {
    // 'value' can contain either a custom value or a string resource id.
    // At first we consider it as a string resource id. If getString() returns null,
    // then we return the value as it is.
    return getString(context, value) ?: value
}

private fun getString(context: Context, stringResourceId: String): String? {
    return try {
        val resourceId = R.string::class.java
            .getField(stringResourceId)
            .getInt(null)
        context.resources.getString(resourceId)
    } catch (e: Exception) {
        null
    }
}
