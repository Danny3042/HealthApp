package repository

import io.ktor.utils.io.errors.IOException
import model.Status
import network.GeminiService

class GeminiRepositoryImpl : GeminiRepository {

    private val geminiService = GeminiService()

    override suspend fun generate(prompt: String, images: List<ByteArray>): Status {
        return try {
            val response = geminiService.generateContent(prompt)

            val status = response.error?.let {
                Status.Error(it.message)
            } ?: response.getText()?.let {
                Status.Success(it)
            } ?: Status.Error("An error occurred, please retry.")

            status

        } catch (e: IOException) {
            Status.Error("Unable to connect to the server. Please check your internet connection and try again.")
        } catch (e: Exception) {
            Status.Error("An error occurred, please retry.")
        }
    }

    override fun getApiKey(): String {
        return geminiService.getApiKey()
    }

    override fun setApiKey(key: String) {
        geminiService.setApiKey(key)
    }


}