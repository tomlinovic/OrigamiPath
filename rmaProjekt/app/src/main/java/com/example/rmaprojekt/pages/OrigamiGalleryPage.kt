package com.example.rmaprojekt.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmaprojekt.CreateAlbumViewModel
import com.example.rmaprojekt.models.OrigamiAlbum
import java.time.Instant
import java.time.ZoneId

@Composable
fun OrigamiGalleryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CreateAlbumViewModel = viewModel()
) {
    val albums = remember { mutableStateListOf<OrigamiAlbum>() }

    LaunchedEffect(Unit) {
        albums.clear()
        albums.addAll(viewModel.getAlbums())
    }

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(40.dp))

        Text("Origami Gallery", fontSize = 28.sp, modifier = Modifier.padding(16.dp))

        Button(
            onClick = { navController.navigate("createAlbum") },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        ) {
            Text("Create new album")
        }

        LazyColumn {
            items(albums) { album ->
                AlbumItem(
                    album = album,
                    onClick = { navController.navigate("albumDetails/${album.id}") },
                    onDelete = {
                        viewModel.deleteAlbum(album.id)
                        albums.remove(album)
                    }
                )
            }
        }
    }
}

@Composable
fun AlbumItem(
    album: OrigamiAlbum,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Delete Album") },
            text = { Text("Are you sure you want to delete this album?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Album (${album.durationDays} days)", fontSize = 20.sp)

                Spacer(Modifier.height(6.dp))

                val startDate = Instant.ofEpochMilli(album.startDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                Text("Started: $startDate")

                Spacer(Modifier.height(6.dp))

                Text("Photos: ${album.photos.size}/${album.durationDays}")
            }

            Button(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Delete")
            }
        }
    }
}
@Composable
fun AlbumItem(
    album: OrigamiAlbum,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Album (${album.durationDays} days)",
                fontSize = 20.sp
            )

            Spacer(Modifier.height(6.dp))

            val startDate = Instant.ofEpochMilli(album.startDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            Text("Started: $startDate")

            Spacer(Modifier.height(6.dp))

            Text("Photos: ${album.photos.size}/${album.durationDays}")
        }
    }
}
