package com.example.kimsia.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.Ink
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.Terracotta
import com.example.kimsia.ui.theme.TrackGray

enum class OptionVisualState { IDLE, SELECTED, CORRECT, WRONG, DIMMED }

private val WrongSoft = Color(0xFFFFEDD5)

/** Tarjeta de opción (texto y/o imagen) con estados de selección y de resultado. */
@Composable
fun OptionCard(
    label: String,
    text: String,
    imageUrl: String?,
    number: Int,
    state: OptionVisualState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val border by animateColorAsState(
        when (state) {
            OptionVisualState.SELECTED, OptionVisualState.CORRECT -> Emerald
            OptionVisualState.WRONG -> Terracotta
            else -> TrackGray
        },
        label = "optionBorder",
    )
    val container by animateColorAsState(
        when (state) {
            OptionVisualState.SELECTED -> EmeraldSoft.copy(alpha = 0.6f)
            OptionVisualState.CORRECT -> EmeraldSoft
            OptionVisualState.WRONG -> WrongSoft
            else -> Color.White
        },
        label = "optionContainer",
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().alpha(if (state == OptionVisualState.DIMMED) 0.55f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = container,
        border = BorderStroke(2.dp, border),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.heightIn(min = 60.dp).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = text,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                "$label) $text",
                color = if (state == OptionVisualState.WRONG) Terracotta else if (state == OptionVisualState.CORRECT) EmeraldDark else Ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, InkSoft.copy(alpha = 0.4f)),
                    modifier = Modifier.size(26.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("$number", color = InkSoft, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
