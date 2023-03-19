package com.aidventory.core.barcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Creates a bitmap that contains a QR code.
 *
 * @param content text that should be encoded in the QR code.
 * @param size the preferred size (width, height) of the bitmap in pixels.
 * @param color the color of the QR code.
 *
 * @return a [Bitmap] or null if an exception is thrown.
 */
fun createQRCodeBitmap(
    content: String,
    size: Int,
    color: Int
): Bitmap? {
    return try {
        val result: BitMatrix = MultiFormatWriter().encode(
            content, BarcodeFormat.QR_CODE, size, size, null
        )

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)

        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] =
                    if (result[x, y]) color else android.graphics.Color.TRANSPARENT
            }
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, size, 0, 0, w, h)

        bitmap
    } catch (e: Exception) {
        null
    }
}