package com.example.rmaprojekt.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rmaprojekt.models.LocalOrigamiModel
import com.example.rmaprojekt.models.OrigamiStep
import com.example.rmaprojekt.storage.saveImageLocally
import com.example.rmaprojekt.viewmodel.CreateModelViewModel
import java.io.File


@Composable
fun EditLocalModelPage(
    navController: NavController,
    model: LocalOrigamiModel,
    viewModel: CreateModelViewModel
) {

    var name by remember {
        mutableStateOf(model.name)
    }

    var difficulty by remember {
        mutableStateOf(model.difficulty)
    }


    val steps = remember {
        mutableStateListOf<OrigamiStep>()
            .apply {
                addAll(model.steps)
            }
    }


    val context = LocalContext.current


    var editingStepIndex by remember {
        mutableStateOf<Int?>(null)
    }


    var cameraImageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    var showImageOptions by remember {
        mutableStateOf(false)
    }


    var expanded by remember {
        mutableStateOf(false)
    }


    val difficulties = listOf(
        "Easy",
        "Medium",
        "Hard"
    )

    val galleryPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val path =
                    saveImageLocally(
                        context,
                        it
                    )
                editingStepIndex?.let { index ->
                    steps[index] =
                        steps[index].copy(
                            imagePath = path
                        )
                }
                editingStepIndex = null
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if(success) {
                cameraImageUri?.let { uri ->
                    val path =
                        saveImageLocally(
                            context,
                            uri
                        )
                    editingStepIndex?.let { index ->
                        steps[index] =
                            steps[index].copy(
                                imagePath = path
                            )
                    }
                }

            }
            editingStepIndex = null
        }

    fun openCamera() {
        val file =
            File(
                context.cacheDir,
                "camera_${System.currentTimeMillis()}.jpg"
            )
        val uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        cameraImageUri = uri
        cameraLauncher.launch(uri)
    }
    fun renumberSteps() {
        steps.forEachIndexed { index, step ->
            steps[index] =
                step.copy(
                    stepNumber = index + 1
                )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(
            Modifier.height(40.dp)
        )
        Column(
            modifier = Modifier
                .background(
                    Color.DarkGray,
                    RoundedCornerShape(16.dp)
                )
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit model",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                Modifier.height(16.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Model name")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                Modifier.height(16.dp)
            )

            Box {
                OutlinedTextField(
                    value = difficulty,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Difficulty")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                expanded = !expanded
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if(expanded)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expanded = true
                        }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
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
                                .background(
                                    difficultyColor(item),
                                    RoundedCornerShape(6.dp)
                                )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Steps:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(steps) { step ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Step ${step.stepNumber}"
                    )
                    if(step == steps.first()) {
                        Button(
                            onClick = {
                                steps.add(
                                    0,
                                    OrigamiStep(
                                        stepNumber = 0,
                                        imagePath =
                                            "android.resource://com.example.rmaprojekt/drawable/placeholder_step"
                                    )
                                )
                                renumberSteps()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Add step before"
                            )
                        }
                    }
                    Spacer(
                        Modifier.height(8.dp)
                    )
                    AsyncImage(
                        model = step.imagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row {
                        Button(
                            onClick = {
                                editingStepIndex =
                                    steps.indexOf(step)
                                showImageOptions = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Replace")
                        }

                        Button(
                            onClick = {
                                val index =
                                    steps.indexOf(step)
                                steps.add(
                                    index + 1,
                                    OrigamiStep(
                                        stepNumber = 0,
                                        imagePath =
                                            "android.resource://com.example.rmaprojekt/drawable/placeholder_step"
                                    )
                                )
                                renumberSteps()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Add step after")
                        }
                        Button(
                            onClick = {
                                steps.remove(step)
                                renumberSteps()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

        if(showImageOptions) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = {
                    showImageOptions = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Gallery")
                    },
                    onClick = {
                        showImageOptions = false
                        galleryPicker.launch(
                            "image/*"
                        )
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Camera")
                    },
                    onClick = {
                        showImageOptions = false
                        openCamera()
                    }
                )
            }
        }

        Button(
            onClick = {
                val updated = model.copy(
                    name = name,
                    difficulty = difficulty,
                    steps = steps.toList()
                )
                viewModel.updateModel(updated)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.Green
            )
        ) {
            Text("Save changes")
        }
    }
}