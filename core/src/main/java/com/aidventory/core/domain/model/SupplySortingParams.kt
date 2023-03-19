package com.aidventory.core.domain.model

sealed class SupplySortingParams private constructor(
    val nameASC: Boolean = false,
    val nameDESC: Boolean = false,
    val expiryASC: Boolean = false,
    val expiryDESC: Boolean = false
) {
    val isDefault: Boolean = !(nameASC || nameDESC || expiryASC || expiryDESC)

    object NameASC : SupplySortingParams(nameASC = true)
    object NameDESC : SupplySortingParams(nameDESC = true)
    object ExpiryASC : SupplySortingParams(expiryASC = true)
    object ExpiryDESC : SupplySortingParams(expiryDESC = true)
    object Default : SupplySortingParams()
}