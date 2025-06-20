package service

import dev.shreyaspatil.ai.client.generativeai.Chat
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.Content
import org.danielramzani.HealthCompose.BuildKonfig

/**
 * Service for Generative AI operations that can interact with text as well as images.
 */
class GenerativeAiService(
     val visionModel: GenerativeModel,
     val maxTokens: Int
) {

    /**
     * Creates a chat instance which internally tracks the ongoing conversation with the model
     *
     * @param history History of conversation
     */
    fun startChat(history: List<Content>): Chat {
        return visionModel.startChat(history)
    }

    suspend fun getSuggestions(results: List<String>): String? {
        val response = visionModel.generateContent(
            prompt = "Based on the following results: ${results.joinToString(", ")}. Provide suggestions for the week."
        )
        return response.text
    }
    companion object {
        @Suppress("ktlint:standard:property-naming")
        var GEMINI_API_KEY = BuildKonfig.GEMINI_API_KEY

        val instance: GenerativeAiService by lazy {
            GenerativeAiService(
                visionModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = GEMINI_API_KEY,
                ),
                maxTokens = 200,
            )
        }
    }
}