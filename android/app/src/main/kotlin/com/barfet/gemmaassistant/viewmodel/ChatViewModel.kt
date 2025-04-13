package com.barfet.gemmaassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.barfet.gemmaassistant.model.ChatMessage
import com.barfet.gemmaassistant.ml.OnDeviceLlmService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for managing chat messages and interactions with the AI assistant.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    // List of messages in the conversation
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Input text state
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    // Loading state for when messages are being processed
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    // Error message state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Initialize service WITHOUT starting initialization immediately
    private val llmService = OnDeviceLlmService(application.applicationContext)

    // Current assistant message being built from streamed tokens
    private var currentAssistantMessageId: String? = null

    // Add init block
    init {
        Timber.d("ChatViewModel init block started.")
        // Call initialize explicitly here
        llmService.initialize()
        // Add listener after starting initialization
        llmService.addInitializationListener {
            Timber.d("ChatViewModel Initialization Listener triggered.")
            if (!llmService.isReady()) {
                val error = llmService.getInitializationError()
                val errorMsg = "Model initialization failed: ${error?.message ?: "Unknown error"}"
                _errorMessage.value = errorMsg
                Timber.e(error, errorMsg)
            } else {
                Timber.d("Model initialized successfully (reported to ViewModel).")
            }
        }
        Timber.d("ChatViewModel init block finished.")
    }

    /**
     * Update the input text as the user types.
     */
    fun updateInputText(text: String) {
        _inputText.value = text
        // Clear error when user starts typing
        if (text.isNotEmpty() && _errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    /**
     * Send a message to the AI assistant.
     * This adds the user message to the list and triggers the assistant's response.
     */
    fun sendMessage() {
        val userMessage = _inputText.value.trim()

        // Don't send empty messages or if processing
        if (userMessage.isEmpty() || _isProcessing.value) return

        // Check if service is initialized before sending
        if (!llmService.isReady()) {
            val error = llmService.getInitializationError()
            val errorMsg = "Model not ready. ${error?.message ?: "Waiting for initialization..."}"
            _errorMessage.value = errorMsg
            Timber.e(error, "Attempted to send message while LLM Service is not ready. Error: ${error?.message}")
            return
        }

        // Clear any previous errors
        _errorMessage.value = null

        // Add user message to the list
        addMessage(ChatMessage(
            content = userMessage,
            isFromUser = true
        ))

        // Clear input field
        _inputText.value = ""

        // Set processing state to true
        _isProcessing.value = true

        // Create an empty assistant message that will be updated as tokens are generated
        val assistantMessage = ChatMessage(
            content = "",
            isFromUser = false
        )
        currentAssistantMessageId = assistantMessage.id
        addMessage(assistantMessage)

        // Generate a response using the LLM service with callbacks
        viewModelScope.launch {
            try {
                llmService.generateResponse(
                    prompt = userMessage,
                    onTokenGenerated = { token ->
                        // Update the assistant message with each new token
                        // Ensure UI updates happen on the main thread if needed, though StateFlow updates are safe
                        updateAssistantMessage(token)
                    },
                    onCompletion = {
                        // Set processing state to false when generation is complete
                        _isProcessing.value = false
                        currentAssistantMessageId = null
                        Timber.d("Response generation completed successfully.")
                    },
                    onError = { error ->
                        Timber.e(error, "Error generating response")
                        val errorText = " [Error: ${error.message}]"
                        // Update the assistant message to show the error
                        updateAssistantMessage(errorText, true)
                        _isProcessing.value = false
                        currentAssistantMessageId = null
                        _errorMessage.value = "Response generation failed: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Catch synchronous errors from calling generateResponse itself (less likely now)
                Timber.e(e, "Exception launching response generation")
                updateAssistantMessage(" [Error: ${e.message}]", true)
                _isProcessing.value = false
                currentAssistantMessageId = null
                _errorMessage.value = "Failed to start generation: ${e.message}"
            }
        }
    }

    /**
     * Build a context string from the conversation history.
     * Note: This is currently not used by the LlmInference API but kept for potential future use
     * or alternative LLM integrations.
     */
    private fun buildConversationContext(): String {
        // For simplicity, we'll just include the last few messages
        val recentMessages = _messages.value.dropLast(1).takeLast(6)

        return buildString {
            for (message in recentMessages) {
                append(if (message.isFromUser) "User: " else "Assistant: ")
                append(message.content)
                append("\n")
            }
        }
    }

    /**
     * Update the current assistant message with a new token or replace its content.
     */
    private fun updateAssistantMessage(token: String, replace: Boolean = false) {
        val currentId = currentAssistantMessageId ?: return // Don't update if no message is active
        val currentMessages = _messages.value.toMutableList()
        val assistantMessageIndex = currentMessages.indexOfLast { it.id == currentId }

        if (assistantMessageIndex >= 0) {
            val assistantMessage = currentMessages[assistantMessageIndex]

            // Create an updated message
            val updatedMessage = if (replace) {
                assistantMessage.copy(content = token)
            } else {
                // Append token to existing content
                assistantMessage.copy(content = assistantMessage.content + token)
            }

            // Replace the old message with the updated one
            currentMessages[assistantMessageIndex] = updatedMessage
            _messages.value = currentMessages
        }
    }

    /**
     * Add a message to the conversation history.
     */
    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
        Timber.d("Added message: ${message.content}")
    }

    /**
     * Clear the error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        llmService.shutdown()
    }
} 