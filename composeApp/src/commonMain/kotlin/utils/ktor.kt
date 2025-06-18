package utils

import components.MoodRating
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

suspend fun sendMoodRatingToGeminiChat(moodRating: MoodRating): Boolean {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    return withContext(Dispatchers.IO) {
        val response = client.post("https://api.gemini.chat/mood-ratings") {
            contentType(ContentType.Application.Json)
            setBody(moodRating)
        }

        response.status == HttpStatusCode.OK
    }
}

suspend fun fetchSuggestionsFromGemini(): List<String> {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    return withContext(Dispatchers.IO) {
        val response = client.get("https://api.gemini.chat/suggestions") {
            contentType(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            emptyList()
        }
    }
}