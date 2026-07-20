package me.wishwith.android.ui.shared

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.wishwith.android.R
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.data.local.entity.MarkEntity
import me.wishwith.android.ui.theme.BrandPrimary

@Composable
fun SharedItemCard(
    item: ItemEntity,
    marks: List<MarkEntity>,
    currentUserId: String?,
    canMark: Boolean,
    onMark: () -> Unit,
    onUnmark: (MarkEntity) -> Unit
) {
    val totalMarked = marks.sumOf { it.quantity }
    val myMark = marks.find { it.markedBy == currentUserId }
    val isFullyMarked = totalMarked >= item.quantity

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SharedItemImage(
                imageBase64 = item.imageBase64,
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.price != null) {
                    Text(
                        text = "${item.price} ${item.currency ?: ""}".trim(),
                        style = MaterialTheme.typography.titleSmall,
                        color = BrandPrimary
                    )
                }
                if (item.quantity > 1) {
                    Text(
                        text = stringResource(R.string.marked_count, totalMarked, item.quantity),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mark state
                when {
                    myMark != null -> {
                        Text(
                            text = stringResource(R.string.marked_by_you),
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandPrimary
                        )
                        OutlinedButton(
                            onClick = { onUnmark(myMark) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(stringResource(R.string.unmark), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    isFullyMarked -> {
                        Text(
                            text = stringResource(R.string.already_taken),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    canMark -> {
                        Button(
                            onClick = onMark,
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text(stringResource(R.string.ill_get_this), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedItemImage(
    imageBase64: String?,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(imageBase64) {
        imageBase64?.let { base64 ->
            try {
                val data = base64.substringAfter("base64,", base64)
                val bytes = Base64.decode(data, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }

    when {
        bitmap != null -> {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        imageUrl != null -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else -> {
            Box(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.CardGiftcard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
