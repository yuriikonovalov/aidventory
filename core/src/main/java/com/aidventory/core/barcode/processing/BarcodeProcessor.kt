package com.aidventory.core.barcode.processing

import android.graphics.RectF
import androidx.core.graphics.toRectF
import com.aidventory.core.barcode.processing.BarcodeProcessor.Config.Companion.MIN_TARGET_NUMBER_OF_FRAMES
import com.google.mlkit.vision.barcode.common.Barcode


class BarcodeProcessor(
    private val config: Config = Config(),
    private val boundingBoxValidator: BarcodeBoundingBoxValidator = BarcodeBoundingBoxValidatorImpl,
    private val onProcessed: (State) -> Unit
) {
    // Keeps a number of the frames of the same barcode.
    // It's used for making a decision whether move to State.Communicate or not.
    private var currentNumberOfCapturedFrames = 0
    private var previousBarcodeValue: String? = null

    /**
     * If false, then any barcodes that passed to the process method will be skipped.
     *
     * During switching the current number of captured frames sets to 0.
     */
    var isActive: Boolean = true
        set(value) {
            if (value != field) {
                // reset the current number of frames during activating and deactivating.
                currentNumberOfCapturedFrames = 0
                field = value
            }
        }

    fun process(
        barcodes: List<Barcode>,
        coordinateTransform: CoordinateTransformWrapper,
        scanner: RectF
    ) {
        if (!isActive) return
        val barcodesInScannerBox = barcodes.filterBarcodesInScannerBox(scanner, coordinateTransform)
        val single = barcodesInScannerBox.size == 1
        val contains =
            single && barcodesInScannerBox.first().isLargeEnough(scanner, coordinateTransform)
        val barcodeEligible = contains && barcodesInScannerBox.first().isEligible()

        when {
            barcodeEligible && currentNumberOfCapturedFrames >= config.targetNumberOfFrames -> {
                communicate(barcodesInScannerBox.first())
            }

            barcodeEligible && currentNumberOfCapturedFrames < config.targetNumberOfFrames -> {
                recognize(barcodesInScannerBox.first())
            }

            else -> sense()
        }
    }

    private fun sense() {
        // Reset the captured number of frames when after user stops scanning a barcode.
        currentNumberOfCapturedFrames = 0
        previousBarcodeValue = null
        onProcessed(State.Sense)
    }

    private fun recognize(barcode: Barcode) {
        // Increase the number of frames until it's sufficient to move to State.Communicating.
        currentNumberOfCapturedFrames++
        previousBarcodeValue = barcode.rawValue
        onProcessed(State.Recognize(currentNumberOfCapturedFrames, config.targetNumberOfFrames))
    }

    private fun communicate(barcode: Barcode) {
        onProcessed(State.Communicate(barcode))
    }

    private fun Barcode.isEligible(): Boolean {
        // A barcode is considered to be eligible if its raw value is the same as previous one,
        // or it's the first barcode that it placed in the scanner box.
        return currentNumberOfCapturedFrames == 0 || rawValue == previousBarcodeValue
    }

    private fun Barcode.isLargeEnough(
        scannerBox: RectF,
        coordinateTransform: CoordinateTransformWrapper
    ): Boolean {
        val boundingBox = getBoundingBoxWithCoordinateTransform(coordinateTransform)
        return boundingBoxValidator.isLargeEnough(scannerBox, boundingBox)
    }

    private fun List<Barcode>.filterBarcodesInScannerBox(
        scannerBox: RectF,
        coordinateTransform: CoordinateTransformWrapper
    ): List<Barcode> {
        return this.filter { barcode ->
            val boundingBox = barcode.getBoundingBoxWithCoordinateTransform(coordinateTransform)
            boundingBoxValidator.isInsideScannerBox(scannerBox, boundingBox)
        }
    }

    private fun Barcode.getBoundingBoxWithCoordinateTransform(coordinateTransform: CoordinateTransformWrapper): RectF {
        val boundingBox = boundingBox!!.toRectF()
        coordinateTransform.mapRect(boundingBox)
        return boundingBox
    }

    fun failure() {
        // Just pass State.Sense in case of any failure.
        onProcessed(State.Sense)
    }

    sealed interface State {
        /**
         * Looking for input.
         */
        object Sense : State

        /**
         * Detecting a barcode.
         */
        class Recognize(currentNumberOfFrames: Int, targetNumberOfFrames: Int) : State {
            /**
             * The progress value of the barcode capturing.
             * The value is between 0F to 1F.
             */
            val progress: Float = currentNumberOfFrames.toFloat() / targetNumberOfFrames.toFloat()
        }

        /**
         * Displaying barcode results.
         */
        data class Communicate(val barcode: Barcode) : State
    }

    /**
     * Configuration for [BarcodeProcessor].
     *
     * @param targetNumberOfFrames a minimum number of frames containing the same barcode
     * that [BarcodeProcessor] needs to capture in order to move from [State.Recognize] to [State.Communicate].
     * The parameter is coerced to be at least [MIN_TARGET_NUMBER_OF_FRAMES].
     */
    class Config(targetNumberOfFrames: Int = MIN_TARGET_NUMBER_OF_FRAMES) {
        val targetNumberOfFrames: Int = targetNumberOfFrames
            .coerceAtLeast(MIN_TARGET_NUMBER_OF_FRAMES)

        companion object {
            private const val MIN_TARGET_NUMBER_OF_FRAMES = 5
        }
    }
}
