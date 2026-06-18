package com.example.rmaprojekt.models

import java.util.UUID

data class OrigamiAlbum(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val durationDays: Int = 7,
    val photos: List<AlbumPhoto> = emptyList()
)
