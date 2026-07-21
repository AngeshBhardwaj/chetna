package chetna.domains.credits.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CreditBalanceTest : FunSpec({
    test("holds the fields it's constructed with") {
        val balance = CreditBalance(minutes = 30)

        balance.minutes shouldBe 30
    }
})
