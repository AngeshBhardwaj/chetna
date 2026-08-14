package `in`.chetna.mobile.onboarding.otp

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
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

// Needs a real Activity + Hilt (unlike OtpScreenTest) — this specifically
// tests that the Hilt-provided ViewModel survives a configuration change
// (androidx.lifecycle.SavedStateHandle scoping to the nav back-stack entry,
// not the Activity), which requires the full graph. Execution deferred to
// Section 10 (no emulator yet).
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OtpConfigChangeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun partiallyEnteredDigitsSurviveConfigurationChange() {
        composeTestRule.onNodeWithText("Get started").performClick()
        composeTestRule.onNodeWithText("Email address").performTextInput("guardian@example.com")
        composeTestRule.onNodeWithText("I agree to receive a verification code at this email address.")
            .performClick()
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithTag("otp-digit-0").performTextInput("1")
        composeTestRule.onNodeWithTag("otp-digit-1").performTextInput("2")

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("otp-digit-0").assertTextContains("1")
        composeTestRule.onNodeWithTag("otp-digit-1").assertTextContains("2")
    }
}
