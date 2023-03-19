package com.aidventory.core.barcode.export


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.text.TextPaint
import com.aidventory.core.barcode.createQRCodeBitmap
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject


internal class QrPdfFileWriterImpl @Inject constructor() : QrPdfFileWriter {
    private val textPaint = TextPaint(Paint().apply {
        color = Color.BLACK
        textSize = 14f
    })

    @Throws(NullPointerException::class, IOException::class)
    override fun writeDocument(
        outputStream: OutputStream,
        label: String,
        content: String
    ) {
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        page.canvas.drawQrCode(content, label)
        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }

    private fun Canvas.drawQrCode(content: String, label: String) {
        val bitmapSize = calculateBitmapSize(width)
        val bitmapLeft = calculateBitmapLeftPoint(width, bitmapSize)
        val bitmap = requireNotNull(createQRCodeBitmap(content, bitmapSize, Color.BLACK))

        drawBitmap(bitmap, bitmapLeft, 0f, null)
        drawLabel(label, bitmapSize)
    }

    private fun Canvas.drawLabel(label: String, bitmapHeight: Int) {
        val bounds = Rect()
        textPaint.getTextBounds(label, 0, label.length, bounds)
        val x = bounds.getX(width)
        val y = bounds.getY(bitmapHeight)
        drawText(label, x, y, textPaint)
    }

    /**
     * Calculates x point for the text so that the text is placed center horizontally.
     */
    private fun Rect.getX(pageWidth: Int): Float {
        val middle = pageWidth / 2
        val halfTextWidth = width() / 2
        return (middle - halfTextWidth).toFloat()
    }

    /**
     * Calculates y point for the text so that the text is placed below the QR code.
     */
    private fun Rect.getY(bitmapHeight: Int): Float {
        val spacing = 14f
        return bitmapHeight + spacing + height()
    }


    /**
     * Calculates the size of a bitmap given that it should be 30% of the width of a page.
     */
    private fun calculateBitmapSize(pageWidth: Int) = pageWidth / 3

    /**
     * Calculates the left point of the bitmap so that it is placed center horizontall.
     */
    private fun calculateBitmapLeftPoint(pageWidth: Int, bitmapSize: Int): Float {
        return (pageWidth - bitmapSize).toFloat() / 2
    }

    companion object {
        // According to PostScript the size of A4 portrait format is 595x842.
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
    }
}