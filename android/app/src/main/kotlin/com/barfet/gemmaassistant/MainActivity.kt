package com.barfet.gemmaassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.barfet.gemmaassistant.ui.ChatScreen
import com.barfet.gemmaassistant.ui.theme.GemmaAssistantTheme
import com.barfet.gemmaassistant.viewmodel.ChatViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created")
        
        setContent {
            GemmaAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create ChatViewModel
                    val chatViewModel: ChatViewModel = viewModel()
                    
                    // Main chat screen
                    ChatScreen(
                        viewModel = chatViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
