package utils

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.danielramzani.HealthCompose.BuildKonfig
import service.GenerativeAiService

suspend fun getGeminiSuggestions(results: List<String>, sleepRating: Int, moodRating: Int): List<String> {
    return withContext(Dispatchers.IO) {
        val GEMINI_API_KEY = BuildKonfig.GEMINI_API_KEY

        val instance = GenerativeAiService(
            visionModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = GEMINI_API_KEY,
            ),
            maxTokens = 20
        )

        // Mocked API call to Gemini
        val response = instance.getSuggestions(results + "Sleep Rating: $sleepRating" + "Mood Rating: $moodRating")
        response?.split(",")?.map { it.trim() } ?: emptyList()
    }
}