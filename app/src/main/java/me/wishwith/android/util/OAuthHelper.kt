package me.wishwith.android.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object OAuthHelper {

    private const val BASE_URL = "https://api.wishwith.me"
    private const val CALLBACK_URL = "wishwithme://auth/callback"

    fun getAuthorizeUrl(provider: String): String {
        return "$BASE_URL/api/v1/oauth/$provider/authorize?callback_url=$CALLBACK_URL"
    }

    fun openOAuthFlow(context: Context, provider: String) {
        val url = getAuthorizeUrl(provider)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun parseCallback(uri: Uri): OAuthResult {
        val error = uri.getQueryParameter("error")
        if (error != null) {
            val email = uri.getQueryParameter("email")
            return OAuthResult.Error(error, email)
        }

        val linked = uri.getQueryParameter("linked")
        if (linked != null) {
            return OAuthResult.Linked(linked)
        }

        val accessToken = uri.getQueryParameter("access_token")
        val refreshToken = uri.getQueryParameter("refresh_token")
        if (accessToken != null && refreshToken != null) {
            return OAuthResult.Success(accessToken, refreshToken)
        }

        return OAuthResult.Error("unknown", null)
    }

    sealed class OAuthResult {
        data class Success(val accessToken: String, val refreshToken: String) : OAuthResult()
        data class Linked(val provider: String) : OAuthResult()
        data class Error(val error: String, val email: String?) : OAuthResult()
    }
}
