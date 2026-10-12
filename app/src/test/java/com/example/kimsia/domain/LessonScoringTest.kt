package com.example.kimsia.domain

import com.example.kimsia.domain.usecase.calculateXp
import org.junit.Assert.assertEquals
import org.junit.Test

class LessonScoringTest {
    @Test fun perfectLesson_givesFullXp() = assertEquals(20, calculateXp(20, 4, 4))
    @Test fun partialLesson_isProportional() = assertEquals(10, calculateXp(20, 2, 4))
    @Test fun noCorrectAnswers_givesZero() = assertEquals(0, calculateXp(20, 0, 4))
    @Test fun emptyLesson_givesZero() = assertEquals(0, calculateXp(20, 0, 0))
    @Test fun correctAboveTotal_isClamped() = assertEquals(20, calculateXp(20, 9, 4))
}
