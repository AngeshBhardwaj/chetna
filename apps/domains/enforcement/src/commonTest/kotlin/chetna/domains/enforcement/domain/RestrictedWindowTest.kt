package chetna.domains.enforcement.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RestrictedWindowTest : FunSpec({
    test("holds the fields it's constructed with") {
        val window = RestrictedWindow(startMinuteOfDay = 1200, endMinuteOfDay = 1440)

        window.startMinuteOfDay shouldBe 1200
        window.endMinuteOfDay shouldBe 1440
    }
})
