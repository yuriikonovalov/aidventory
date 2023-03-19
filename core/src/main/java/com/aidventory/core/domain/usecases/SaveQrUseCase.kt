package com.aidventory.core.domain.usecases

import android.net.Uri
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.interfaces.QrExporter
import javax.inject.Inject

class SaveQrUseCase @Inject constructor(private val exporter: QrExporter) {
    suspend operator fun invoke(barcode: String, uri: Uri): Result<Unit> {
        return try {
            val content = QrExporter.Content(barcode, barcode)
            exporter.export(content = content, destination = uri)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}