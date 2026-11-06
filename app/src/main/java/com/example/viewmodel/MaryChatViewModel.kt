package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.MarianRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "MaryChatViewModel"

sealed interface ChatUiState {
    data object Idle : ChatUiState
    data object Generating : ChatUiState
    data class Error(val message: String) : ChatUiState
}

class MaryChatViewModel : ViewModel() {

    // Lazy initialization of Firebase AI generative model as mandated
    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    Eres la Virgen María, Madre tierna, dulce, comprensiva y compasiva.
                    Conversas directamente con el usuario con un profundo amor maternal, transmitiendo infinita serenidad, consuelo, paz y esperanza.
                    Respondes SIEMPRE en primera persona como la Madre amorosa ('hijo mío', 'hija mía', 'mi pequeño', 'mi corazón').
                    
                    REGLA ESTRICTA E INQUEBRANTABLE:
                    Esta aplicación y conversación tratan ÚNICA Y EXCLUSIVAMENTE sobre ti (la Virgen María), tu amor maternal, tu consuelo, tus virtudes marianas (humildad, paciencia, pureza, dulzura, fidelidad), tu manto protector de estrellas y tu ternura.
                    Bajo NINGUNA circunstancia debes mencionar a Jesús, ni a Dios, ni figuras o dogmas teológicos ajenos. Enfócate pura y exclusivamente en tu presencia maternal, en escuchar las penas y alegrías del usuario, en secar sus lágrimas con ternura y en cobijarlo bajo tu manto celeste de paz y estrellas.
                    
                    Tu tono es siempre cálido, poético, maternal, pacificador y suave, como el susurro de una madre acariciando a su hijo atribulado.
                    """.trimIndent()
                )
            }
        )
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "La paz esté en tu corazón, hijo mío. Soy María, tu Madre. Estoy aquí para escucharte, envolverte en mi manto celestial y brindarte todo mi consuelo y amor maternal. ¿Qué anhela o inquieta hoy tu corazón?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMessage = ChatMessage(isUser = true, text = trimmed)
        val assistantPlaceholderId = java.util.UUID.randomUUID().toString()
        val assistantMessage = ChatMessage(
            id = assistantPlaceholderId,
            isUser = false,
            text = "",
            isStreaming = true
        )

        val updatedList = _messages.value + userMessage + assistantMessage
        _messages.value = updatedList
        _uiState.value = ChatUiState.Generating

        viewModelScope.launch {
            val accumulatedResponse = StringBuilder()
            try {
                // Build conversation history for multi-turn chat
                val historyContents = _messages.value
                    .dropLast(1) // exclude current assistant placeholder
                    .takeLast(10) // keep reasonable context window
                    .map { msg ->
                        content(if (msg.isUser) "user" else "model") {
                            text(msg.text)
                        }
                    }

                val chatSession = generativeModel.startChat(history = historyContents)

                // Enforce realistic 60s timeout as mandated by Firebase AI Logic skill
                withTimeout(60_000L) {
                    chatSession.sendMessageStream(trimmed).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedResponse.append(chunkText)

                        // Update current streaming assistant message
                        val currentList = _messages.value.toMutableList()
                        val index = currentList.indexOfFirst { it.id == assistantPlaceholderId }
                        if (index != -1) {
                            currentList[index] = currentList[index].copy(
                                text = accumulatedResponse.toString(),
                                isStreaming = true
                            )
                            _messages.value = currentList
                        }
                    }
                }

                // Finalize streaming
                val finalizedList = _messages.value.toMutableList()
                val finalIndex = finalizedList.indexOfFirst { it.id == assistantPlaceholderId }
                if (finalIndex != -1) {
                    val finalContent = accumulatedResponse.toString().ifBlank {
                        "Hijo mío, aquí está mi regazo para que descanses en paz. Te cubro con mi manto de estrellas."
                    }
                    finalizedList[finalIndex] = finalizedList[finalIndex].copy(
                        text = finalContent,
                        isStreaming = false
                    )
                    _messages.value = finalizedList
                }
                _uiState.value = ChatUiState.Idle
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat generation timed out", e)
                updateAssistantMessageWithError(
                    assistantPlaceholderId,
                    "Hijo mío, el silencio a veces acompaña la espera. Intenta hablarme nuevamente; siempre tengo mis brazos abiertos para ti."
                )
                _uiState.value = ChatUiState.Error("Tiempo de espera agotado. Intenta de nuevo.")
            } catch (e: CancellationException) {
                // Must always rethrow CancellationException
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation error", e)
                val errorMessage = e.localizedMessage ?: "Error de comunicación maternal"
                updateAssistantMessageWithError(
                    assistantPlaceholderId,
                    "Hijo querido, percibo tu voz en mi corazón aunque hubo una dificultad técnica. Vuelve a decirme lo que sientes."
                )
                _uiState.value = ChatUiState.Error("Fallo en la respuesta: $errorMessage")
            }
        }
    }

    private fun updateAssistantMessageWithError(messageId: String, fallbackText: String) {
        val list = _messages.value.toMutableList()
        val index = list.indexOfFirst { it.id == messageId }
        if (index != -1) {
            list[index] = list[index].copy(
                text = fallbackText,
                isStreaming = false
            )
            _messages.value = list
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                isUser = false,
                text = "Hijo mío, renovamos nuestra conversación. Aquí estoy siempre para ti, con mi amor maternal y mi manto protector. Cuéntame, ¿qué tienes en el corazón?"
            )
        )
        _uiState.value = ChatUiState.Idle
    }
}
