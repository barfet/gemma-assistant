package com.barfet.gemmaassistant.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import timber.log.Timber
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Helper class for TensorFlow Lite operations.
 */
class TFLiteHelper(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    
    /**
     * Initializes the TFLite interpreter with the given model.
     * @param modelName The name of the TFLite model file in assets.
     * @param useGpu Whether to use GPU acceleration if available.
     */
    fun initializeInterpreter(modelName: String, useGpu: Boolean = true) {
        try {
            val options = Interpreter.Options().apply {
                // Use GPU if available and requested
                if (useGpu) {
                    val compatList = CompatibilityList()
                    if (compatList.isDelegateSupportedOnThisDevice) {
                        gpuDelegate = GpuDelegate()
                        addDelegate(gpuDelegate)
                        Timber.i("Using GPU acceleration for TFLite")
                    } else {
                        Timber.i("GPU acceleration not available, using CPU")
                    }
                }
                
                // Set number of threads for CPU execution
                setNumThreads(4)
            }
            
            interpreter = Interpreter(loadModelFile(modelName), options)
            Timber.i("TFLite interpreter initialized successfully for model: $modelName")
        } catch (e: Exception) {
            Timber.e(e, "Error initializing TFLite interpreter")
        }
    }
    
    /**
     * Run inference on input tokens.
     * Note: This is a simplified test method and doesn't represent the actual
     * Gemma 3 model's input/output format, which requires proper tokenization.
     * 
     * @param inputTokens ByteBuffer containing tokenized input
     * @return Output text or null if inference failed
     */
    fun runInference(inputTokens: ByteBuffer): String? {
        try {
            // For testing purposes, we'll just create a dummy output
            // In a real implementation, we'd need to match the model's output tensor specs
            val outputBuffer = FloatBuffer.allocate(10)
            
            // Run inference
            interpreter?.run(inputTokens, outputBuffer)
            Timber.d("Inference completed successfully")
            
            // Process output (in a real implementation, we'd detokenize the output)
            // For now, just return a dummy string
            return "Model inference completed with test output"
        } catch (e: Exception) {
            Timber.e(e, "Error running inference")
            return null
        }
    }
    
    /**
     * Loads a TFLite model file from assets.
     * @param modelName The name of the TFLite model file in assets.
     * @return MappedByteBuffer containing the model.
     */
    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * Clean up resources when no longer needed.
     */
    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
        
        interpreter = null
        gpuDelegate = null
        
        Timber.i("TFLite resources released")
    }
} 