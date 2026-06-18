package com.example.rmaprojekt

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.ListenableWorker
import com.example.rmaprojekt.models.AlbumPhoto
import com.example.rmaprojekt.models.OrigamiAlbum
import com.example.rmaprojekt.storage.AlbumStorage
import com.example.rmaprojekt.storage.ModelStorage
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.Instant

class CreateAlbumViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun getAlbums(): List<OrigamiAlbum> {
        val context = getApplication<Application>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        return AlbumStorage.loadAlbums(context,userId)
    }


    fun createAlbum(durationDays: Int) {
        val context = getApplication<Application>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val newAlbum = OrigamiAlbum(ownerId = userId, durationDays = durationDays)
        val albums = AlbumStorage.loadAlbums(context, userId).toMutableList()
        albums.add(newAlbum)
        AlbumStorage.saveAlbums(context, albums, userId)
    }

    fun addOrReplaceTodayPhoto(albumId: String, imagePath: String) {
        val context = getApplication<Application>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val albums = AlbumStorage.loadAlbums(context, userId).toMutableList()

        val index = albums.indexOfFirst { it.id == albumId }
        if (index == -1) return

        val album = albums[index]
        val today = LocalDate.now()
        val start = Instant.ofEpochMilli(album.startDate)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        val dayIndex = ChronoUnit.DAYS.between(start, today).toInt()

        if (dayIndex < 0 || dayIndex >= album.durationDays) return

        val updatedPhotos = album.photos.toMutableList()
        val existing = updatedPhotos.indexOfFirst { it.dayIndex == dayIndex }
        if (existing != -1) {
            updatedPhotos[existing] = AlbumPhoto(dayIndex, System.currentTimeMillis(), imagePath)
        } else {
            updatedPhotos.add(AlbumPhoto(dayIndex, System.currentTimeMillis(), imagePath))
        }

        albums[index] = album.copy(photos = updatedPhotos)
        AlbumStorage.saveAlbums(context, albums, userId)
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
        val fileName = "origami_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file.absolutePath
    }

    fun deleteAlbum(albumId: String) {
        val context = getApplication<Application>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val albums = AlbumStorage.loadAlbums(context, userId).toMutableList()
        albums.removeIf { it.id == albumId }
        AlbumStorage.saveAlbums(context, albums, userId)
    }


}