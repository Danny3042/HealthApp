package utils

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.danielramzani.HealthCompose.BuildKonfig
import service.GenerativeAiService

suspend fun getGeminiSuggestions(results: List<String>): List<String> {
    return withContext(Dispatchers.IO) {
        val GEMINI_API_KEY = BuildKonfig.GEMINI_API_KEY

        val instance = GenerativeAiService(
            visionModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = GEMINI_API_KEY,
            )
        )

        // Mocked API call to Gemini
        val response = instance.getSuggestions(results)
        response.toString().split(",")
    }
}