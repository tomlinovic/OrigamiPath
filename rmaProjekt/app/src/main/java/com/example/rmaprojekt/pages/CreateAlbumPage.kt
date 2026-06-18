package com.example.rmaprojekt.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmaprojekt.CreateAlbumViewModel

@Composable
fun CreateAlbumPage(
    navController: NavController,
    viewModel: CreateAlbumViewModel = viewModel()
) {
    var selectedDays by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(7, 14, 21)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Create New Origami Album",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text(if (selectedDays != null) "$selectedDays days" else "Select duration")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                options.forEach { days ->
                    DropdownMenuItem(
                        text = { Text("$days days") },
                        onClick = {
                            selectedDays = days
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (selectedDays != null) {
                    viewModel.createAlbum(selectedDays!!)
                    navController.popBackStack()
                    navController.navigate("gallery")
                }
            },
            enabled = selectedDays != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.Green
            )
        ) {
            Text("Create Album")
        }
    }
}