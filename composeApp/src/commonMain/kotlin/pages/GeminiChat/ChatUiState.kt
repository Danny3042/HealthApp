package pages.GeminiChat

import apikey
import model.Message
import model.Status

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val status: Status = Status.Idle,
    val apiKey: String = apikey
)