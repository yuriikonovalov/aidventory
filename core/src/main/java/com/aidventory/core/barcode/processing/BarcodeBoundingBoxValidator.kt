package com.aidventory.core.barcode.processing

import android.graphics.RectF
import kotlin.math.abs

interface BarcodeBoundingBoxValidator {
    /**
     * Checks if the bounding box of a barcode is large enough comparing to the scanner box.
     * @param scanner [RectF] of the scanner box
     * @param barcode [RectF] of the barcode
     *
     * @return true if the provided [barcode] is large enough, otherwise - false.
     */
    fun isLargeEnough(scanner: RectF, barcode: RectF): Boolean

    /**
     * Checks if the bounding box of a barcode is inside the scanner box.
     * @param scanner [RectF] of the scanner box
     * @param barcode [RectF] of the barcode
     *
     * @return true if the provided [barcode] is inside the [scanner].
     */
    fun isInsideScannerBox(scanner: RectF, barcode: RectF): Boolean
}

object BarcodeBoundingBoxValidatorImpl : BarcodeBoundingBoxValidator {
    override fun isLargeEnough(scanner: RectF, barcode: RectF): Boolean {
        val scannerRectWidth = abs(scanner.width())
        val scannerRectHeight = abs(scanner.height())

        val barcodeRectWidth = abs(barcode.width())
        val barcodeRectHeight = abs(barcode.height())

        val widthPercentage = barcodeRectWidth * 100 / scannerRectWidth
        val heightPercentage = barcodeRectHeight * 100 / scannerRectHeight
        return heightPercentage >= 80f || widthPercentage >= 80f
    }

    override fun isInsideScannerBox(scanner: RectF, barcode: RectF): Boolean {
        return scanner.contains(barcode)
    }
}