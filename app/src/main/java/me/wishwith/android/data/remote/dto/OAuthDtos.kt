package me.wishwith.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthProvidersResponse(
    val providers: List<String>
)

@Serializable
data class ConnectedAccountsResponse(
    val accounts: List<ConnectedAccount>,
    @SerialName("has_password")
    val hasPassword: Boolean = true
)

@Serializable
data class ConnectedAccount(
    val provider: String,
    val email: String,
    @SerialName("connected_at")
    val connectedAt: String
)

@Serializable
data class OAuthLinkInitiateResponse(
    @SerialName("authorization_url")
    val authorizationUrl: String,
    val state: String
)
