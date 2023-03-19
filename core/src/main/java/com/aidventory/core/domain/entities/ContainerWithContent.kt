package com.aidventory.core.domain.entities

data class ContainerWithContent(
    val container: Container,
    val content: List<Supply>
)
