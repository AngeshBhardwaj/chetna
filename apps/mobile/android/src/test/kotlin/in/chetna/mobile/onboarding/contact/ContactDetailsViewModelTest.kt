package `in`.chetna.mobile.onboarding.contact

import androidx.lifecycle.SavedStateHandle
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ContactDetailsViewModelTest : FunSpec({

    fun newViewModel(initialEmail: String = "") =
        ContactDetailsViewModel(SavedStateHandle(mapOf("email" to initialEmail)))

    test("invalid email keeps Continue disabled regardless of consent") {
        val viewModel = newViewModel()
        viewModel.onEmailChanged("not-an-email")
        viewModel.onConsentChanged(true)

        viewModel.uiState.value.isContinueEnabled shouldBe false
    }

    test("valid email without consent keeps Continue disabled") {
        val viewModel = newViewModel()
        viewModel.onEmailChanged("guardian@example.com")

        viewModel.uiState.value.isContinueEnabled shouldBe false
    }

    test("valid email with consent enables Continue") {
        val viewModel = newViewModel()
        viewModel.onEmailChanged("guardian@example.com")
        viewModel.onConsentChanged(true)

        viewModel.uiState.value.isContinueEnabled shouldBe true
    }

    test("SMS selection attempts are ignored, Email stays selected") {
        val viewModel = newViewModel()
        viewModel.onSelectMethod(ContactMethod.SMS)

        viewModel.uiState.value.selectedMethod shouldBe ContactMethod.EMAIL
    }

    test("initial email from the nav argument is retained on construction") {
        val viewModel = newViewModel(initialEmail = "guardian@example.com")

        viewModel.uiState.value.email shouldBe "guardian@example.com"
    }
})
