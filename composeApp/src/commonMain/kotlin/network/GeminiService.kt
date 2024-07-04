package network

import apikey
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val BASE_URL = "https://generativelanguage.googleapis.com"
const val TIMEOUT = 3000L

@OptIn(ExperimentalSerializationApi::class)
class GeminiService {

    //region setup
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                explicitNulls = false
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT
            requestTimeoutMillis = TIMEOUT
            socketTimeoutMillis = TIMEOUT
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    private var apiKey = apikey

    fun getApiKey(): String {
        return apiKey
    }

    fun setApiKey(Key: String) {
        apiKey = Key
    }

    suspend fun generateContent(prompt: String): Response {
        return makeApiRequest("$BASE_URL/v1beta/models/gemini-1.5-pro:generateContent?key=$apiKey") {
            addText(prompt)
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun makeApiRequest(url: String, requestBuilder: Request.RequestBuilder.() -> Unit): Response {
        val request = Request.RequestBuilder().apply(requestBuilder).build()

        val response: String = client.post(url) {
            body = Json.encodeToString(request)
        }.bodyAsText()

        return Json.decodeFromString(response)
    }
}