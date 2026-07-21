package chetna.backend.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// Placeholder proving the REST/OpenAPI wiring works; replaced by real API endpoints.
@RestController
class StatusController {

    @GetMapping("/api/v1/status")
    fun status(): StatusResponse = StatusResponse(status = "ok")
}

data class StatusResponse(val status: String)
