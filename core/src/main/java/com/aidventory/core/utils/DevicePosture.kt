package com.aidventory.core.utils

import android.graphics.Rect
import androidx.window.layout.FoldingFeature
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * Information about the posture of the device
 */
sealed interface DevicePosture {
    object NormalPosture : DevicePosture

    data class BookPosture(
        val hingePosition: Rect
    ) : DevicePosture

    data class Separating(
        val hingePosition: Rect,
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}

@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}


fun FoldingFeature?.toDevicePosture(): DevicePosture {
    return when {
        isBookPosture(this) -> DevicePosture.BookPosture(hingePosition = this.bounds)
        isSeparating(this) -> DevicePosture.Separating(
            hingePosition = this.bounds,
            orientation = this.orientation
        )
        else -> DevicePosture.NormalPosture
    }
}
