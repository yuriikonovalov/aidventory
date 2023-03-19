package com.aidventory.core.barcode.camera

import android.annotation.SuppressLint
import android.graphics.RectF
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.camera.view.transform.CoordinateTransform
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aidventory.core.R
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.barcode.processing.BarcodeAnalyzer
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.processing.CoordinateTransformWrapperImpl

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun BarcodeScannerLayout(
    isActive: Boolean,
    isSensing: Boolean,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    modifier: Modifier = Modifier,
    scannerBoxWidth: Dp = 300.dp,
    scannerBoxHeight: Dp = 200.dp,
    scannerBoxCornerRadius: Dp = 16.dp,
    overlayContent: @Composable (toggleTorch: () -> Unit, torchOn: Boolean, scannerBox: RectF) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var torchOn by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var scannerBoxRect by remember { mutableStateOf(RectF()) }
    val barcodeProcessor = remember {
        BarcodeProcessor { state -> onBarcodeProcessorStateChanged(state) }
    }

    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = PreviewView.ScaleType.FILL_CENTER
            // Prevent the view from overlying the parent compose view.
            this.clipToOutline = true
        }
    }

    val preview = Preview.Builder()
        .build()
        .also { it.setSurfaceProvider(previewView.surfaceProvider) }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    val analyzer = BarcodeAnalyzer(
        onFailure = { barcodeProcessor.failure() },
        onSuccess = { barcodes, source ->
            val target = previewView.outputTransform!!
            val coordinateTransform = CoordinateTransformWrapperImpl(
                CoordinateTransform(source, target)
            )
            barcodeProcessor.process(barcodes, coordinateTransform, scannerBoxRect)
        }
    )

    imageAnalysis.setAnalyzer(context.mainExecutor, analyzer)

    LaunchedEffect(isActive) {
        // Stop the barcode processor together with the camera.
        barcodeProcessor.isActive = isActive

        val cameraProvider = context.getCameraProvider()
        if (isActive) {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } else {
            imageAnalysis.clearAnalyzer() // stop data from streaming to the ImageAnalyzer.
            cameraProvider.unbindAll()
        }
    }

    Box(modifier = modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        ScannerBoxOverlay(
            modifier = Modifier.fillMaxSize(),
            scannerBoxWidth = scannerBoxWidth,
            scannerBoxHeight = scannerBoxHeight,
            cornerRadius = scannerBoxCornerRadius,
            isSensing = isSensing,
            onRectAvailable = { scannerBoxRect = it }
        )

        overlayContent(
            torchOn = torchOn,
            toggleTorch = {
                camera?.cameraControl?.enableTorch(!torchOn)
                torchOn = !torchOn
            },
            scannerBox = scannerBoxRect
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (torchOn) {
                camera?.cameraControl?.enableTorch(false)
                torchOn = !torchOn
            }
        }
    }
}

fun Modifier.barcodeScannerLayoutModifier(widthSizeClass: WindowWidthSizeClass): Modifier =
    composed {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> this
            else -> this
                .systemBarsPadding()
                .padding(16.dp)
                .clip(
                    MaterialTheme.shapes.large.copy(
                        topStart = ZeroCornerSize,
                        topEnd = ZeroCornerSize
                    )
                )
        }
    }

@Composable
fun Boolean.torchToggleIcon() = if (this) {
    AidventoryIcons.FlashOff.imageVector
} else {
    AidventoryIcons.FlashOn.imageVector
}

@Composable
fun Boolean.torchToggleContentDescription() = if (this) {
    stringResource(R.string.torch_toggle_on_content_description)
} else {
    stringResource(R.string.torch_toggle_off_content_description)
}

@Composable
fun TorchToggleButton(
    modifier: Modifier = Modifier,
    isTorchOn: Boolean,
    onClick: () -> Unit,
) {
    OutlinedIconToggleButton(
        modifier = modifier,
        checked = isTorchOn,
        onCheckedChange = { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = IconButtonDefaults.outlinedIconToggleButtonColors(contentColor = Color.White),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Icon(
            imageVector = isTorchOn.torchToggleIcon(),
            contentDescription = isTorchOn.torchToggleContentDescription()
        )
    }
}


@Composable
private fun ScannerBoxOverlay(
    scannerBoxWidth: Dp,
    scannerBoxHeight: Dp,
    cornerRadius: Dp,
    isSensing: Boolean,
    modifier: Modifier = Modifier,
    onRectAvailable: (RectF) -> Unit = {},
) {
    // Convert Dp size to pixels.
    val scannerBoxWidthInPx: Float
    val scannerBoxHeightInPx: Float
    val cornerRadiusInPx: Float
    with(LocalDensity.current) {
        scannerBoxWidthInPx = scannerBoxWidth.toPx()
        scannerBoxHeightInPx = scannerBoxHeight.toPx()
        cornerRadiusInPx = cornerRadius.toPx()
    }

    val infiniteTransitionAnimation = rememberInfiniteTransition()
    val recognizingAnimation by infiniteTransitionAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val scannerBoxOffset = Offset(
            x = (size.width - scannerBoxWidthInPx) / 2,
            y = size.height / 2 - (scannerBoxHeightInPx / 2)
        )
        val scannerBoxSize = Size(scannerBoxWidthInPx, scannerBoxHeightInPx)
        val scannerRect = Rect(scannerBoxOffset, scannerBoxSize)
        // Pass the clip RectF up as a callback.
        // It's used by BarcodeProcessor object during checking
        // if the bounding box of a Barcode is inside the scanner box.
        onRectAvailable(
            RectF(
                scannerRect.left,
                scannerRect.top,
                scannerRect.right,
                scannerRect.bottom
            )
        )

        // Scrim
        drawRect(Color(0x99000000))
        // Scanner box
        drawRoundRect(
            topLeft = scannerBoxOffset,
            size = scannerBoxSize,
            cornerRadius = CornerRadius(cornerRadiusInPx, cornerRadiusInPx),
            color = Color.Transparent,
            blendMode = BlendMode.Clear
        )

        if (isSensing) {
            // Draw the animated path.
            scale(recognizingAnimation * 0.2f + 1f) {
                drawPath(
                    path = scannerRect.toScannerBoxBorderPath(),
                    color = Color.White,
                    alpha = 1f - recognizingAnimation,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(cornerRadiusInPx)
                    )
                )
            }
        }
    }
}


private fun Rect.toScannerBoxBorderPath(): Path {
    return Path().apply {
        moveTo(left + width / 2, top)
        lineTo(right, top)
        lineTo(right, bottom)
        lineTo(left, bottom)
        lineTo(left, top)
        lineTo(left + width / 2, top)
        close()
    }
}