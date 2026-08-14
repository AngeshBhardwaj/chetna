package `in`.chetna.mobile.onboarding.signedin

import `in`.chetna.mobile.session.SessionState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SignedInViewModelTest : FunSpec({

    test("email reflects the session's verified email") {
        val sessionState = SessionState().apply { setVerifiedEmail("guardian@example.com") }
        val viewModel = SignedInViewModel(sessionState)

        viewModel.email.value shouldBe "guardian@example.com"
    }

    test("logout clears the held session state") {
        val sessionState = SessionState().apply { setVerifiedEmail("guardian@example.com") }
        val viewModel = SignedInViewModel(sessionState)

        viewModel.onLogout()

        sessionState.verifiedEmail.value shouldBe null
    }
})
