@file:Suppress("BlockingMethodInNonBlockingContext", "BlockingMethodInNonBlockingContext")

package com.aidventory.core.barcode.camera

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, mainExecutor)
        }
    }