package com.barfet.gemmaassistant.ml

import android.content.Context
import android.os.SystemClock
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Service for interacting with the on-device LLM using MediaPipe's LlmInference API.
 */
class OnDeviceLlmService(private val context: Context) {

    private var llmInference: LlmInference? = null
    private var initializationJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    // Model configuration
    private val modelName = "gemma3-1b-it-int4.tflite"
    private val maxTokens = 1024
    private val topK = 40
    private val temperature = 0.8f
    private val randomSeed = 101

    // Initialization state
    @Volatile private var isInitialized = false
    @Volatile private var isInitializing = false
    private val initializationListeners = mutableListOf<() -> Unit>()
    private var initializationError: Exception? = null

    // Response generation state and callbacks
    private var currentProgressListener: ((String) -> Unit)? = null
    private var currentCompletionListener: (() -> Unit)? = null
    private var currentErrorListener: ((Exception) -> Unit)? = null
    private val responseLock = ReentrantLock()
    private val isGenerating = AtomicBoolean(false)
    private var lastTokenTime: Long = 0

    /**
     * Initializes the LLM Inference engine asynchronously.
     */
    fun initialize() {
        Timber.d("OnDeviceLlmService.initialize() called.")
        if (isInitialized || isInitializing) {
            Timber.d("Initialization already complete or in progress.")
            return
        }

        isInitializing = true
        initializationJob = serviceScope.launch {
            try {
                Timber.d("Starting LlmInference initialization...")
                
                // List assets to verify model file accessibility
                try {
                    val assets = context.assets.list("")
                    Timber.d("Asset directory contents: ${assets?.joinToString(", ") ?: "empty or null"}")
                    
                    // Check if model file exists
                    val modelExists = assets?.contains(modelName) ?: false
                    Timber.d("Model file '$modelName' exists in assets: $modelExists")
                    
                    if (!modelExists) {
                        throw RuntimeException("Model file $modelName not found in assets directory")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error listing assets directory")
                }

                val options = LlmInferenceOptions.builder()
                    .setModelPath(modelName)
                    .setMaxTokens(maxTokens)
                    .setTopK(topK)
                    .setTemperature(temperature)
                    .setRandomSeed(randomSeed)
                    .setResultListener { partialResultString: String?, done: Boolean ->
                        handlePartialResult(partialResultString, done)
                    }
                    .setErrorListener { error: RuntimeException? ->
                        val errorMessage = error?.message ?: "Unknown MediaPipe Error"
                        Timber.e(error, "MediaPipe setErrorListener triggered: $errorMessage")
                        handleError(error ?: RuntimeException(errorMessage))
                    }
                    .build()

                Timber.d("LlmInferenceOptions built. Calling createFromOptions...")
                
                try {
                    Timber.i("Attempting to call LlmInference.createFromOptions...")
                    llmInference = LlmInference.createFromOptions(context, options)
                    Timber.i("LlmInference.createFromOptions call completed. Instance created: ${llmInference != null}")

                    isInitialized = true
                    isInitializing = false
                    initializationError = null
                    Timber.d("LlmInference initialized successfully.")
                } catch (e: Throwable) { // Catch Throwable to capture more error types, including native ones if possible
                    Timber.e(e, "Exception during LlmInference.createFromOptions call: ${e.message}")
                    val wrappedError = RuntimeException("Failed to create LlmInference instance: ${e.message}", e)
                    isInitialized = false
                    isInitializing = false
                    initializationError = wrappedError
                    Timber.d("LlmInference initialization FAILED.")
                }
                
                notifyInitializationListeners()
            } catch (e: Exception) {
                Timber.e(e, "Exception during LlmInference initialization block")
                isInitialized = false
                isInitializing = false
                initializationError = e
                notifyInitializationListeners()
                handleError(e)
            }
        }
    }

    private fun handlePartialResult(partialResultString: String?, done: Boolean) {
        responseLock.withLock {
            if (!isGenerating.get() && !done) {
                Timber.w("Received partial result but not in generating state. Ignoring.")
                return@withLock
            }

            partialResultString?.let { token ->
                if (token.isNotEmpty()) {
                    lastTokenTime = SystemClock.uptimeMillis()
                    Timber.v("Token received: $token")
                    serviceScope.launch(Dispatchers.Main) {
                        currentProgressListener?.invoke(token)
                    }
                }
            } ?: Timber.w("Received null partial result string")

            if (done) {
                Timber.d("Response generation complete (marked as done).")
                val completionCallback = currentCompletionListener
                resetResponseState()
                serviceScope.launch(Dispatchers.Main) {
                    completionCallback?.invoke()
                }
            }
        }
    }

    private fun handleError(error: Exception) {
        responseLock.withLock {
            if (!isGenerating.get()) {
                Timber.w("Received error but not in generating state. Error likely during init or already handled: ${error.message}")
                return@withLock
            }
            Timber.e(error, "Error during response generation or initialization")
            val errorCallback = currentErrorListener
            resetResponseState()
            serviceScope.launch(Dispatchers.Main) {
                errorCallback?.invoke(error)
            }
        }
    }

    private fun resetResponseState() {
        currentProgressListener = null
        currentCompletionListener = null
        currentErrorListener = null
        isGenerating.set(false)
    }

    private fun notifyInitializationListeners() {
        serviceScope.launch(Dispatchers.Main) {
            initializationListeners.forEach { it() }
            initializationListeners.clear()
        }
    }

    fun addInitializationListener(listener: () -> Unit) {
        if (isInitialized || initializationError != null) {
            serviceScope.launch(Dispatchers.Main) { listener() }
        } else {
            initializationListeners.add(listener)
        }
    }

    /**
     * Generates a response based on the prompt using callbacks for streaming.
     */
    fun generateResponse(
        prompt: String,
        onTokenGenerated: (String) -> Unit,
        onCompletion: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (!isInitialized) {
            val errorMsg = "LlmInference not initialized."
            Timber.e(errorMsg)
            // Generate a fallback response if LLM is not initialized
            val fallbackResponse = "I'm sorry, but the AI model is not available at the moment. " +
                "Please check your device compatibility or try again later."
            serviceScope.launch(Dispatchers.Main) { 
                onTokenGenerated(fallbackResponse)
                onCompletion()
            }
            return
        }

        if (!isGenerating.compareAndSet(false, true)) {
            val error = IllegalStateException("Already processing a request.")
            Timber.w(error.message)
            serviceScope.launch(Dispatchers.Main) { onError(error) }
            return
        }

        responseLock.withLock {
            currentProgressListener = onTokenGenerated
            currentCompletionListener = onCompletion
            currentErrorListener = onError
        }

        Timber.d("Generating response for prompt: $prompt")
        lastTokenTime = SystemClock.uptimeMillis()

        try {
            llmInference?.generateResponseAsync(prompt)
        } catch (e: Exception) {
            Timber.e(e, "Exception calling generateResponseAsync")
            serviceScope.launch(Dispatchers.Main) { handleError(e) }
        }
    }

    fun isReady(): Boolean = isInitialized

    fun getInitializationError(): Exception? = initializationError

    fun shutdown() {
        Timber.d("Shutting down LlmInference...")
        serviceScope.launch {
            initializationJob?.cancel()
            llmInference?.close()
            llmInference = null
            isInitialized = false
            isInitializing = false
            resetResponseState()
            Timber.d("LlmInference shut down.")
        }
    }
} 