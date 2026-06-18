package com.example.rmaprojekt.storage

import android.content.Context
import com.example.rmaprojekt.models.LocalOrigamiModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ModelStorage {

    private const val FILE_NAME = "origami_models.json"

    fun saveModel(
        context: Context,
        model: LocalOrigamiModel
    ) {

        val models = loadModels(context).toMutableList()

        models.add(model)

        val json = Gson().toJson(models)

        File(
            context.filesDir,
            FILE_NAME
        ).writeText(json)
    }

    fun loadModels(
        context: Context
    ): List<LocalOrigamiModel> {

        val file = File(
            context.filesDir,
            FILE_NAME
        )

        if (!file.exists()) {
            return emptyList()
        }

        val json = file.readText()

        val type = object : TypeToken<List<LocalOrigamiModel>>() {}.type

        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun saveModels(
        context: Context,
        models: List<LocalOrigamiModel>
    ) {
        val json = Gson().toJson(models)
        File(context.filesDir, FILE_NAME).writeText(json)
    }
}