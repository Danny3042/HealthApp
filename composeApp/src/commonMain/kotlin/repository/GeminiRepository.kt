package repository

import model.Status

interface GeminiRepository {
    suspend fun generate(prompt: String, images: List<ByteArray> = emptyList()): Status

    fun getApiKey(): String

    fun setApiKey(key: String)
}
