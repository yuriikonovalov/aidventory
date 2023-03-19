package com.aidventory.core.barcode

import com.aidventory.core.barcode.export.PdfFileQrExporter
import com.aidventory.core.barcode.export.QrPdfFileWriterImpl
import com.aidventory.core.barcode.generator.BarcodeGeneratorImpl
import com.aidventory.core.domain.interfaces.BarcodeGenerator
import com.aidventory.core.barcode.export.QrPdfFileWriter
import com.aidventory.core.domain.interfaces.QrExporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface BarcodeModule {
    @Binds
    fun bindsBarcodeGenerator(impl: BarcodeGeneratorImpl): BarcodeGenerator

    @Binds
    fun bindsQRCodePdfFileWriter(impl: QrPdfFileWriterImpl): QrPdfFileWriter

    @Binds
    fun bindsQrExporter(impl: PdfFileQrExporter): QrExporter
}