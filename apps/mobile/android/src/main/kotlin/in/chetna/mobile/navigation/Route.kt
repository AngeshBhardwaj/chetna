package `in`.chetna.mobile.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Welcome : Route

    @Serializable
    data class Contact(val email: String = "") : Route

    @Serializable
    data class Otp(val email: String) : Route

    @Serializable
    data object SignedIn : Route
}
