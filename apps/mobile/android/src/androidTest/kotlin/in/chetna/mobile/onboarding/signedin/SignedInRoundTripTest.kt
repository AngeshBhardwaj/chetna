package `in`.chetna.mobile.onboarding.signedin

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import `in`.chetna.mobile.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Full round trip: Welcome -> Contact -> OTP -> Signed-in -> Logout ->
// Welcome again, with empty fields the second time (no leftover state).
// Execution deferred to Section 10 (no emulator yet).
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SignedInRoundTripTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun completeLoginFlow(email: String) {
        composeTestRule.onNodeWithText("Get started").performClick()
        composeTestRule.onNodeWithText("Email address").performTextInput(email)
        composeTestRule.onNodeWithText("I agree to receive a verification code at this email address.")
            .performClick()
        composeTestRule.onNodeWithText("Continue").performClick()
        "123456".forEachIndexed { index, digit ->
            composeTestRule.onNodeWithTag("otp-digit-$index").performTextInput(digit.toString())
        }
    }

    @Test
    fun logoutReturnsToWelcomeAndClearsStateForNextAttempt() {
        completeLoginFlow("guardian@example.com")

        // OtpViewModel navigates to Signed-in 800ms after the correct code
        // is entered (matching the prototype's verified-state delay) —
        // wait for that transition rather than asserting immediately.
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule
                .onAllNodesWithText("You're signed in", useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("You're signed in", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("guardian@example.com", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithText("Log out").performClick()
        composeTestRule.onNodeWithText("Real control over screen time, not just tracking").assertIsDisplayed()

        // Re-enter the flow — Contact details must be empty, not carrying
        // the previous attempt's email forward.
        composeTestRule.onNodeWithText("Get started").performClick()
        composeTestRule.onNodeWithText("Email address").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("guardian@example.com").assertCountEquals(0)
    }
}
