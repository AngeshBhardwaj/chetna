package `in`.chetna.mobile.navigation

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import `in`.chetna.mobile.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChetnaNavHostTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun completeContactDetails(email: String = "guardian@example.com") {
        composeTestRule.onNodeWithText("Get started").performClick()
        composeTestRule.onNodeWithText("Email address").performTextInput(email)
        composeTestRule.onNodeWithText("I agree to receive a verification code at this email address.")
            .performClick()
        composeTestRule.onNodeWithText("Continue").performClick()
    }

    @Test
    fun startDestinationIsWelcome() {
        composeTestRule.onNodeWithText("Real control over screen time, not just tracking").assertExists()
    }

    @Test
    fun getStartedNavigatesToContactDetails() {
        composeTestRule.onNodeWithText("Get started").performClick()
        composeTestRule.onNodeWithText("How should we reach you?").assertExists()
    }

    @Test
    fun contactToOtpNavigatesForward() {
        completeContactDetails()
        composeTestRule.onNodeWithText("Enter the code").assertExists()
    }

    @Test
    fun systemBackOnWelcomeExitsActivity() {
        // Not Espresso.pressBackUnconditionally() — flaky window-focus requirement here.
        composeTestRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assert(composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED) {
            "Expected the activity to be destroyed on system back from Welcome"
        }
    }

    @Test
    fun reachingSignedInClearsContactAndOtpFromBackStack() {
        completeContactDetails()
        "123456".forEachIndexed { index, digit ->
            composeTestRule.onNodeWithTag("otp-digit-$index").performTextInput(digit.toString())
        }
        // OtpViewModel navigates to Signed-in 800ms after the correct code
        // is entered — wait for that transition rather than asserting immediately.
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule
                .onAllNodesWithText("You're signed in", useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("You're signed in", useUnmergedTree = true).assertExists()

        composeTestRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        // Back from Signed-in should land on Welcome, not a popped Contact/OTP
        // screen (design.md's popUpTo(Welcome) decision).
        composeTestRule.onNodeWithText("Real control over screen time, not just tracking").assertExists()
    }
}
