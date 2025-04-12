package com.barfet.gemmaassistant

import android.app.Application
import timber.log.Timber

class GemmaAssistantApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
        
        Timber.i("GemmaAssistantApp initialized")
    }
} 