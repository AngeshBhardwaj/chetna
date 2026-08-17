package `in`.chetna.mobile.onboarding.otp

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import `in`.chetna.mobile.session.SessionState
import org.junit.Rule
import org.junit.Test

// Constructs OtpViewModel directly (SavedStateHandle, no Hilt context) — same
// rationale as ContactScreenTest. Covers the wrong-code retry/lockout path
// (task 6.3) and the success path plus resend's initial disabled state
// (task 6.4). Config-change survival is a separate test (OtpConfigChangeTest)
// since it needs a real Activity + Hilt. Execution deferred to Section 10.
class OtpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun enterCode(code: String) {
        code.forEachIndexed { index, digit ->
            composeTestRule.onNodeWithTag("otp-digit-$index").performTextInput(digit.toString())
        }
    }

    @Test
    fun wrongCodeRetryPathThenLockout() {
        composeTestRule.setContent {
            OtpScreen(onBack = {}, onVerified = {}, viewModel = OtpViewModel(SavedStateHandle(), SessionState()))
        }

        // 1st wrong attempt: error shown, retry available.
        enterCode("000000")
        composeTestRule.onNodeWithText("Incorrect code. 2 attempts left.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear and try again").performClick()

        // 2nd wrong attempt.
        enterCode("111111")
        composeTestRule.onNodeWithText("Incorrect code. 1 attempt left.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear and try again").performClick()

        // 3rd wrong attempt: locked, no retry action, fields disabled.
        enterCode("222222")
        composeTestRule.onNodeWithText("Too many incorrect attempts. Go back and request a new code.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("otp-digit-0").assertIsNotEnabled()
        // "Clear and try again" must not exist once locked.
        composeTestRule.onAllNodesWithTag("clear-and-retry").assertCountEquals(0)
    }

    @Test
    fun correctCodeShowsVerifiedState() {
        composeTestRule.setContent {
            OtpScreen(onBack = {}, onVerified = {}, viewModel = OtpViewModel(SavedStateHandle(), SessionState()))
        }

        enterCode("123456")
        composeTestRule.onNodeWithText("Verified — redirecting…").assertIsDisplayed()
    }

    @Test
    fun resendIsDisabledImmediatelyAfterEntry() {
        composeTestRule.setContent {
            OtpScreen(onBack = {}, onVerified = {}, viewModel = OtpViewModel(SavedStateHandle(), SessionState()))
        }

        // Full 30s countdown re-enabling is covered by OtpViewModelTest's
        // virtual-time test. Not pinning the exact "0:30" text here — real
        // wall-clock time on a device can tick the countdown down by a
        // second or two before this assertion runs.
        composeTestRule.onNodeWithText("Resend code in 0:", substring = true)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}
