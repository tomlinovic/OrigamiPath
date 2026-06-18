package com.example.rmaprojekt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rmaprojekt.pages.AlbumDetailsPage
import com.example.rmaprojekt.pages.CreateAlbumPage
import com.example.rmaprojekt.pages.CreateModelScreen
import com.example.rmaprojekt.pages.EditLocalModelPage
import com.example.rmaprojekt.pages.HomePage
import com.example.rmaprojekt.pages.LocalOrigamiModelStepsPage
import com.example.rmaprojekt.pages.LoginPage
import com.example.rmaprojekt.pages.ModelStepsPage
import com.example.rmaprojekt.pages.ModelsPage
import com.example.rmaprojekt.pages.OrigamiGalleryPage
import com.example.rmaprojekt.pages.OrigamiIdeaPage
import com.example.rmaprojekt.pages.SignupPage
import com.example.rmaprojekt.viewmodel.CreateModelViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, modelsViewModel: ModelsViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("models") { ModelsPage(modifier,navController,authViewModel,modelsViewModel) }
        composable("modelSteps/{modelId}") { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId")!!

            val models by modelsViewModel.models.collectAsState()
            val model = models.first { it.id == modelId }

            ModelStepsPage(
                navController = navController,
                authViewModel = authViewModel,
                modelsViewModel = modelsViewModel,
                model = model
            )
        }

        composable("dailyIdea") {
            OrigamiIdeaPage(modifier, navController, authViewModel)
        }
        composable("gallery") {
            val createAlbumViewModel: CreateAlbumViewModel = viewModel()
            OrigamiGalleryPage(modifier, navController, createAlbumViewModel)
        }
        composable("createAlbum") {
            val createAlbumViewModel: CreateAlbumViewModel = viewModel()
            CreateAlbumPage(navController, createAlbumViewModel)
        }
        composable("albumDetails/{albumId}") { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId")!!
            AlbumDetailsPage(navController, albumId)
        }
        composable("createModel") {
            CreateModelScreen(modifier, navController, authViewModel)
        }
        composable("localModelSteps/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!
            val createModelViewModel: CreateModelViewModel = viewModel()
            val model = createModelViewModel.getMyModels().first { it.id == id }

            LocalOrigamiModelStepsPage(
                navController = navController,
                model = model
            )
        }
        composable("editLocalModel/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!
            val createModelViewModel: CreateModelViewModel = viewModel()
            val model = createModelViewModel.getMyModels().first { it.id == id }

            EditLocalModelPage(
                navController = navController,
                model = model,
                viewModel = createModelViewModel
            )
        }


    })
}