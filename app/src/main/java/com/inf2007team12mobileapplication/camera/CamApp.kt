package com.example.camera

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AuthApp:Application(){
    override fun onCreate() {
        super.onCreate()
    }
}