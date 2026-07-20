package me.wishwith.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.wishwith.android.domain.sync.SyncState

@Composable
fun SyncStatusIndicator(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    when (syncState) {
        SyncState.SYNCING -> {
            val infiniteTransition = rememberInfiniteTransition(label = "sync")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = "Syncing",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .size(20.dp)
                    .rotate(rotation)
            )
        }
        SyncState.ERROR -> {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = "Sync error",
                tint = MaterialTheme.colorScheme.error,
                modifier = modifier.size(20.dp)
            )
        }
        SyncState.OFFLINE -> {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = "Offline",
                tint = Color(0xFFFF9800),
                modifier = modifier.size(20.dp)
            )
        }
        SyncState.IDLE -> {
            // No indicator when synced and idle
        }
    }
}
