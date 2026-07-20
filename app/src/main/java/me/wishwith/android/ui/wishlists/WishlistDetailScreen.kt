package me.wishwith.android.ui.wishlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.ui.components.SyncStatusIndicator
import me.wishwith.android.ui.shared.ShareSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailScreen(
    wishlistId: String,
    onNavigateBack: () -> Unit,
    isTabBarCollapsed: Boolean,
    onTabBarCollapseChanged: (Boolean) -> Unit,
    viewModel: WishlistDetailViewModel = hiltViewModel()
) {
    val wishlist by viewModel.wishlist.collectAsState()
    val items by viewModel.items.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val isOwner by viewModel.isOwner.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<ItemEntity?>(null) }
    var deleteItem by remember { mutableStateOf<ItemEntity?>(null) }
    var showShareSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isScrolled by remember { derivedStateOf { listState.firstVisibleItemScrollOffset > 10 } }

    LaunchedEffect(wishlistId) {
        viewModel.loadWishlist(wishlistId)
    }

    LaunchedEffect(isScrolled) {
        onTabBarCollapseChanged(isScrolled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text(wishlist?.name ?: "") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                SyncStatusIndicator(syncState = syncState)
                if (isOwner) {
                    IconButton(onClick = { showShareSheet = true }) {
                        Icon(Icons.Filled.Share, stringResource(R.string.share))
                    }
                    IconButton(onClick = { showAddSheet = true }) {
                        Icon(Icons.Filled.Add, stringResource(R.string.add_item))
                    }
                }
            }
        )

        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_items_yet),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_items_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                if (isOwner) {
                    Spacer(modifier = Modifier.height(24.dp))
                    FilledTonalButton(onClick = { showAddSheet = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(stringResource(R.string.add_item))
                    }
                }
            }
        } else {
            // Description
            if (!wishlist?.description.isNullOrBlank()) {
                Text(
                    text = wishlist?.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        onEdit = { editItem = item },
                        onDelete = { deleteItem = item }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Add item sheet
    if (showAddSheet) {
        AddItemSheet(
            onDismiss = { showAddSheet = false },
            onAddByUrl = { url ->
                viewModel.createItemByUrl(wishlistId, url)
                showAddSheet = false
            },
            onAddManually = { title, desc, price, currency, qty, sourceUrl, imageBase64 ->
                viewModel.createItemManually(wishlistId, title, desc, price, currency, qty, sourceUrl, imageBase64)
                showAddSheet = false
            }
        )
    }

    // Edit item sheet
    if (editItem != null) {
        EditItemSheet(
            item = editItem!!,
            onDismiss = { editItem = null },
            onSave = { title, desc, price, currency, qty, sourceUrl, imageBase64 ->
                viewModel.updateItem(editItem!!, title, desc, price, currency, qty, sourceUrl, imageBase64)
                editItem = null
            }
        )
    }

    // Delete confirmation
    if (deleteItem != null) {
        AlertDialog(
            onDismissRequest = { deleteItem = null },
            title = { Text(stringResource(R.string.delete_item)) },
            text = { Text(stringResource(R.string.delete_item_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(deleteItem!!)
                    deleteItem = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteItem = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Share sheet
    if (showShareSheet && wishlist != null) {
        ShareSheet(
            wishlistId = wishlist!!.id,
            onDismiss = { showShareSheet = false }
        )
    }
}
