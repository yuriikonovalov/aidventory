package com.aidventory.core.domain.interfaces

import android.net.Uri

interface QrExporter {
    suspend fun export(content: Content, destination: Uri)
    suspend fun exportInCache(content: Content): Uri

    data class Content(val label: String, val value: String)
}