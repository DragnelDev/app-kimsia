package com.example.kimsia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.R
import com.example.kimsia.ui.theme.EmeraldDeep
import com.example.kimsia.ui.theme.HeartRed
import com.example.kimsia.ui.theme.HeartSoft
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.SunYellow
import com.example.kimsia.ui.theme.SunYellowSoft
import com.example.kimsia.ui.theme.Terracotta

@Composable
fun KimsiaTopBar(
    subtitle: String,
    streak: Int,
    hearts: Int,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier, color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.kimsia_logo),
                contentDescription = "Kimsia",
                modifier = Modifier.size(40.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("KIMSIA", color = EmeraldDeep, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, letterSpacing = 1.sp)
                Text(subtitle, color = InkSoft, fontSize = 12.sp)
            }
            StatChip(Icons.Filled.LocalFireDepartment, streak, Terracotta, SunYellowSoft, SunYellow)
            Spacer(Modifier.width(8.dp))
            StatChip(Icons.Filled.Favorite, hearts, HeartRed, HeartSoft, HeartRed.copy(alpha = 0.25f))
        }
    }
}

@Composable
fun StatChip(
    icon: ImageVector,
    value: Int,
    tint: Color,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = background,
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Text("$value", color = tint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
