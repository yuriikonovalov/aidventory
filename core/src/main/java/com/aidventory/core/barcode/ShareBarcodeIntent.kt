package com.aidventory.core.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat


/**
 * Creates an intent for sharing a Barcode PDF file from the provided Uri.
 *
 * @param uri a path the PDF file is shared by.
 */
fun Context.sendBarcodeIntent(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    ContextCompat.startActivities(this, arrayOf(Intent.createChooser(intent, "")), null)
}
