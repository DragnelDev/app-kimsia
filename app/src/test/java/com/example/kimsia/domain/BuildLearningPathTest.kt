package com.example.kimsia.domain

import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.NodeStatus.COMPLETED
import com.example.kimsia.domain.model.NodeStatus.CURRENT
import com.example.kimsia.domain.model.NodeStatus.LOCKED
import com.example.kimsia.domain.usecase.buildLearningPath
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildLearningPathTest {
    private val language = Language(3, "gn", "Guaraní", null, true)
    private val unit = LearningUnit(3, 3, "Saludos y Presentaciones", "", 1)
    private val lessons = (1..3).map { Lesson(it.toLong(), 3, "Lección $it", it, 20) }

    private fun statuses(done: Set<Long>) = buildLearningPath(language, unit, lessons, done).nodes.map { it.status }

    @Test
    fun withoutProgress_firstLessonIsCurrent() =
        assertEquals(listOf(CURRENT, LOCKED, LOCKED, LOCKED), statuses(emptySet()))

    @Test
    fun firstDone_secondIsCurrent() =
        assertEquals(listOf(COMPLETED, CURRENT, LOCKED, LOCKED), statuses(setOf(1)))

    @Test
    fun allDone_chestUnlocks() =
        assertEquals(listOf(COMPLETED, COMPLETED, COMPLETED, CURRENT), statuses(setOf(1, 2, 3)))
}
