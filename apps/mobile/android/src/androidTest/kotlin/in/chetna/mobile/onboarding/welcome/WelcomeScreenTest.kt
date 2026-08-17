package `in`.chetna.mobile.onboarding.welcome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// WelcomeScreen holds no state of its own (see design.md's screen mapping),
// so this UI-layer test is its only coverage — no separate ViewModel unit
// test. Execution deferred to Section 10 (no emulator yet — see Section 3's
// tasks.md note).
@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun trustPointsRenderAndGetStartedNavigatesAway() {
        var navigated = false
        composeTestRule.setContent {
            WelcomeScreen(onGetStarted = { navigated = true })
        }

        composeTestRule.onNodeWithText("Blocks access in real time, not just logs it").assertIsDisplayed()
        composeTestRule.onNodeWithText("DPDP-aware. No behavioral profiling, ever.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Emergency calling always works, even when locked.").assertIsDisplayed()

        composeTestRule.onNodeWithText("Get started").performClick()
        assert(navigated) { "Expected onGetStarted to be invoked" }
    }
}
