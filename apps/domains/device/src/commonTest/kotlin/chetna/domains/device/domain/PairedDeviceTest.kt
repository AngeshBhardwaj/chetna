package chetna.domains.device.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PairedDeviceTest : FunSpec({
    test("holds the fields it's constructed with") {
        val device = PairedDevice(deviceId = "d-1", familyId = "f-1")

        device.deviceId shouldBe "d-1"
        device.familyId shouldBe "f-1"
    }
})
