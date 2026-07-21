package chetna.backend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// Exercises the actual running backend (e.g. via docker compose), not an in-process test context.
// Not run as part of `check` — invoke explicitly with `./gradlew e2eTest` once the stack is up.
class StatusEndpointE2ETest : FunSpec({
    test("running backend reports ok status") {
        val baseUrl = System.getenv("BACKEND_BASE_URL") ?: "http://localhost:8080"
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder(URI.create("$baseUrl/api/v1/status")).GET().build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        response.body() shouldBe """{"status":"ok"}"""
    }
})
