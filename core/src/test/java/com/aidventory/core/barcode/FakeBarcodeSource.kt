package com.aidventory.core.barcode

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.common.internal.BarcodeSource


class FakeBarcodeSource(
    private val boundingBox: Rect,
    private val rawValue: String = "rawValue"
) : BarcodeSource {
    override fun getFormat(): Int {
        return Barcode.FORMAT_ALL_FORMATS
    }

    override fun getValueType(): Int {
        return Barcode.TYPE_TEXT
    }

    override fun getBoundingBox(): Rect {
        return boundingBox
    }

    override fun getCalendarEvent(): Barcode.CalendarEvent? {
        return null
    }

    override fun getContactInfo(): Barcode.ContactInfo? {
        return null
    }

    override fun getDriverLicense(): Barcode.DriverLicense? {
        return null
    }

    override fun getEmail(): Barcode.Email? {
        return null
    }

    override fun getGeoPoint(): Barcode.GeoPoint? {
        return null
    }

    override fun getPhone(): Barcode.Phone? {
        return null
    }

    override fun getSms(): Barcode.Sms? {
        return null
    }

    override fun getUrl(): Barcode.UrlBookmark? {
        return null
    }

    override fun getWifi(): Barcode.WiFi? {
        return null
    }

    override fun getDisplayValue(): String? {
        return null
    }

    override fun getRawValue(): String {
        return rawValue
    }

    override fun getRawBytes(): ByteArray? {
        return null
    }

    override fun getCornerPoints(): Array<Point>? {
        return null
    }

}