package `in`.chetna.mobile.onboarding.otp

import androidx.lifecycle.SavedStateHandle
import `in`.chetna.mobile.session.SessionState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

private fun enterCode(viewModel: OtpViewModel, code: String) {
    code.forEachIndexed { index, digit -> viewModel.onDigitChanged(index, digit.toString()) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest : FunSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun newViewModel() = OtpViewModel(
        SavedStateHandle(mapOf("email" to "guardian@example.com")),
        SessionState(),
    )

    test("digit entry advances focus to the next field") {
        val viewModel = newViewModel()
        viewModel.onDigitChanged(0, "1")

        viewModel.uiState.value.focusedIndex shouldBe 1
        viewModel.uiState.value.digits[0] shouldBe "1"
    }

    test("filling all 6 digits with the correct code transitions to verified") {
        val viewModel = newViewModel()
        enterCode(viewModel, "123456")

        viewModel.uiState.value.status shouldBe OtpStatus.VERIFIED
    }

    test("wrong code decrements attempts and transitions to wrong") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")

        viewModel.uiState.value.status shouldBe OtpStatus.WRONG
        viewModel.uiState.value.attemptsLeft shouldBe 2
    }

    test("third wrong attempt locks the screen") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")

        viewModel.uiState.value.status shouldBe OtpStatus.LOCKED
        viewModel.uiState.value.attemptsLeft shouldBe 0
    }

    test("clear and retry resets digits and status to entering") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()

        viewModel.uiState.value.status shouldBe OtpStatus.ENTERING
        viewModel.uiState.value.digits shouldBe List(6) { "" }
    }

    test("clear and retry is a no-op once locked") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")
        val lockedState = viewModel.uiState.value

        viewModel.onClearAndRetry()

        viewModel.uiState.value shouldBe lockedState
    }

    test("resend disabled during countdown, does not reset the attempt counter once available") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")
        viewModel.uiState.value.attemptsLeft shouldBe 2

        // Countdown hasn't elapsed yet — resend is a no-op.
        viewModel.onResend()
        viewModel.uiState.value.resendSecondsLeft shouldBe 30

        dispatcher.scheduler.advanceTimeBy(30_001)
        dispatcher.scheduler.runCurrent()
        viewModel.uiState.value.resendSecondsLeft shouldBe 0

        viewModel.onResend()
        viewModel.uiState.value.resendSecondsLeft shouldBe 30
        viewModel.uiState.value.attemptsLeft shouldBe 2
    }

    test("resend stays disabled once locked, even after the countdown elapses") {
        val viewModel = newViewModel()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")
        viewModel.onClearAndRetry()
        enterCode(viewModel, "000000")
        viewModel.uiState.value.status shouldBe OtpStatus.LOCKED

        dispatcher.scheduler.advanceTimeBy(30_001)
        dispatcher.scheduler.runCurrent()

        viewModel.uiState.value.resendSecondsLeft shouldBe 0
        viewModel.uiState.value.isResendEnabled shouldBe false
    }
})
