package com.aidventory.core.barcode.export

import java.io.OutputStream

/**
 * Interface for classes that implement creating a PDF file with a barcode.
 */
internal interface QrPdfFileWriter {

    /**
     * Writes a barcode PDF file of the provided label and content in the provided output stream.
     *
     * @param outputStream a stream the file should be written in.
     * @param label a label of the QR code image.
     * @param content text encoded in the QR code.
     */
    fun writeDocument(
        outputStream: OutputStream,
        label: String,
        content: String
    )
}