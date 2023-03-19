package com.aidventory.core.barcode.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.aidventory.core.common.di.AppDispatcher
import com.aidventory.core.common.di.Dispatcher
import com.aidventory.core.domain.interfaces.QrExporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class PdfFileQrExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
    private val qrPdfFileWriter: QrPdfFileWriter
) : QrExporter {

    override suspend fun export(content: QrExporter.Content, destination: Uri) {
        withContext(ioDispatcher) {
            context.contentResolver.openOutputStream(destination)!!.use { outputStream ->
                qrPdfFileWriter.writeDocument(
                    outputStream = outputStream,
                    label = content.label,
                    content = content.value
                )
            }
        }
    }

    override suspend fun exportInCache(content: QrExporter.Content): Uri {
        return withContext(ioDispatcher) {
            val fileName = content.label.toFileName()
            val file = File("${context.cacheDir}/$fileName")
            val uri = context.contentUri(file)

            if (uri.isCached()) return@withContext uri

            FileOutputStream(file).use { outputStream ->
                outputStream.use {
                    qrPdfFileWriter.writeDocument(
                        outputStream = outputStream,
                        label = content.label,
                        content = content.value
                    )
                }

            }
            uri
        }
    }

    private fun Context.contentUri(file: File): Uri {
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(this, authority, file)
    }

    private fun String.toFileName() = "aidventory-$this.pdf"

    /**
     * Returns true if the [Uri] points at an existing file.
     */
    private fun Uri?.isCached(): Boolean {
        return this?.let {
            return@let try {
                val inputStream = context.contentResolver.openInputStream(this)
                inputStream?.close()
                true
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}