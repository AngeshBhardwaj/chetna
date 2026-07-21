package chetna.backend.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StatusControllerTest : FunSpec({
    test("reports ok status") {
        val controller = StatusController()

        controller.status() shouldBe StatusResponse(status = "ok")
    }
})
