package com.example.rmaprojekt.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmaprojekt.AuthViewModel
import com.example.rmaprojekt.ModelsViewModel
import com.example.rmaprojekt.models.LocalOrigamiModel
import com.example.rmaprojekt.models.OrigamiModel
import com.example.rmaprojekt.viewmodel.CreateModelViewModel

@Composable
fun ModelsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ModelsViewModel = viewModel()
) {
    val models by viewModel.models.collectAsState()
    val createModelViewModel: CreateModelViewModel = viewModel()
    val myModels = remember { mutableStateListOf<LocalOrigamiModel>() }

    LaunchedEffect(Unit) {
        myModels.clear()
        myModels.addAll(createModelViewModel.getMyModels())
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(40.dp))
        Text("Origami Models", fontSize = 28.sp, modifier = Modifier.padding(16.dp))

        LazyColumn {
            items(models) { model ->
                ModelItem(model) {
                    navController.navigate("modelSteps/${model.id}")
                }
            }

            item {
                if (myModels.isNotEmpty()) {
                    Text(
                        text = "Your models",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            items(myModels) { myModel ->
                MyModelItem(
                    model = myModel,
                    onClick = { navController.navigate("localModelSteps/${myModel.id}") },
                    onDelete = {
                        createModelViewModel.deleteModel(myModel.id)
                        myModels.remove(myModel)
                    }
                )
            }

            item {
                AddNewModelItem {
                    navController.navigate("createModel")
                }
            }
        }
    }
}

@Composable
fun ModelItem(model: OrigamiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(model.name, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text("Made by ${model.madeCount} users")
            Spacer(Modifier.height(4.dp))
            Text("Difficulty: ${model.difficulty}")
        }
    }
}

@Composable
fun AddNewModelItem(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "+", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = "Create new model", fontSize = 18.sp)
        }
    }
}

@Composable
fun MyModelItem(
    model: LocalOrigamiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Delete Model") },
            text = { Text("Are you sure you want to delete this model?") },
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(model.name, fontSize = 22.sp)
                Spacer(Modifier.height(4.dp))
                Text("Difficulty: ${model.difficulty}")
                Spacer(Modifier.height(4.dp))
                Text("Steps: ${model.steps.size}")
            }

            Button(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Delete")
            }
        }
    }
}