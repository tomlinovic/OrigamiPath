package com.example.rmaprojekt.storage

import android.content.Context
import com.example.rmaprojekt.models.OrigamiAlbum
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object AlbumStorage {

    private const val FILE_NAME = "origami_albums.json"

    fun loadAlbums(context: Context, userId: String): List<OrigamiAlbum> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        val json = file.readText()
        val type = object : TypeToken<List<OrigamiAlbum>>() {}.type
        val all = Gson().fromJson<List<OrigamiAlbum>>(json, type) ?: emptyList()

        return all.filter { it.ownerId == userId }
    }


    fun saveAlbums(context: Context, albums: List<OrigamiAlbum>, userId: String) {
        val file = File(context.filesDir, FILE_NAME)

        val existing = if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<OrigamiAlbum>>() {}.type
            Gson().fromJson<List<OrigamiAlbum>>(json, type) ?: emptyList()
        } else emptyList()

        val otherUsersAlbums = existing.filter { it.ownerId != userId }
        val merged = otherUsersAlbums + albums

        File(context.filesDir, FILE_NAME).writeText(Gson().toJson(merged))
    }
}
