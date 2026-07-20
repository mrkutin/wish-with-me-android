package me.wishwith.android.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.wishwith.android.R
import me.wishwith.android.ui.theme.GoogleBlue
import me.wishwith.android.ui.theme.YandexRed
import me.wishwith.android.util.OAuthHelper

@Composable
fun SocialLoginButtons(
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { OAuthHelper.openOAuthFlow(context, "google") },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue)
        ) {
            Text(
                text = stringResource(R.string.google),
                color = Color.White
            )
        }
        Button(
            onClick = { OAuthHelper.openOAuthFlow(context, "yandex") },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = YandexRed)
        ) {
            Text(
                text = stringResource(R.string.yandex),
                color = Color.White
            )
        }
    }
}
