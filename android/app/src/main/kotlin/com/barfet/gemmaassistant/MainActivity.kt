package com.barfet.gemmaassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.barfet.gemmaassistant.ml.SimpleTokenizer
import com.barfet.gemmaassistant.ml.TFLiteHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {
    // TFLite helper and tokenizer
    private lateinit var tfliteHelper: TFLiteHelper
    private val tokenizer = SimpleTokenizer()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created")
        
        // Initialize TFLite helper
        tfliteHelper = TFLiteHelper(this)
        
        setContent {
            GemmaAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InferenceTestScreen(
                        onRunInference = { text -> runInferenceTest(text) }
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            // Initialize the TFLite interpreter with the model
            tfliteHelper.initializeInterpreter("gemma3-1b-it-int4.tflite")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize TFLite interpreter")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        tfliteHelper.close()
    }
    
    /**
     * Run inference test with the provided text.
     */
    private fun runInferenceTest(text: String): String {
        Timber.d("Running inference test with text: $text")
        
        try {
            // Tokenize the input text
            val tokens = tokenizer.tokenize(text)
            Timber.d("Tokenized to: $tokens")
            
            // Encode tokens to ByteBuffer
            val inputBuffer = tokenizer.encode(tokens)
            
            // Run inference
            val result = tfliteHelper.runInference(inputBuffer)
            
            return result ?: "Inference failed"
        } catch (e: Exception) {
            Timber.e(e, "Error in inference test")
            return "Error: ${e.message}"
        }
    }
}

@Composable
fun GemmaAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

@Composable
fun InferenceTestScreen(onRunInference: (String) -> String) {
    var inputText by remember { mutableStateOf("Hello world. This is a test.") }
    var outputText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gemma 3 Model Test",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Test Input") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = {
                isProcessing = true
                coroutineScope.launch(Dispatchers.IO) {
                    val result = onRunInference(inputText)
                    outputText = result
                    isProcessing = false
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !isProcessing
        ) {
            Text("Run Inference Test")
        }
        
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        
        Text(
            text = "Result:",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = outputText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InferenceTestScreenPreview() {
    GemmaAssistantTheme {
        InferenceTestScreen(onRunInference = { "Model inference completed with test output" })
    }
} 