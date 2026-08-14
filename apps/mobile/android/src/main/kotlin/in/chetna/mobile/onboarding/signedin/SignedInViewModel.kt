package `in`.chetna.mobile.onboarding.signedin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.chetna.mobile.session.SessionState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignedInViewModel @Inject constructor(
    private val sessionState: SessionState,
) : ViewModel() {

    val email: StateFlow<String?> = sessionState.verifiedEmail

    fun onLogout() {
        sessionState.clear()
    }
}
