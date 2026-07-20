package me.wishwith.android.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.wishwith.android.R
import me.wishwith.android.ui.components.AvatarView
import me.wishwith.android.ui.theme.BrandPrimary
import me.wishwith.android.util.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "") }
    var slug by remember(user) { mutableStateOf(user?.publicUrlSlug ?: "") }
    var birthday by remember(user) { mutableStateOf(user?.birthday ?: "") }
    var avatarBase64 by remember(user) { mutableStateOf(user?.avatarBase64) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                val resized = ImageUtils.resizeAvatar(bytes)
                if (resized != null) {
                    avatarBase64 = resized
                    viewModel.updateAvatar(resized)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.profile)) },
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Filled.Settings, stringResource(R.string.settings))
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { photoLauncher.launch("image/*") }
            ) {
                AvatarView(
                    avatarBase64 = avatarBase64,
                    name = name,
                    size = 80
                )
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = stringResource(R.string.change_avatar),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearMessages() },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it; viewModel.clearMessages() },
                label = { Text(stringResource(R.string.bio)) },
                placeholder = { Text(stringResource(R.string.bio_placeholder)) },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Slug
            OutlinedTextField(
                value = slug,
                onValueChange = { slug = it; viewModel.clearMessages() },
                label = { Text(stringResource(R.string.public_url_slug)) },
                prefix = { Text(stringResource(R.string.slug_prefix)) },
                placeholder = { Text(stringResource(R.string.slug_placeholder)) },
                singleLine = true,
                isError = !viewModel.isSlugValid(slug),
                supportingText = {
                    if (!viewModel.isSlugValid(slug)) {
                        Text(stringResource(R.string.slug_error))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Birthday
            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it; viewModel.clearMessages() },
                label = { Text(stringResource(R.string.birthday)) },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Messages
            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = BrandPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveProfile(
                        name = name,
                        bio = bio.ifBlank { null },
                        publicUrlSlug = slug.ifBlank { null },
                        birthday = birthday.ifBlank { null },
                        avatarBase64 = avatarBase64
                    )
                },
                enabled = !isSaving && viewModel.canSave(name, slug),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(stringResource(R.string.save_profile))
            }
        }
    }
}
