package com.aidventory.core.barcode.processing

import android.graphics.RectF
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform

fun interface CoordinateTransformWrapper {
    fun mapRect(rect: RectF)
}

@TransformExperimental
class CoordinateTransformWrapperImpl(
    private val coordinateTransform: CoordinateTransform
) : CoordinateTransformWrapper {
    override fun mapRect(rect: RectF) {
        coordinateTransform.mapRect(rect)
    }
}