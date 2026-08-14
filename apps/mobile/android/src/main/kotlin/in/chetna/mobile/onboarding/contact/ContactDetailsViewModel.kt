package `in`.chetna.mobile.onboarding.contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class ContactMethod { EMAIL, SMS }

private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$")

data class ContactDetailsUiState(
    val email: String = "",
    val selectedMethod: ContactMethod = ContactMethod.EMAIL,
    val consentChecked: Boolean = false,
) {
    val isEmailValid: Boolean = EMAIL_REGEX.matches(email)
    val isContinueEnabled: Boolean = isEmailValid && consentChecked
}

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Scoped to the Contact-details back-stack entry (hiltViewModel()), so
    // the "email" nav argument (Route.Contact's field, exposed directly as a
    // SavedStateHandle key by navigation-compose's type-safe routes) carries
    // the previously entered email back when returning from OTP verify — see
    // the "Back navigation preserves previously entered data" spec
    // requirement. Read via the individual key rather than
    // SavedStateHandle.toRoute<>() so this stays a plain, JVM-unit-testable
    // constructor (SavedStateHandle(mapOf(...))), not one that needs the
    // internal route-typeMap wiring `toRoute<>()` relies on.
    private val _uiState = MutableStateFlow(
        ContactDetailsUiState(email = savedStateHandle.get<String>("email") ?: "")
    )
    val uiState: StateFlow<ContactDetailsUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSelectMethod(method: ContactMethod) {
        // SMS is a visibly disabled "Coming soon" option (ADR-0010) — a
        // selection attempt is a no-op, not an error, matching the
        // prototype's disabled-row behavior.
        if (method == ContactMethod.EMAIL) {
            _uiState.update { it.copy(selectedMethod = ContactMethod.EMAIL) }
        }
    }

    fun onConsentChanged(checked: Boolean) {
        _uiState.update { it.copy(consentChecked = checked) }
    }
}
