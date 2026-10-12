package com.example.kimsia.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.domain.model.NodeStatus
import com.example.kimsia.domain.model.NodeType
import com.example.kimsia.domain.model.PathNode
import com.example.kimsia.ui.theme.Bubble
import com.example.kimsia.ui.theme.ChestPurple
import com.example.kimsia.ui.theme.ChestSoft
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.LockedGray
import com.example.kimsia.ui.theme.SunYellow
import com.example.kimsia.ui.theme.SunYellowDark
import com.example.kimsia.ui.theme.SunYellowSoft
import com.example.kimsia.ui.theme.TrackGray
import kotlin.math.PI
import kotlin.math.sin

private val ItemHeight = 132.dp
private val NodeWidth = 170.dp
private val VerticalPadding = 24.dp
private val Amplitude = 64.dp

/**
 * Ruta serpenteante estilo Duolingo: el trazo se dibuja con Bézier entre los centros de los nodos
 * y los nodos se posicionan encima con el mismo cálculo (centro X = ancho/2 + A·sin(i·π/2)).
 */
@Composable
fun LearningPathView(
    nodes: List<PathNode>,
    onNodeClick: (PathNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalHeight = VerticalPadding * 2 + ItemHeight * nodes.size

    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(totalHeight)) {
        val centerX = maxWidth / 2
        fun cx(i: Int): Dp = centerX + Amplitude * sin(i * PI / 2).toFloat()
        fun cy(i: Int): Dp = VerticalPadding + ItemHeight * i + ItemHeight / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until nodes.size - 1) {
                val start = Offset(cx(i).toPx(), cy(i).toPx())
                val end = Offset(cx(i + 1).toPx(), cy(i + 1).toPx())
                val midY = (start.y + end.y) / 2
                val segment = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(start.x, midY, end.x, midY, end.x, end.y)
                }
                drawPath(
                    path = segment,
                    color = if (nodes[i].status != NodeStatus.LOCKED) Emerald else TrackGray,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }

        nodes.forEachIndexed { i, node ->
            Box(
                modifier = Modifier
                    .offset(x = cx(i) - NodeWidth / 2, y = cy(i) - ItemHeight / 2)
                    .size(NodeWidth, ItemHeight),
                contentAlignment = Alignment.Center,
            ) {
                PathNodeItem(node = node, onClick = { onNodeClick(node) })
            }
        }
    }
}

@Composable
private fun PathNodeItem(node: PathNode, onClick: () -> Unit) {
    val clickable = node.status != NodeStatus.LOCKED
    val tap = Modifier.clickable(enabled = clickable, role = Role.Button, onClick = onClick)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when {
            node.type == NodeType.CHEST -> ChestNode(node, tap)
            node.status == NodeStatus.COMPLETED -> CompletedNode(tap)
            node.status == NodeStatus.CURRENT -> CurrentNode(node, tap)
            else -> LockedNode(node)
        }
    }
}

@Composable
private fun CompletedNode(tap: Modifier) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Emerald)
            .border(3.dp, Color.White, CircleShape)
            .then(tap),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Check, contentDescription = "Lección completada", tint = Color.White, modifier = Modifier.size(30.dp))
    }
}

@Composable
private fun CurrentNode(node: PathNode, tap: Modifier) {
    val transition = rememberInfiniteTransition(label = "bob")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bobOffset",
    )

    Text(
        "¡Empezar!",
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .offset { IntOffset(0, bob.dp.roundToPx()) }
            .clip(RoundedCornerShape(14.dp))
            .background(Bubble)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
    Spacer(Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .size(84.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(SunYellow)
            .border(6.dp, Color(0xFFFDE68A), CircleShape)
            .then(tap),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Star, contentDescription = "Empezar ${node.title}", tint = Color.White, modifier = Modifier.size(38.dp))
    }
    Spacer(Modifier.height(6.dp))
    Text(
        node.title,
        color = SunYellowDark,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SunYellowSoft)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun LockedNode(node: PathNode) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0xFFE5E7EB))
            .border(3.dp, LockedGray, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Lock, contentDescription = "Bloqueada", tint = Color(0xFF9CA3AF), modifier = Modifier.size(26.dp))
    }
    Spacer(Modifier.height(4.dp))
    Text(node.title.substringBefore(":"), color = InkSoft, fontSize = 12.sp)
}

@Composable
private fun ChestNode(node: PathNode, tap: Modifier) {
    val locked = node.status == NodeStatus.LOCKED
    Box(
        modifier = Modifier
            .alpha(if (locked) 0.6f else 1f)
            .size(52.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(ChestSoft)
            .border(3.dp, ChestPurple.copy(alpha = 0.5f), CircleShape)
            .then(tap),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.CardGiftcard, contentDescription = node.title, tint = ChestPurple, modifier = Modifier.size(28.dp))
    }
    Spacer(Modifier.height(4.dp))
    Text(node.title, color = ChestPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
}
