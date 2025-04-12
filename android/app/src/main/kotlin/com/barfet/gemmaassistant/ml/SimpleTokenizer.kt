package com.barfet.gemmaassistant.ml

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * A very simplified tokenizer for testing purposes.
 * In a real implementation, we would use a proper BPE tokenizer.
 */
class SimpleTokenizer {
    
    companion object {
        // Sample token IDs for testing (completely made up)
        private const val START_TOKEN = 1
        private const val END_TOKEN = 2
        private const val UNKNOWN_TOKEN = 3
        
        // Simple word-to-token mapping (just for testing)
        private val SAMPLE_TOKENS = mapOf(
            "hello" to 100,
            "world" to 101,
            "gemma" to 102,
            "model" to 103,
            "test" to 104,
            "the" to 105,
            "is" to 106,
            "a" to 107,
            "this" to 108
        )
        
        // Maximum sequence length (adjust as needed)
        private const val MAX_SEQ_LENGTH = 32
    }
    
    /**
     * Converts a string to a token sequence for testing.
     */
    fun tokenize(text: String): List<Int> {
        // Simple space-based tokenization for testing
        val words = text.lowercase().split(Regex("[\\s.,;!?]+"))
        
        val tokens = mutableListOf<Int>()
        tokens.add(START_TOKEN)
        
        for (word in words) {
            if (word.isNotEmpty()) {
                val token = SAMPLE_TOKENS[word] ?: UNKNOWN_TOKEN
                tokens.add(token)
            }
        }
        
        tokens.add(END_TOKEN)
        return tokens
    }
    
    /**
     * Encodes a token sequence to a ByteBuffer for TFLite input.
     */
    fun encode(tokens: List<Int>, maxLength: Int = MAX_SEQ_LENGTH): ByteBuffer {
        // Create a ByteBuffer to hold the tokens
        val buffer = ByteBuffer.allocateDirect(maxLength * 4).order(ByteOrder.nativeOrder())
        
        // Add tokens to the buffer, padding if necessary
        val paddedTokens = tokens.take(maxLength).toMutableList()
        while (paddedTokens.size < maxLength) {
            paddedTokens.add(0) // Padding token
        }
        
        // Add the tokens to the buffer
        for (token in paddedTokens) {
            buffer.putInt(token)
        }
        
        // Rewind the buffer so it's ready to be read
        buffer.rewind()
        return buffer
    }
    
    /**
     * Convert token IDs back to a string (for testing).
     */
    fun detokenize(tokens: List<Int>): String {
        val reverseMap = SAMPLE_TOKENS.entries.associate { (k, v) -> v to k }
        
        val words = mutableListOf<String>()
        for (token in tokens) {
            if (token != START_TOKEN && token != END_TOKEN && token != 0) {
                val word = reverseMap[token] ?: "<unk>"
                words.add(word)
            }
        }
        
        return words.joinToString(" ")
    }
} 