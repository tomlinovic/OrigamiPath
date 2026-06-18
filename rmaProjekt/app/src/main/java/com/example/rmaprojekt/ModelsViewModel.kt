package com.example.rmaprojekt

import androidx.lifecycle.ViewModel
import com.example.rmaprojekt.models.OrigamiModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ModelsViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _models = MutableStateFlow<List<OrigamiModel>>(emptyList())
    val models = _models.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        db.collection("models")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val list = snapshot.documents.map { doc ->
                    OrigamiModel(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        madeCount = doc.getLong("madeCount")?.toInt() ?: 0,
                        difficulty = doc.getString("difficulty")?:"Unknown",
                    )
                }

                _models.value = list
            }
    }

    fun markModelAsMade(userId: String, modelId: String) {
        val userDoc = db.collection("userModels").document(userId)
        val modelDoc = db.collection("models").document(modelId)

        db.runTransaction { transaction ->

            val userSnapshot = transaction.get(userDoc)
            val alreadyMade = userSnapshot.getBoolean(modelId) ?: false

            if (alreadyMade) {
                return@runTransaction null
            }

            val modelSnapshot = transaction.get(modelDoc)
            val currentCount = modelSnapshot.getLong("madeCount") ?: 0

            transaction.update(modelDoc, "madeCount", currentCount + 1)

            transaction.set(
                userDoc,
                mapOf(modelId to true),
                com.google.firebase.firestore.SetOptions.merge()
            )

            null
        }
    }





    fun hasUserMadeModel(userId: String, modelId: String, onResult: (Boolean) -> Unit) {
        db.collection("userModels")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val done = doc.getBoolean(modelId) ?: false
                onResult(done)
            }
    }

}