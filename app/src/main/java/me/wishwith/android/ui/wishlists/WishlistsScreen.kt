package me.wishwith.android.ui.wishlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.wishwith.android.R
import me.wishwith.android.data.local.entity.WishlistEntity
import me.wishwith.android.ui.components.SyncStatusIndicator
import me.wishwith.android.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: WishlistsViewModel = hiltViewModel()
) {
    val wishlists by viewModel.wishlists.collectAsState()
    val itemCounts by viewModel.itemCounts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    var showCreateSheet by remember { mutableStateOf(false) }
    var editWishlist by remember { mutableStateOf<WishlistEntity?>(null) }
    var deleteWishlist by remember { mutableStateOf<WishlistEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.my_wishlists),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                SyncStatusIndicator(
                    syncState = syncState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (wishlists.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_wishlists_yet),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_wishlists_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(onClick = { showCreateSheet = true }) {
                            Text(
                                text = stringResource(R.string.new_wishlist),
                                color = BrandPrimary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(wishlists, key = { it.id }) { wishlist ->
                            WishlistRow(
                                wishlist = wishlist,
                                itemCount = itemCounts[wishlist.id] ?: 0,
                                onClick = { onNavigateToDetail(wishlist.id) },
                                onEdit = { editWishlist = wishlist },
                                onDelete = { deleteWishlist = wishlist },
                                onShare = { onNavigateToDetail(wishlist.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showCreateSheet = true },
            containerColor = BrandPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_wishlist))
        }
    }

    // Create/Edit sheet
    if (showCreateSheet || editWishlist != null) {
        CreateEditWishlistSheet(
            wishlist = editWishlist,
            onDismiss = { showCreateSheet = false; editWishlist = null },
            onSave = { name, description, icon, iconColor ->
                if (editWishlist != null) {
                    viewModel.updateWishlist(editWishlist!!, name, description, icon, iconColor)
                } else {
                    viewModel.createWishlist(name, description, icon, iconColor)
                }
                showCreateSheet = false
                editWishlist = null
            }
        )
    }

    // Delete confirmation
    if (deleteWishlist != null) {
        AlertDialog(
            onDismissRequest = { deleteWishlist = null },
            title = { Text(stringResource(R.string.delete_wishlist)) },
            text = {
                Text(stringResource(R.string.delete_wishlist_confirm, deleteWishlist!!.name))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteWishlist(deleteWishlist!!)
                    deleteWishlist = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteWishlist = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
