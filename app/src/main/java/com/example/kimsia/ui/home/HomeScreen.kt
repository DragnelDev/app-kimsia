package com.example.kimsia.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningPath
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.NodeStatus
import com.example.kimsia.domain.model.NodeType
import com.example.kimsia.domain.model.PathNode
import com.example.kimsia.domain.model.UserProfile
import com.example.kimsia.ui.components.KimsiaTopBar
import com.example.kimsia.ui.components.LearningPathView
import com.example.kimsia.ui.components.UnitBanner
import com.example.kimsia.ui.theme.Cream
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.KimsiaTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onLessonClick: (lessonId: Long) -> Unit = {}, // Fase 3: navega a la LessonScreen
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state = state, onNodeClick = { node ->
        if (node.type == NodeType.LESSON) onLessonClick(node.id)
    }, modifier = modifier)
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onNodeClick: (PathNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(Cream)) {
        KimsiaTopBar(
            subtitle = state.path?.language?.name ?: "Cargando…",
            streak = state.profile?.streakDays ?: 0,
            hearts = state.profile?.hearts ?: 0,
        )

        val path = state.path
        if (path == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Emerald)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                UnitBanner(unitNumber = path.unit.orderIndex, title = path.unit.title)
                LearningPathView(nodes = path.nodes, onNodeClick = onNodeClick)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 780)
@Composable
private fun HomeContentPreview() {
    val language = Language(3, "gn", "Guaraní", null, true)
    val unit = LearningUnit(3, 3, "Saludos y Presentaciones", "", 1)
    val nodes = listOf(
        PathNode(31, "Lección 1: Presentaciones", NodeType.LESSON, NodeStatus.COMPLETED, 20),
        PathNode(32, "Lección 2: Saludos", NodeType.LESSON, NodeStatus.CURRENT, 20),
        PathNode(33, "Lección 3: Familia", NodeType.LESSON, NodeStatus.LOCKED, 20),
        PathNode(-3, "Cofre Final", NodeType.CHEST, NodeStatus.LOCKED, 0),
    )
    KimsiaTheme {
        HomeContent(
            state = HomeUiState(false, LearningPath(language, unit, nodes), UserProfile("u", "Estudiante Kimsia", null, 5, 3, 1240)),
            onNodeClick = {},
        )
    }
}
