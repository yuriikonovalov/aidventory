package com.aidventory.core.barcode


import android.graphics.Rect
import android.graphics.RectF
import com.aidventory.core.barcode.processing.BarcodeBoundingBoxValidator
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.processing.CoordinateTransformWrapper
import com.google.common.truth.Truth.assertThat
import com.google.mlkit.vision.barcode.common.Barcode
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class BarcodeProcessorTest {

    /* Values passed to the RectF constructor don't matter because a fake barcode bounding box
    validator is used for size and position checking. */
    private val scannerBox = RectF(0f, 0f, 0f, 0f)
    private val boundingBox = Rect(0, 0, 0, 0)
    private val coordinateTransform = CoordinateTransformWrapper {/* do nothing for test cases */ }

    @Test
    fun process_whenInactive_shouldNotProcessBarcodes() {
        val mockOnProcessed: (BarcodeProcessor.State) -> Unit = mock()
        val barcodeProcessor = BarcodeProcessor(
            onProcessed = mockOnProcessed,
            boundingBoxValidator = createBarcodeBoundingBoxValidator()
        )
        barcodeProcessor.isActive = false

        val barcodes = listOf(createBarcode(boundingBox, "123456789"))
        barcodeProcessor.process(barcodes, coordinateTransform, scannerBox)

        verify(mockOnProcessed, never()).invoke(any())
    }

    @Test
    fun process_whenActiveAndBarcodeIsLargeEnough_shouldPassRecognizeState() {
        var state: BarcodeProcessor.State = BarcodeProcessor.State.Sense
        val barcodeProcessor = BarcodeProcessor(
            onProcessed = { state = it },
            boundingBoxValidator = createBarcodeBoundingBoxValidator()
        )
        barcodeProcessor.isActive = true

        val barcodes = listOf(createBarcode(boundingBox, "123456789"))
        barcodeProcessor.process(barcodes, coordinateTransform, scannerBox)

        assertThat(state).isInstanceOf(BarcodeProcessor.State.Recognize::class.java)
    }


    @Test
    fun process_whenNumberOfFramesEnough_shouldPassCommunicateState() {
        var state: BarcodeProcessor.State = BarcodeProcessor.State.Sense
        val barcodeProcessor = BarcodeProcessor(
            onProcessed = { state = it },
            boundingBoxValidator = createBarcodeBoundingBoxValidator()
        )
        barcodeProcessor.isActive = true

        val barcodes = listOf(createBarcode(boundingBox, "123456789"))
        repeat(6) {
            barcodeProcessor.process(barcodes, coordinateTransform, scannerBox)
        }

        assertThat(state).isInstanceOf(BarcodeProcessor.State.Communicate::class.java)
    }


    private fun createBarcode(boundingBox: Rect, rawValue: String): Barcode {
        return Barcode(FakeBarcodeSource(boundingBox, rawValue))
    }

    private fun createBarcodeBoundingBoxValidator(
        isLargeEnough: Boolean = true,
        isInsideScannerBox: Boolean = true
    ): BarcodeBoundingBoxValidator = object : BarcodeBoundingBoxValidator {
        override fun isLargeEnough(scanner: RectF, barcode: RectF) = isLargeEnough
        override fun isInsideScannerBox(scanner: RectF, barcode: RectF) = isInsideScannerBox
    }
}
