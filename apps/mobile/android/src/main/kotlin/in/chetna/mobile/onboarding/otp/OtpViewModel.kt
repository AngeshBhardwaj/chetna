package `in`.chetna.mobile.onboarding.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.chetna.mobile.session.SessionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val OTP_LENGTH = 6
private const val MAX_ATTEMPTS = 3
private const val RESEND_SECONDS = 30
private const val VERIFIED_NAVIGATE_DELAY_MS = 800L
private const val COUNTDOWN_TICK_MS = 1000L

// Mock-only, matching the prototype — no real backend to verify an OTP
// against yet (real auth is explicit follow-up work after this UI ships).
private const val MOCK_CORRECT_CODE = "123456"

enum class OtpStatus { ENTERING, VERIFIED, WRONG, LOCKED }

data class OtpUiState(
    val digits: List<String> = List(OTP_LENGTH) { "" },
    val status: OtpStatus = OtpStatus.ENTERING,
    val attemptsLeft: Int = MAX_ATTEMPTS,
    val resendSecondsLeft: Int = RESEND_SECONDS,
    val focusedIndex: Int = 0,
) {
    val canRetry: Boolean = status == OtpStatus.WRONG
    val fieldsEnabled: Boolean = status != OtpStatus.VERIFIED && status != OtpStatus.LOCKED
    val isResendEnabled: Boolean = resendSecondsLeft == 0 && status != OtpStatus.LOCKED
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionState: SessionState,
) : ViewModel() {

    val email: String = savedStateHandle.get<String>("email") ?: ""

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    // One-shot navigation signal — the ViewModel stays navigation-agnostic;
    // MainActivity/NavHost owns the actual navigate() call.
    private val _verified = MutableSharedFlow<Unit>()
    val verified: SharedFlow<Unit> = _verified.asSharedFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onDigitChanged(index: Int, rawValue: String) {
        if (!_uiState.value.fieldsEnabled) return
        val digit = rawValue.filter { it.isDigit() }.takeLast(1)
        val nextDigits = _uiState.value.digits.toMutableList().apply { this[index] = digit }
        val nextFocus = if (digit.isNotEmpty() && index < OTP_LENGTH - 1) index + 1 else index
        _uiState.update { it.copy(digits = nextDigits, focusedIndex = nextFocus) }

        if (nextDigits.all { it.isNotEmpty() }) {
            checkCode(nextDigits.joinToString(""))
        }
    }

    private fun checkCode(code: String) {
        if (code == MOCK_CORRECT_CODE) {
            _uiState.update { it.copy(status = OtpStatus.VERIFIED) }
            sessionState.setVerifiedEmail(email)
            viewModelScope.launch {
                delay(VERIFIED_NAVIGATE_DELAY_MS)
                _verified.emit(Unit)
            }
            return
        }
        val remaining = _uiState.value.attemptsLeft - 1
        _uiState.update {
            it.copy(
                attemptsLeft = remaining,
                status = if (remaining > 0) OtpStatus.WRONG else OtpStatus.LOCKED,
            )
        }
    }

    fun onClearAndRetry() {
        // Only available before lockout — matching the prototype, there is
        // no in-body recovery once locked (the back arrow is the only path).
        if (_uiState.value.status != OtpStatus.WRONG) return
        _uiState.update {
            it.copy(digits = List(OTP_LENGTH) { "" }, status = OtpStatus.ENTERING, focusedIndex = 0)
        }
    }

    fun onResend() {
        if (!_uiState.value.isResendEnabled) return
        // Does NOT reset attemptsLeft — see the "Resend requests a new code
        // without resetting attempts" spec requirement.
        _uiState.update { it.copy(resendSecondsLeft = RESEND_SECONDS) }
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_uiState.value.resendSecondsLeft > 0) {
                delay(COUNTDOWN_TICK_MS)
                _uiState.update { it.copy(resendSecondsLeft = (it.resendSecondsLeft - 1).coerceAtLeast(0)) }
            }
        }
    }
}
