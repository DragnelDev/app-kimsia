package com.example.kimsia.domain.usecase

/** XP proporcional a los aciertos: lección perfecta = XP completo; 0 aciertos = 0 XP. */
fun calculateXp(lessonXp: Int, correctAnswers: Int, totalExercises: Int): Int =
    if (totalExercises <= 0) 0 else lessonXp * correctAnswers.coerceIn(0, totalExercises) / totalExercises
