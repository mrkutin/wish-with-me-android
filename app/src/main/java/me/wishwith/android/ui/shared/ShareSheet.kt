package me.wishwith.android.ui.shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.wishwith.android.R
import me.wishwith.android.data.local.entity.ShareEntity
import me.wishwith.android.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    wishlistId: String,
    onDismiss: () -> Unit,
    viewModel: ShareViewModel = hiltViewModel()
) {
    val shares by viewModel.shares.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    val context = LocalContext.current
    var revokeShare by remember { mutableStateOf<ShareEntity?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(wishlistId) {
        viewModel.loadShares(wishlistId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.share_wishlist),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showCreateDialog = true },
                enabled = !isCreating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(stringResource(R.string.create_share_link))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (shares.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_share_links),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.share_links),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(shares) { share ->
                        ShareLinkCard(
                            share = share,
                            shareUrl = viewModel.getShareUrl(share),
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("share link", viewModel.getShareUrl(share)))
                            },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, viewModel.getShareUrl(share))
                                }
                                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)))
                            },
                            onRevoke = { revokeShare = share }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.create_share_link)) },
            text = { Text("Choose link type:") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createShareLink(wishlistId, "mark")
                    showCreateDialog = false
                }) { Text(stringResource(R.string.view_and_mark)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.createShareLink(wishlistId, "view")
                    showCreateDialog = false
                }) { Text(stringResource(R.string.view_only)) }
            }
        )
    }

    if (revokeShare != null) {
        AlertDialog(
            onDismissRequest = { revokeShare = null },
            title = { Text(stringResource(R.string.revoke_link)) },
            text = { Text(stringResource(R.string.revoke_link_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.revokeShareLink(wishlistId, revokeShare!!)
                    revokeShare = null
                }) { Text(stringResource(R.string.revoke_link), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { revokeShare = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun ShareLinkCard(
    share: ShareEntity,
    shareUrl: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onRevoke: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (share.linkType == "mark") stringResource(R.string.view_and_mark) else stringResource(R.string.view_only),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(R.string.used_count, share.accessCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                IconButton(onClick = onCopy) { Icon(Icons.Filled.ContentCopy, stringResource(R.string.copy_link)) }
                IconButton(onClick = onShare) { Icon(Icons.Filled.Share, stringResource(R.string.share_link)) }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onRevoke) {
                    Text(stringResource(R.string.revoke_link), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
