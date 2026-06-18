package com.example.rmaprojekt.pages


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmaprojekt.AuthViewModel
import com.example.rmaprojekt.ModelsViewModel
import com.example.rmaprojekt.models.OrigamiModel
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch


@Composable
fun ModelStepsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, modelsViewModel: ModelsViewModel, model: OrigamiModel,){
    val context = LocalContext.current
    val steps = remember { loadAllSteps(context, model.id) }
    var hasMade by remember { mutableStateOf(false) }
    val userId = authViewModel.userId!!
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }




    LaunchedEffect(model.id) {
        modelsViewModel.hasUserMadeModel(userId, model.id) {
            hasMade = it
            isLoading = false
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(Modifier.height(40.dp))

            Text(
                text = model.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Difficulty: ", fontWeight = FontWeight.Medium)
                DifficultyBadge(model.difficulty)
            }
            Text("Made by: ${model.madeCount} users")

            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            } else {
                if (hasMade) {
                    MadeItBadge()
                } else {
                    Button(
                        onClick = {
                            hasMade = true
                            scope.launch {
                                modelsViewModel.markModelAsMade(userId, model.id)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as made")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Steps:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
        }

        itemsIndexed(steps) { index, resId ->

            Text(
                text = "Step ${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = "Step ${index + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }


            Spacer(Modifier.height(24.dp))
        }
    }
}

fun loadAllSteps(context: Context, modelId: String): List<Int> {
    val steps = mutableListOf<Int>()
    var step = 1

    while (true) {
        val resId = context.resources.getIdentifier(
            "${modelId}_step$step",
            "drawable",
            context.packageName
        )

        if (resId == 0) break

        steps.add(resId)
        step++
    }

    return steps
}
@Composable
fun DifficultyBadge(difficulty: String) {

    val normalized = difficulty.lowercase()

    val bgColor = when (normalized) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFFFF9800)
        "hard" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = difficulty,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MadeItBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF4CAF50))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "You made this model!",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}




