package com.barfet.gemmaassistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.barfet.gemmaassistant.ui.theme.GemmaAssistantTheme
import com.barfet.gemmaassistant.model.ChatMessage
import com.barfet.gemmaassistant.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main chat screen composable function.
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    
    ChatScreenContent(
        messages = messages,
        inputText = inputText,
        isProcessing = isProcessing,
        onInputTextChange = viewModel::updateInputText,
        onSendClick = viewModel::sendMessage,
        modifier = modifier
    )
}

/**
 * Content of the chat screen.
 */
@Composable
fun ChatScreenContent(
    messages: List<ChatMessage>,
    inputText: String,
    isProcessing: Boolean,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat history list
        ChatMessageList(
            messages = messages,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input area
        ChatInputField(
            text = inputText,
            onTextChange = onInputTextChange,
            onSendClick = onSendClick,
            isProcessing = isProcessing,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * List of chat messages.
 */
@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    // Scroll to bottom when new messages are added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            ChatMessageItem(
                message = message,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Individual chat message item.
 */
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isFromUser = message.isFromUser
    
    Row(
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start,
        modifier = modifier
    ) {
        if (!isFromUser) {
            // Avatar for assistant
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.Top)
            ) {
                Text(
                    text = "G", // G for Gemma
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, false)
        ) {
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isFromUser) 16.dp else 4.dp,
                    topEnd = if (isFromUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            // Timestamp
            Text(
                text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
        
        if (isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            
            // Avatar for user
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .align(Alignment.Top)
            ) {
                Text(
                    text = "U", // U for User
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

/**
 * Input field for typing messages.
 */
@Composable
fun ChatInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            // Text input
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Type a message") },
                singleLine = false,
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            
            // Send button
            Box(contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onSendClick,
                    enabled = text.isNotEmpty() && !isProcessing,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Loading indicator when processing
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val previewMessages = listOf(
        ChatMessage(content = "Hello! How can I help you today?", isFromUser = false),
        ChatMessage(content = "I need help with my schedule", isFromUser = true),
        ChatMessage(content = "I'd be happy to help with your schedule. What would you like to know or do?", isFromUser = false)
    )
    
    GemmaAssistantTheme {
        ChatScreenContent(
            messages = previewMessages,
            inputText = "",
            isProcessing = false,
            onInputTextChange = {},
            onSendClick = {}
        )
    }
} 