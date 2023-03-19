package com.aidventory.feature.containers.presentation.containers

import com.aidventory.core.domain.entities.ContainerWithContent

internal data class ContainersUiState(
    val isLoading: Boolean = true,
    val containersWithContent: List<ContainerWithContent> = emptyList(),
    val selectedContainerBarcode: String? = null,
    val isDetailOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isShareQrErrorDialogOpen: Boolean = false
) {
    val selectedContainer: ContainerWithContent? = selectedContainerBarcode?.let { barcode ->
        containersWithContent.find { containerWithContent ->
            containerWithContent.container.barcode == barcode
        }
    }
}