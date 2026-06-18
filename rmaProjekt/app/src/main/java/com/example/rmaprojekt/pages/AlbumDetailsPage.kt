package com.example.rmaprojekt.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.rmaprojekt.CreateAlbumViewModel
import com.example.rmaprojekt.models.OrigamiAlbum
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.core.content.FileProvider
import java.io.File
import java.time.temporal.ChronoUnit
@Composable
fun AlbumDetailsPage(
    navController: NavController,
    albumId: String,
    viewModel: CreateAlbumViewModel = viewModel()
) {
    val context = LocalContext.current
    var album by remember { mutableStateOf<OrigamiAlbum?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        album = viewModel.getAlbums().find { it.id == albumId }
    }

    if (album == null) {
        Text("Loading...", fontSize = 22.sp)
        return
    }

    val today = LocalDate.now()
    val startDate = Instant.ofEpochMilli(album!!.startDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val totalDays = album!!.durationDays

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = viewModel.saveImageToInternalStorage(context, uri)
            viewModel.addOrReplaceTodayPhoto(albumId, savedPath)
            album = viewModel.getAlbums().find { it.id == albumId }
        }
    }

    val cameraPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            val savedPath = viewModel.saveImageToInternalStorage(context, cameraImageUri!!)
            viewModel.addOrReplaceTodayPhoto(albumId, savedPath)
            album = viewModel.getAlbums().find { it.id == albumId }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Add photo") },
            text = { Text("Choose photo source") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val tempFile = File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        tempFile
                    )
                    cameraImageUri = uri
                    cameraPicker.launch(uri)
                }) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryPicker.launch("image/*")
                }) {
                    Text("Gallery")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Album Details",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items((0 until totalDays).toList()) { dayIndex ->

                val dayDate = startDate.plusDays(dayIndex.toLong())
                val photo = album!!.photos.find { it.dayIndex == dayIndex }
                val isToday = dayDate == today
                val isPast = dayDate < today

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(enabled = isToday) {
                            if (isToday) showImageSourceDialog = true
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isToday -> Color(0xFFBBDEFB)
                            isPast -> Color(0xFFE0E0E0)
                            else -> Color(0xFFFFFFFF)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Day ${dayIndex + 1} — $dayDate",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (photo != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photo.imagePath)
                                        .size(Size.ORIGINAL)
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(
                                text = if (isToday) "Tap to add today's photo"
                                else if (isPast) "No photo added"
                                else "Not available yet",
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}