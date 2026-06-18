package com.example.rmaprojekt.models

data class LocalOrigamiModel(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val difficulty: String = "",
    val steps: List<OrigamiStep> = emptyList()
)

data class OrigamiStep(
    val stepNumber: Int,
    val imagePath: String
)