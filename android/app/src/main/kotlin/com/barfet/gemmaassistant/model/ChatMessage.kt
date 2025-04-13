package com.barfet.gemmaassistant.model

import java.util.Date

/**
 * Represents a message in the chat conversation.
 */
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)

/**
 * Enum to represent different states of a message
 */
enum class MessageStatus {
    SENDING,
    SENT,
    RECEIVING,
    RECEIVED,
    ERROR
} 