package me.wishwith.android.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.wishwith.android.R
import me.wishwith.android.ui.auth.AuthViewModel
import me.wishwith.android.ui.components.AvatarView
import me.wishwith.android.ui.theme.BrandPrimary

@Composable
fun HomeScreen(
    onNavigateToWishlists: () -> Unit,
    onNavigateToShared: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Greeting
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarView(
                avatarBase64 = currentUser?.avatarBase64,
                name = currentUser?.name ?: "",
                size = 48
            )
            Column {
                Text(
                    text = stringResource(R.string.greeting_hello),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentUser?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation cards
        HomeNavigationCard(
            icon = Icons.AutoMirrored.Filled.ViewList,
            iconColor = BrandPrimary,
            title = stringResource(R.string.home_my_wishlists),
            subtitle = stringResource(R.string.home_my_wishlists_subtitle),
            onClick = onNavigateToWishlists
        )
        Spacer(modifier = Modifier.height(12.dp))
        HomeNavigationCard(
            icon = Icons.Filled.People,
            iconColor = Color(0xFFFF9800),
            title = stringResource(R.string.home_shared_with_me),
            subtitle = stringResource(R.string.home_shared_subtitle),
            onClick = onNavigateToShared
        )
        Spacer(modifier = Modifier.height(12.dp))
        HomeNavigationCard(
            icon = Icons.Filled.Person,
            iconColor = Color(0xFF009688),
            title = stringResource(R.string.home_profile),
            subtitle = stringResource(R.string.home_profile_subtitle),
            onClick = onNavigateToProfile
        )
        Spacer(modifier = Modifier.height(12.dp))
        HomeNavigationCard(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            iconColor = Color.Red,
            title = stringResource(R.string.home_log_out),
            subtitle = stringResource(R.string.home_log_out_subtitle),
            onClick = onLogout
        )
    }
}

@Composable
private fun HomeNavigationCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
