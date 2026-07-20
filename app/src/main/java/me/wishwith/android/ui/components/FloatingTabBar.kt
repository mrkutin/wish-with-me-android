package me.wishwith.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.wishwith.android.R
import me.wishwith.android.ui.navigation.MainTab
import me.wishwith.android.ui.theme.BrandPrimary

@Composable
fun FloatingTabBar(
    selectedTab: MainTab,
    isCollapsed: Boolean,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isCollapsed) 24.dp else 28.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessLow),
        label = "cornerRadius"
    )

    Row(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val isMiddleTab = tab == MainTab.WISHLISTS || tab == MainTab.SHARED

            val tabAlpha by animateFloatAsState(
                targetValue = if (isCollapsed && isMiddleTab) 0f else 1f,
                animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessLow),
                label = "tabAlpha"
            )

            if (!isCollapsed || !isMiddleTab) {
                TabItem(
                    tab = tab,
                    isSelected = isSelected,
                    showLabel = !isCollapsed,
                    alpha = tabAlpha,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: MainTab,
    isSelected: Boolean,
    showLabel: Boolean,
    alpha: Float,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            BrandPrimary.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f),
        label = "tabBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabContent"
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = if (showLabel) 12.dp else 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = tab.icon(),
                contentDescription = tab.label(),
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            if (showLabel && isSelected) {
                Text(
                    text = tab.label(),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MainTab.icon(): ImageVector = when (this) {
    MainTab.HOME -> Icons.Filled.Home
    MainTab.WISHLISTS -> Icons.AutoMirrored.Filled.ViewList
    MainTab.SHARED -> Icons.Filled.People
    MainTab.PROFILE -> Icons.Filled.Person
}

@Composable
private fun MainTab.label(): String = when (this) {
    MainTab.HOME -> stringResource(R.string.tab_home)
    MainTab.WISHLISTS -> stringResource(R.string.tab_wishlists)
    MainTab.SHARED -> stringResource(R.string.tab_shared)
    MainTab.PROFILE -> stringResource(R.string.tab_profile)
}
