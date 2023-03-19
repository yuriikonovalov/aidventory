package com.aidventory.core.domain.usecases

import android.net.Uri
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.interfaces.QrExporter
import javax.inject.Inject


/**
 * Returns [Uri] of a PDF file that can be passed to third-party apps in order to send a QR.
 */
class SendQrUseCase @Inject constructor(private val exporter: QrExporter) {
    suspend operator fun invoke(barcode: String): Result<Uri> {
        return try {
            val content = QrExporter.Content(barcode, barcode)
            val uri = exporter.exportInCache(content)
            Result.Success(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}