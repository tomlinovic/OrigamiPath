package com.example.rmaprojekt.pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rmaprojekt.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Composable
fun OrigamiIdeaPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    val context = LocalContext.current

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun loadRandomOrigamiImage() {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("https://api.unsplash.com/photos/random?query=origami_idea")
                    .addHeader("Authorization", "Client-ID 0xaq5FpwHPCDYXEZ47PwehcLvZhGR8rtPsFVdxQ3XRg")
                    .addHeader("Accept-Version", "v1")
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code}")
                }

                val body = response.body?.string()
                    ?: throw Exception("Empty response")

                val json = JSONObject(body)
                val urls = json.getJSONObject("urls")
                val regularUrl = urls.getString("regular")

                withContext(Dispatchers.Main) {
                    imageUrl = regularUrl
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadRandomOrigamiImage()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Daily Origami Idea", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 3.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Random origami",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading...")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { loadRandomOrigamiImage() },
            enabled = !isLoading,
            border = BorderStroke(2.dp, Color.DarkGray),
        ) {
            Text("New idea")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.popBackStack() },
            border = BorderStroke(2.dp, Color.DarkGray),
        ) {
            Text("Back")
        }
    }
}