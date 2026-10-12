package com.example.kimsia.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExerciseWithOptions(
    @Embedded val exercise: ExerciseEntity,
    @Relation(parentColumn = "id", entityColumn = "exercise_id")
    val options: List<ExerciseOptionEntity>,
)
