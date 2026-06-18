package com.example.rmaprojekt.pages

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rmaprojekt.AuthViewModel
import com.example.rmaprojekt.models.OrigamiStep
import com.example.rmaprojekt.storage.saveImageLocally
import com.example.rmaprojekt.viewmodel.CreateModelViewModel
import java.io.File

@Composable
fun CreateModelScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current

    val viewModel: CreateModelViewModel = viewModel()

    var modelName by remember {
        mutableStateOf("")
    }

    var difficulty by remember {
        mutableStateOf("Easy")
    }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val steps = remember {
        mutableStateListOf<OrigamiStep>()
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && cameraImageUri != null) {
                val savedPath = saveImageLocally(context, cameraImageUri!!)
                steps.add(
                    OrigamiStep(
                        stepNumber = steps.size + 1,
                        imagePath = savedPath
                    )
                )
            }
        }

    val difficulties = listOf(
        "Easy",
        "Medium",
        "Hard"
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                val imagePath =
                    saveImageLocally(
                        context,
                        it
                    )

                steps.add(
                    OrigamiStep(
                        stepNumber = steps.size + 1,
                        imagePath = imagePath
                    )
                )
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Create model",
            fontSize = 28.sp
        )

        Column(
            modifier = Modifier
                .background(Color.DarkGray, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = modelName,
                onValueChange = {
                    modelName = it
                },
                label = {
                    Text("Model name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = difficulty,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Difficulty") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    difficulties.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                difficulty = item
                                expanded = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = difficultyColor(item),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val uri = createTempImageUri(context)
                    cameraImageUri = uri
                    cameraLauncher.launch(uri)
                }
            ) {
                Text("Add a camera step")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                }
            ) {
                Text("Add step from gallery")
            }
        }





        Text(
            text = "Steps: ${steps.size}"
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(steps) { step ->

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {

                    Text(
                        text = "Step ${step.stepNumber}"
                    )

                    AsyncImage(
                        model = step.imagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                if (
                    modelName.isNotBlank()
                    && steps.isNotEmpty()
                ) {

                    viewModel.saveModel(
                        name = modelName,
                        difficulty = difficulty,
                        steps = steps.toList()
                    )

                    navController.popBackStack()
                }
            }
        ) {
            Text("Save model")
        }
    }
}

fun createTempImageUri(context: Context): Uri {
    val file = File.createTempFile("camera_image_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

fun difficultyColor(value: String): Color {
    return when (value.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFFFF9800)
        "hard" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}
