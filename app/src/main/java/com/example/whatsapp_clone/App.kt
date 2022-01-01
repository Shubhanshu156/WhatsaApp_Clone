package com.example.whatsapp_clone

import android.app.Application
import android.util.Log
import android.widget.Toast

class App: Application() {
    val TAG="TESTING"
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: app is started")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "onTerminate: app exit")
        Toast.makeText(this, "exit app", Toast.LENGTH_SHORT).show()
    }

}