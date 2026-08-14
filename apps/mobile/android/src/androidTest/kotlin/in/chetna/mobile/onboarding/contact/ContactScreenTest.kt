package `in`.chetna.mobile.onboarding.contact

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import org.junit.Rule
import org.junit.Test

// Constructs ContactDetailsViewModel directly (SavedStateHandle(), no Hilt
// context needed) so this stays a plain Compose UI test — same rationale as
// WelcomeScreenTest. Execution deferred to Section 10 (no emulator yet).
class ContactScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun smsCannotBeSelectedAndContinueGatingWorks() {
        var continuedWith: String? = null
        composeTestRule.setContent {
            ContactScreen(
                onBack = {},
                onContinue = { continuedWith = it },
                viewModel = ContactDetailsViewModel(SavedStateHandle()),
            )
        }

        // SMS is disabled — tapping it must not change selection or crash.
        composeTestRule.onNodeWithText("Mobile number (SMS)").performClick()
        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Email address").performTextInput("guardian@example.com")
        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()

        composeTestRule.onNodeWithText("I agree to receive a verification code at this email address.")
            .performClick()
        composeTestRule.onNodeWithText("Continue").assertIsEnabled()

        composeTestRule.onNodeWithText("Continue").performClick()
        assert(continuedWith == "guardian@example.com") {
            "Expected onContinue to carry the entered email, got $continuedWith"
        }
    }

    @Test
    fun backInvokesCallback() {
        var backCalled = false
        composeTestRule.setContent {
            ContactScreen(
                onBack = { backCalled = true },
                onContinue = {},
                viewModel = ContactDetailsViewModel(SavedStateHandle()),
            )
        }

        composeTestRule.onNodeWithText("How should we reach you?").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backCalled) { "Expected onBack to be invoked" }
    }
}
