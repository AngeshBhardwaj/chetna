package `in`.chetna.mobile.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Holds the mocked-auth session's verified email across the OTP → Signed-in
// hop (a plain nav argument isn't enough — Logout must clear it so a fresh
// login round-trip starts empty, per the "Logout returns to Welcome and
// clears state" spec requirement). Singleton-scoped, not tied to any one
// screen's back-stack entry.
@Singleton
class SessionState @Inject constructor() {
    private val _verifiedEmail = MutableStateFlow<String?>(null)
    val verifiedEmail: StateFlow<String?> = _verifiedEmail.asStateFlow()

    fun setVerifiedEmail(email: String) {
        _verifiedEmail.value = email
    }

    fun clear() {
        _verifiedEmail.value = null
    }
}
