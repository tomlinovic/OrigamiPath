package com.example.rmaprojekt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rmaprojekt.models.LocalOrigamiModel
import com.example.rmaprojekt.models.OrigamiStep
import com.example.rmaprojekt.storage.ModelStorage
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CreateModelViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun saveModel(
        name: String,
        difficulty: String,
        steps: List<OrigamiStep>
    ) {

        val user = FirebaseAuth
            .getInstance()
            .currentUser ?: return

        val model = LocalOrigamiModel(
            id = UUID.randomUUID().toString(),
            ownerId = user.uid,
            name = name,
            difficulty = difficulty,
            steps = steps
        )

        ModelStorage.saveModel(
            getApplication(),
            model
        )
    }

    fun getMyModels(): List<LocalOrigamiModel> {

        val userId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid ?: return emptyList()

        return ModelStorage
            .loadModels(getApplication())
            .filter {
                it.ownerId == userId
            }
    }

    fun deleteModel(modelId: String) {
        val context = getApplication<Application>()

        val allModels = ModelStorage.loadModels(context).toMutableList()

        val updated = allModels.filterNot { it.id == modelId }

        ModelStorage.saveModels(context, updated)
    }

    fun updateModel(updated: LocalOrigamiModel) {
        val context = getApplication<Application>()
        val all = ModelStorage.loadModels(context).toMutableList()

        val index = all.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            all[index] = updated
            ModelStorage.saveModels(context, all)
        }
    }


}