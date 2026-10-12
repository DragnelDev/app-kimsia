package com.example.kimsia.ui.lesson

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.HeartRed
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.TrackGray

/** Barra superior de la lección: salir (X), progreso animado y corazones. */
@Composable
fun LessonTopBar(
    progress: Float,
    hearts: Int?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "lessonProgress")

    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Salir de la lección", tint = InkSoft)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(TrackGray),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Emerald),
            )
        }
        if (hearts != null) {
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Filled.Favorite, contentDescription = "Corazones", tint = HeartRed, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(4.dp))
            Text("$hearts", color = HeartRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
        }
    }
}
