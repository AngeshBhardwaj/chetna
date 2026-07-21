package chetna.domains.consent.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GuardianTest : FunSpec({
    test("holds the fields it's constructed with") {
        val guardian = Guardian(id = "g-1", displayName = "Angesh")

        guardian.id shouldBe "g-1"
        guardian.displayName shouldBe "Angesh"
    }
})
