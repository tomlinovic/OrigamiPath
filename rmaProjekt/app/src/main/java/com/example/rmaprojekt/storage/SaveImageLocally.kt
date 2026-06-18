package com.example.rmaprojekt.storage

import android.content.Context
import android.net.Uri
import java.io.File

fun saveImageLocally(
    context: Context,
    uri: Uri
): String {

    val inputStream = context.contentResolver.openInputStream(uri)

    val file = File(
        context.filesDir,
        "img_${System.currentTimeMillis()}.jpg"
    )

    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return file.absolutePath
}