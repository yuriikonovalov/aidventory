package com.aidventory.core.barcode.processing

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.camera.view.transform.OutputTransform
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


class BarcodeAnalyzer(
    private val onSuccess: (List<Barcode>, OutputTransform) -> Unit,
    private val onFailure: (Exception) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val sourceOutputTransform = imageProxy.getCoordinateTransform()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    onSuccess(barcodes, sourceOutputTransform)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun ImageProxy.getCoordinateTransform(): OutputTransform {
        return ImageProxyTransformFactory()
            .apply { this.isUsingRotationDegrees = true }
            .getOutputTransform(this)
    }
}