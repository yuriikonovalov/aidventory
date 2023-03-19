package com.aidventory.core.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SupplyFilterParamsTest {

    @Test
    fun takeContainerBarcode_whenBarcodeIsNotTakenYet_shouldAddBarcodeToSet() {
        val params = SupplyFilterParams()
        val barcode = "1234"
        val actualParams = params.takeContainerBarcode(barcode)
        assertThat(actualParams.useFilterContainerBarcodes).isTrue()
        assertThat(actualParams.filterContainerBarcodes).containsExactlyElementsIn(listOf(barcode))
    }

    @Test
    fun takeContainerBarcode_whenBarcodeIsAlreadyTaken_shouldRemoveBarcodeFromSet() {
        val barcode = "1234"
        val otherBarcode = "other_barcode"
        val params = SupplyFilterParams(filterContainerBarcodes = setOf(barcode, otherBarcode))

        val actualParams = params.takeContainerBarcode(barcode)
        assertThat(actualParams.filterContainerBarcodes).doesNotContain(barcode)
        assertThat(actualParams.filterContainerBarcodes)
            .containsExactlyElementsIn(listOf(otherBarcode))
    }

    @Test
    fun takeSupplyUseId_whenIdIsNotTakenYet_shouldAddIdToSet() {
        val params = SupplyFilterParams()
        val id = 1
        val actualParams = params.takeSupplyUseId(id)
        assertThat(actualParams.useFilterSupplyUseIds).isTrue()
        assertThat(actualParams.filterSupplyUseIds).containsExactlyElementsIn(listOf(id))
    }

    @Test
    fun takeSupplyUseId_whenIdIsAlreadyTaken_shouldRemoveIdFromSet() {
        val id = 1
        val otherId = 2
        val params = SupplyFilterParams(filterSupplyUseIds = setOf(id, otherId))

        val actualParams = params.takeSupplyUseId(id)
        assertThat(actualParams.filterSupplyUseIds).doesNotContain(id)
        assertThat(actualParams.filterSupplyUseIds).containsExactlyElementsIn(listOf(otherId))
    }
}